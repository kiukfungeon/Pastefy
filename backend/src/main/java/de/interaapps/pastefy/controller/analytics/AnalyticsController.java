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
