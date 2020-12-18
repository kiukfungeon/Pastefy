package de.interaapps.pastefy.controller.stats;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.exceptions.PermissionsDeniedException;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.responses.app.StatsResponse;
import org.javawebstack.http.router.router.annotation.PathPrefix;
