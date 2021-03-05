package de.interaapps.pastefy.controller.seo;

import de.interaapps.pastefy.Pastefy;
import org.apache.commons.text.StringEscapeUtils;
import org.javawebstack.http.router.Exchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeoRenderer {
    private static final Logger LOGGER = Logger.getLogger(SeoRenderer.class.getName());
    private static final String META_START_TAG = "<!--META_TAGS-->";
    private static final String META_END_TAG = "<!--META_TAGS_END-->";
    private static final String META_REPLACEMENT = "<!--META_TAGS_REPLACEMENT-->";
    private static final String TITLE_REPLACEMENT = "<!--TITLE_REPLACEMENT-->";
    private static final String SEO_CONTENT_REPLACEMENT = "<!--SEO_CONTENT_REPLACEMENT-->";
    private static final Pattern TITLE_TAG_PATTERN = Pattern.compile("(?is)<title\\b[^>]*>.*?</title>");
    private static final Pattern APP_MOUNT_PATTERN = Pattern.compile("(?is)(<div\\s+id=[\"']app[\"'][^>]*>)\\s*</div>");
    private static final Pattern HTTP_SCHEME_PATTERN = Pattern.compile("(?i)^https?://");
    private static final Pattern ANY_SCHEME_PATTERN = Pattern.compile("(?i)^[a-z][a-z0-9+.-]*://");

    private final String html;

    public SeoRenderer() {
        Pastefy pastefy = Pastefy.getInstance();
        if (pastefy != null && pastefy.getHomeHTML() != null) {
            String preparedHtml = prepareHtml(pastefy.getHomeHTML());
            if (preparedHtml != null) {
                html = preparedHtml;
                return;
            }
        }

        html = loadHtml();
    }

    public String render(Exchange exchange, Supplier<SeoPage> pageSupplier) {
        if (!isEnabled() || html == null) return null;

        SeoPage page = pageSupplier.get();
        if (page == null) return null;

        exchange.header("Content-Type", "text/html; charset=UTF-8");
        return html
                .replace(TITLE_REPLACEMENT, escapeHtml(page.title))
                .replace(META_REPLACEMENT, tagsToHTML(page))
                .replace(SEO_CONTENT_REPLACEMENT, page.content);
    }

    public SeoPage page(String canonicalPath, String title, String description) {
        return new SeoPage(absoluteUrl(canonicalPath), title, description);
    }

    public String absoluteUrl(String pathOrUrl) {
        if (pathOrUrl == null || pathOrUrl.trim().isEmpty()) return getBaseUrl();

        String value = pathOrUrl.trim();
        if (HTTP_SCHEME_PATTERN.matcher(value).find()) return value;
        return getBaseUrl() + (value.startsWith("/") ? value : "/" + value);
    }

    public String pathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public String truncate(String value, int maxLength) {
        int codePointCount = value.codePointCount(0, value.length());
        if (codePointCount <= maxLength) return value;

        int endIndex = value.offsetByCodePoints(0, maxLength - 3);
        return value.substring(0, endIndex) + "...";
    }

    public String truncateWithoutEllipsis(String value, int maxLength) {
        int codePointCount = value.codePointCount(0, value.length());
        if (codePointCount <= maxLength) return value;
        return value.substring(0, value.offsetByCodePoints(0, maxLength));
    }

    public String escapeHtml(String value) {
        return StringEscapeUtils.escapeHtml4(value == null ? "" : value);
    }

    private boolean isEnabled() {
        Pastefy pastefy = Pastefy.getInstance();
        return pastefy != null
                && pastefy.getConfig().get("pastefy.metatags", "false").equalsIgnoreCase("true");
    }

    private String loadHtml() {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            if (resourceStream == null) {
                LOGGER.warning("Unable to load static/index.html for SEO metadata");
                return null;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return prepareHtml(new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException exception) {
            LOGGER.log(Level.WARNING, "Unable to prepare SEO metadata HTML", exception);
            return null;
        }
    }

    private String prepareHtml(String sourceHtml) {
        int startMetaTags = sourceHtml.indexOf(META_START_TAG);
        int endMetaTags = sourceHtml.indexOf(META_END_TAG, startMetaTags + META_START_TAG.length());
        if (startMetaTags < 0 || endMetaTags < 0) {
            LOGGER.warning("Unable to find SEO metadata placeholders in static/index.html");
            return null;
        }

