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
