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

    private static void requireToken(Config config, String configKey, String environmentVariable) {
        if (!hasToken(config, configKey)) {
            throw new IllegalArgumentException(environmentVariable + " is required for the configured AI provider");
        }
    }

    private static boolean hasToken(Config config, String configKey) {
        return config.has(configKey) && !config.get(configKey, "").trim().isEmpty();
    }

    public AbstractObject generateInfo(Paste paste) {
        String contents = paste.getContent();
        if (contents.length() > 1000)
            contents = contents.substring(0, 1000);

        String response = provider.generate(
                """
                You are a metadata extraction service for Pastefy, a public paste/code sharing platform.

                Task:
                Analyze the paste title and content preview. Return useful metadata for search, discovery, moderation and SEO.

                Rules:
                - Return valid JSON only. No markdown. No explanation.
                - Do not invent facts that are not visible in the title or content.
                - Do not include secrets, tokens, passwords, API keys or private data in the description.
                - Description must be neutral and max 1300 characters.
                - Tags must be lowercase slugs using only a-z, 0-9 and hyphen.
                - Max 8 tags.
                - Always include exactly one language tag if a programming/config language is identifiable: lang-{language}.
                - Prefer broad, useful tags over spammy keyword tags.
                - suggested_filename is optional. Only set it if the current title is missing, generic or clearly worse.
                - dangerous is true only if the paste obviously contains harmful code, malware, credential theft, destructive commands, phishing, token stealing or exploit logic.
                - Examples, documentation, harmless snippets and toy code are not dangerous.

                Severity:
                1 = harmless
                5 = suspicious but not clearly harmful
                8 = likely harmful
                10 = severe malware/credential theft/destructive code

                JSON schema:
                {
