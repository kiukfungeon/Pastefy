package de.interaapps.pastefy.ai;

import com.google.genai.Client;
import com.google.genai.types.*;
import org.javawebstack.webutils.config.Config;

public class GoogleGenAIProvider implements AIProvider {
    private static final String DEFAULT_MODEL = "gemini-2.5-flash-lite";
    private static final int MAX_TOKENS = 512;

    private final Client client;
    private final String model;

    public GoogleGenAIProvider(Config config) {
        client = Client.builder()
                .apiKey(config.get("ai.google.token"))
                .build();
        model = config.get("ai.model", DEFAULT_MODEL);
    }

    @Override
