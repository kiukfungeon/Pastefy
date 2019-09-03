package de.interaapps.pastefy.auth;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Profile;
import de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Strategy;
import de.interaapps.pastefy.model.database.AuthKey;
import de.interaapps.pastefy.model.database.User;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.orm.Repo;

public class OAuth2Callback implements OAuth2Strategy.HttpCallbackHandler {
    public Object handle(Exchange exchange, de.interaapps.pastefy.auth.strategies.oauth2.OAuth2Callback callback, String name) {
        User.AuthenticationProvider provider = de.interaapps.pastefy.model.database.User.AuthenticationProvider.getProviderByClass(callback.getProvider().getClass());
        OAuth2Profile profile = callback.getProfile();

        User user = Repo.get(User.class).where("authId", profile.getId()).where("authProvider", provider).first();

        if (user == null) {
            user = new de.interaapps.pastefy.model.database.User();
            int i = 1;
            final String uniqueName = profile.getName().replaceAll("[^a-zA-Z0-9]", "");
            user.uniqueName = uniqueName;
            while (Repo.get(de.interaapps.pastefy.model.database.User.class).where("uniqueName", user.uniqueName).first() != null) {
                user.uniqueName = uniqueName + i++;
            }
            user.authId = profile.getId();
            user.authProvider = provider;

            if (Pastefy.getInstance().getConfig().get("pastefy.grantaccessrequired", "false").equalsIgnoreCase("true"))
                user.type = User.Type.AWAITING_ACCESS;
        }

