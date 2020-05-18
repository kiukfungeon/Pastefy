package de.interaapps.pastefy.ai;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.algorithm.TagListing;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.webutils.config.Config;

import java.util.Locale;

public class PasteAI {
    private final AIProvider provider;

    public PasteAI(AIProvider provider) {
        this.provider = provider;
    }

    public static PasteAI create(Pastefy pastefy) {
        Config config = pastefy.getConfig();
        String providerName = config.get("ai.provider", "").trim().toLowerCase(Locale.ROOT);

        if (providerName.isEmpty()) {
            if (hasToken(config, "ai.antrophic.token")) {
                providerName = "anthropic";
            } else if (hasToken(config, "ai.google.token")) {
                providerName = "google";
            } else {
                return null;
            }
        }

        switch (providerName) {
            case "anthropic":
                requireToken(config, "ai.antrophic.token", "AI_ANTHROPIC_TOKEN");
                return new PasteAI(new AnthropicAIProvider(config));
            case "google":
                requireToken(config, "ai.google.token", "AI_GOOGLE_TOKEN");
                return new PasteAI(new GoogleGenAIProvider(config));
            default:
                throw new IllegalArgumentException("Unsupported AI provider: " + providerName);
        }
    }
