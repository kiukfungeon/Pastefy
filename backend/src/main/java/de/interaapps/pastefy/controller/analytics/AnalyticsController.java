package de.interaapps.pastefy.controller.analytics;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.analytics.AnalyticsQuery;
import de.interaapps.pastefy.analytics.AnalyticsResponse;
import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.FeatureDisabledException;
import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.User;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Attrib;
import org.javawebstack.http.router.router.annotation.params.Path;
import org.javawebstack.http.router.router.annotation.verbs.Get;

import java.util.Objects;

@PathPrefix("/api/v2/analytics")
@With("auth")
public class AnalyticsController extends HttpController {
