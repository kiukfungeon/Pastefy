package de.interaapps.pastefy.analytics;

import org.javawebstack.http.router.Exchange;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AnalyticsQuery {
    private static final Set<String> FILTERS = Set.of(
            "paste_key", "paste_visibility", "paste_user_id", "visit_type",
            "country", "region", "city", "visitor_user_id", "browser", "device_type", "os",
            "referer_host", "acquisition", "is_bot"
    );

    public Instant from = Instant.now().minus(30, ChronoUnit.DAYS);
    public Instant to = Instant.now();
    public String interval = "day";
    public String groupBy = "country";
    public boolean includeSummary = true;
    public boolean includeBreakdown = true;
    public final Map<String, String> filters = new LinkedHashMap<>();

    public static AnalyticsQuery from(Exchange exchange) {
