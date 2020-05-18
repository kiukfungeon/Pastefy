package de.interaapps.pastefy;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.google.gson.Gson;
import de.interaapps.pastefy.ai.PasteAI;
import de.interaapps.pastefy.ai.PasteAIInfoService;
import de.interaapps.pastefy.analytics.AnalyticsService;
import de.interaapps.pastefy.auth.*;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Strategy;
import de.interaapps.pastefy.auth.strategies.oauth2.providers.*;
import de.interaapps.pastefy.cli.PastefyCLI;
import de.interaapps.pastefy.controller.AppController;
import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.*;
import de.interaapps.pastefy.helper.elastic.ElasticMigrator;
import de.interaapps.pastefy.jobs.BackgroundJobService;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.plugins.PastefyBackendPlugin;
import de.interaapps.pastefy.model.plugins.PastefyPlugin;
import de.interaapps.pastefy.model.responses.ExceptionResponse;
import io.minio.MinioClient;
import io.undertow.util.FileUtils;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.javawebstack.http.router.HTTPRouter;
import org.javawebstack.http.router.handler.RequestHandler;
import org.javawebstack.http.router.transformer.response.SerializedResponseTransformer;
import org.javawebstack.http.router.undertow.UndertowHTTPSocketServer;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.connection.MySQL;
import org.javawebstack.orm.connection.SQL;
import org.javawebstack.orm.connection.SQLite;
import org.javawebstack.orm.connection.pool.MinMaxScaler;
import org.javawebstack.orm.connection.pool.SQLPool;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.mapper.AbstractDataTypeMapper;
import org.javawebstack.webutils.config.Config;
import org.javawebstack.webutils.config.EnvFile;
import org.javawebstack.webutils.config.Mapping;
import org.javawebstack.webutils.config.MappingTryout;
import org.javawebstack.webutils.middleware.CORSPolicy;
import org.javawebstack.webutils.middleware.MultipartPolicy;
import org.javawebstack.webutils.middleware.RateLimitMiddleware;
import picocli.CommandLine;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Pastefy {

    private static Pastefy instance;
    private JedisPool redisPool;

    public static Pastefy getInstance() {
        return instance;
    }

    private final Config config;
    private Passport passport;
    private OAuth2Strategy oAuth2Strategy;
    private final HTTPRouter httpRouter;
    private boolean loginRequired = false;
    private boolean loginRequiredForRead = false;
    private boolean loginRequiredForCreate = false;
    private boolean publicPastesEnabled = false;

    private ElasticsearchClient elasticsearchClient;
    private MinioClient minioClient;

    private PasteAI pasteAI;
    private BackgroundJobService backgroundJobService;
    private PasteAIInfoService pasteAIInfoService;
    private AnalyticsService analyticsService;

    private String homeHTML;

    private final Map<String, PastefyPlugin> plugins = new HashMap<>();

    public final String[] INDEXES = new String[]{"pastefy_pastes"};
    private ArrayList<String> availableIndexes = new ArrayList<>();

    private ExecutorService executor = Executors.newFixedThreadPool(30);

    public Pastefy() {
        config = new Config();

        int cpu = Runtime.getRuntime().availableProcessors();

        UndertowHTTPSocketServer undertowHTTPSocketServer = new UndertowHTTPSocketServer();

        undertowHTTPSocketServer.setMaxThreads(Math.max(config.getInt("http.server.min.threads", 8), config.getInt("http.server.min.maxthreads", cpu * config.getInt("http.server.min.maxthreadscpumultiplier", 4))));

        httpRouter = new HTTPRouter(undertowHTTPSocketServer);

        initPlugins();

        setupConfig();
        setupPassport();
        setupModels();
        setupElastic();
        setupMinio();
        setupRedis();
        analyticsService = AnalyticsService.create(this);

        pasteAI = PasteAI.create(this);
        backgroundJobService = new BackgroundJobService(this);
        pasteAIInfoService = new PasteAIInfoService(this, backgroundJobService);
    }

    private void setupRedis() {
        if (!config.has("redis.host")) return;

        String host = config.get("redis.host");
        int port = config.getInt("redis.port", 6379);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getInt("redis.pool.max", 128));
        poolConfig.setMaxIdle(config.getInt("redis.pool.maxidle", 64));
        poolConfig.setMinIdle(config.getInt("redis.pool.minidle", 16));
        poolConfig.setTestOnBorrow(config.get("redis.pool.textonborrow", "true").equalsIgnoreCase("true"));
        poolConfig.setTestWhileIdle(config.get("redis.pool.testwhileitdle", "true").equalsIgnoreCase("true"));
        poolConfig.setMinEvictableIdleTimeMillis(config.getInt("redis.pool.minevictableIdletimemillis", 60000));
        poolConfig.setTimeBetweenEvictionRunsMillis(config.getInt("redis.pool.timebetweenevictionrunsmillis", 30000));
        poolConfig.setNumTestsPerEvictionRun(-1);


        this.redisPool = new JedisPool(poolConfig, host, port, config.getInt("redis.pool.timeout", 1000));
    }

    private void setupMinio() {
        if (!config.has("minio.server") || !config.has("minio.access.key") || !config.has("minio.secret.key")) {
            return;
        }

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(config.getInt("minio.pool", 50), 2, TimeUnit.MINUTES))
                .connectTimeout(4, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        minioClient = MinioClient.builder()
            .region(config.get("minio.region"))
            .endpoint(config.get("minio.server"))
            .credentials(config.get("minio.access.key"), config.get("minio.secret.key"))
            .httpClient(httpClient)
            .build();
    }

    protected void setupConfig() {
        Mapping mapping = new Mapping()
                .map("HTTP_SERVER_MIN_THREADS", "http.server.min.threads")
                .map("HTTP_SERVER_MIN_MAXTHREADS", "http.server.min.maxthreads")
                .map("HTTP_SERVER_MIN_MAXTHREADS_CPU_MULTIPLIER", "http.server.min.maxthreadscpumultiplier")

                .map("OAUTH2_INTERAAPPS_CLIENT_ID", "oauth2.interaapps.id")
                .map("OAUTH2_INTERAAPPS_CLIENT_SECRET", "oauth2.interaapps.secret")
                .map("OAUTH2_TWITCH_CLIENT_ID", "oauth2.twitch.id")
                .map("OAUTH2_TWITCH_CLIENT_SECRET", "oauth2.twitch.secret")
                .map("OAUTH2_GITHUB_CLIENT_ID", "oauth2.github.id")
                .map("OAUTH2_GITHUB_CLIENT_SECRET", "oauth2.github.secret")
                .map("OAUTH2_GOOGLE_CLIENT_ID", "oauth2.google.id")
                .map("OAUTH2_GOOGLE_CLIENT_SECRET", "oauth2.google.secret")
                .map("OAUTH2_DISCORD_CLIENT_ID", "oauth2.discord.id")
                .map("OAUTH2_DISCORD_CLIENT_SECRET", "oauth2.discord.secret")

                .map("OAUTH2_OIDC_CLIENT_ID", "oauth2.oidc.id")
                .map("OAUTH2_OIDC_CLIENT_SECRET", "oauth2.oidc.secret")
                .map("OAUTH2_OIDC_AUTH_ENDPOINT", "oauth2.oidc.authendpoint")
                .map("OAUTH2_OIDC_TOKEN_ENDPOINT", "oauth2.oidc.tokenendpoint")
                .map("OAUTH2_OIDC_USERINFO_ENDPOINT", "oauth2.oidc.userinfoendpoint")


                .map("PASTEFY_GRANT_ACCESS_REQUIRED", "pastefy.grantaccessrequired")
                .map("PASTEFY_LOGIN_REQUIRED", "pastefy.loginrequired")
                .map("PASTEFY_LOGIN_REQUIRED_CREATE", "pastefy.loginrequired.create")
                .map("PASTEFY_LOGIN_REQUIRED_READ", "pastefy.loginrequired.read")
                .map("PASTEFY_LIST_PASTES", "pastefy.listpastes")
                .map("PASTEFY_PUBLIC_STATS", "pastefy.publicstats")
                .map("PASTEFY_PUBLIC_PASTES", "pastefy.publicpastes")
                .map("PASTEFY_META_TAGS", "pastefy.metatags")
                .map("PASTEFY_META_TAGS_PREVIEW_LENGTH", "pastefy.metatags.previewlength")
                .map("PASTEFY_CUSTOM_BODY", "pastefy.custombody")
                .map("PASTEFY_CUSTOM_HEADER", "pastefy.customheader")
                .map("PASTEFY_PAGINATION_PAGE_LIMIT", "pastefy.paginaton.pagelimit")
                .map("PASTEFY_DEV", "pastefy.dev")

                .map("PASTEFY_AUTOMIGRATE", "pastefy.automigrate")

                .map("ELASTICSEARCH_URL", "elasticsearch.url")
                 .map("ELASTICSEARCH_USER", "elasticsearch.user")
                .map("ELASTICSEARCH_PASSWORD", "elasticsearch.password")
                .map("ELASTICSEARCH_API_KEY", "elasticsearch.apikey")

                .map("REDIS_HOST", "redis.host")
                .map("REDIS_PORT", "redis.port")
                .map("REDIS_POOL_MAX", "redis.pool.max")
                .map("REDIS_POOL_MAX_IDLE", "redis.pool.maxidle")
                .map("REDIS_POOL_MIN_IDLE", "redis.pool.minidle")
                .map("REDIS_POOL_TEXT_ON_BORROW", "redis.pool.textonborrow")
                .map("REDIS_POOL_TEST_WHILE_IDLE", "redis.pool.testwhileitdle")
                .map("REDIS_POOL_TIME_BETWEEN_EVICTION_MILLIS", "redis.pool.timebetweenevictionrunsmillis")
                .map("REDIS_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS", "redis.pool.minevictableIdletimemillis")
                .map("REDIS_POOL_TIMEOUT", "redis.pool.timeout")

                .map("MINIO_SERVER", "minio.server")
                .map("MINIO_BUCKET", "minio.bucket")
                .map("MINIO_ACCESS_KEY", "minio.access.key")
                .map("MINIO_SECRET_KEY", "minio.secret.key")
                .map("MINIO_POOL", "minio.pool")

                .map("MINIO_PASTESIZE_THRESHOLD", "minio.pastesize.threshold")

                .map("AI_ANTHROPIC_TOKEN", "ai.antrophic.token")
                .map("AI_GOOGLE_TOKEN", "ai.google.token")
                .map("AI_PROVIDER", "ai.provider")
                .map("AI_MODEL", "ai.model")
                .map("AI_ENGAGEMENT_THRESHOLD", "ai.engagement.threshold")
                .map("AI_JOB_WORKERS", "ai.jobs.workers")
                .map("AI_JOB_POLL_INTERVAL_MILLIS", "ai.jobs.pollintervalmillis")
                .map("AI_JOB_SWEEPER_ENABLED", "ai.jobs.sweeperenabled")
                .map("AI_JOB_SWEEP_INTERVAL_MILLIS", "ai.jobs.sweepintervalmillis")
                .map("AI_JOB_LEASE_SECONDS", "ai.jobs.leaseseconds")
                .map("AI_JOB_MAX_ATTEMPTS", "ai.jobs.maxattempts")
                .map("AI_JOB_RETRY_DELAY_SECONDS", "ai.jobs.retrydelayseconds")

                .map("ANALYTICS_CLICKHOUSE_URL", "analytics.clickhouse.url")
                .map("ANALYTICS_CLICKHOUSE_DATABASE", "analytics.clickhouse.database")
                .map("ANALYTICS_CLICKHOUSE_TABLE", "analytics.clickhouse.table")
                .map("ANALYTICS_CLICKHOUSE_USER", "analytics.clickhouse.user")
                .map("ANALYTICS_CLICKHOUSE_PASSWORD", "analytics.clickhouse.password")
                .map("ANALYTICS_CLICKHOUSE_AUTOMIGRATE", "analytics.clickhouse.automigrate")
                .map("ANALYTICS_RETENTION_DAYS", "analytics.retentiondays")
                .map("ANALYTICS_BATCH_SIZE", "analytics.batchsize")
                .map("ANALYTICS_FLUSH_INTERVAL_MILLIS", "analytics.flushintervalmillis")
                .map("ANALYTICS_QUEUE_CAPACITY", "analytics.queuecapacity")
                .map("ANALYTICS_HTTP_CONNECT_TIMEOUT_MILLIS", "analytics.http.connecttimeoutmillis")
                .map("ANALYTICS_HTTP_REQUEST_TIMEOUT_MILLIS", "analytics.http.requesttimeoutmillis")
                .map("ANALYTICS_IP_HASH_SALT", "analytics.iphashsalt")
                .map("ANALYTICS_IP_SOURCE", "analytics.ipsource")
                .map("ANALYTICS_IP_HEADER", "analytics.ipheader")
                .map("ANALYTICS_GEOIP_MMDB_PATH", "analytics.geoip.mmdbpath")
                .map("ANALYTICS_TRACK_BOTS", "analytics.trackbots")

                .map("DATABASE_CUSTOMPARAMS_CACHE_PREP_STMTS", "database.customparams.cachePrepStmts")
                .map("DATABASE_CUSTOMPARAMS_PREP_STMT_CACHE_SIZE", "database.customparams.prepStmtCacheSize")
                .map("DATABASE_CUSTOMPARAMS_PREP_STMT_CACHE_SQL_LIMIT", "database.customparams.prepStmtCacheSqlLimit")
                .map("DATABASE_CUSTOMPARAMS_USE_SERVER_PREP_STMTS", "database.customparams.useServerPrepStmts")
                .map("DATABASE_CUSTOMPARAMS_CACHE_RESULT_SET_METADATA", "database.customparams.cacheResultSetMetadata")
                .map("DATABASE_CUSTOMPARAMS_CACHE_SERVER_CONFIGURATION", "database.customparams.cacheServerConfiguration")
                .map("DATABASE_CUSTOMPARAMS_MAINTAIN_TIME_STATS", "database.customparams.maintainTimeStats");

        getBackendPlugins().forEach(p -> p.customConfigMapping(mapping));

        config.add(new EnvFile(new File(".env")).withVariables(), new MappingTryout(mapping, Config::basicEnvMapping));

        loginRequired = config.get("pastefy.loginrequired", "false").toLowerCase(Locale.ROOT).equals("true");
        loginRequiredForCreate = loginRequired || config.get("pastefy.loginrequired.create", "false").toLowerCase(Locale.ROOT).equals("true");
        loginRequiredForRead = loginRequired || config.get("pastefy.loginrequired.read", "false").toLowerCase(Locale.ROOT).equals("true");
        publicPastesEnabled = config.get("pastefy.publicpastes", "true").toLowerCase(Locale.ROOT).equals("true");
    }

    protected void initPlugins() {
        // Go through plugins folder and register everything
        File pluginsFolder = new File("plugins");
        if (!pluginsFolder.exists() || !pluginsFolder.isDirectory())
            return;

        for (File file : pluginsFolder.listFiles()) {
            File pluginFolder = new File(file.getPath());
            if (!pluginsFolder.exists() || !pluginsFolder.isDirectory())
                continue;

            try {
                PastefyPlugin pastefyPlugin = new PastefyPlugin(pluginFolder.getPath());
                System.out.println("Registered plugin " + pastefyPlugin.config.name);
                plugins.put(pastefyPlugin.config.name, pastefyPlugin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setupModels() {
        try {
            Supplier<SQL> factory;
            String driverName = config.get("database.driver", "none");
            switch (driverName) {
                case "mysql": {
                    factory = () -> {
                        MySQL sql = new MySQL(
                                config.get("database.host", "localhost"),
                                config.getInt("database.port", 3306),
                                config.get("database.name", "app"),
                                config.get("database.user", "root"),
                                config.get("database.password", "")
                        );

                        if (config.getObject("database.customparams") != null) {
                            config.getObject("database.customparams").forEach((key, value) -> {
                                sql.setCustomParam(key, value.string());
                            });
                        }
                        return sql;
                    };


                    break;
                }
                case "sqlite": {
                    factory = () -> new SQLite(config.get("database.file", "sb.sqlite"));
                    break;
                }
                default: {
                    throw new ORMConfigurationException("Unknown database driver " + driverName);
                }
            }


            SQLPool sqlPool = new SQLPool(new MinMaxScaler(config.getInt("database.pool.min", 20), config.getInt("database.pool.max", 50)), factory);


            /*if (this.isDevMode()) sqlPool.addQueryLogger((query, parameters) -> {
                System.out.println(query + " | " + Arrays.toString(parameters));
            });*/

            Handler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            // Logger.getLogger("ORM").addHandler(handler);
            // Logger.getLogger("ORM").setLevel(Level.ALL);
            ORMConfig ormConfig = new ORMConfig()
                    .setTablePrefix("pastefy_")
                    .addTypeMapper(new AbstractDataTypeMapper());

            ORM.register(Paste.class.getPackage(), sqlPool, ormConfig);

            if (config.get("pastefy.automigrate", "true").equals("true")) {
                ORM.autoMigrate();
            }

        } catch (ORMConfigurationException e) {
            e.printStackTrace();
        }

        getBackendPlugins().forEach(PastefyBackendPlugin::setupModels);
    }

    protected void setupElastic() {
