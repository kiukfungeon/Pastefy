package de.interaapps.pastefy.ai;

import com.google.genai.Client;
import com.google.genai.types.*;
import org.javawebstack.webutils.config.Config;

public class GoogleGenAIProvider implements AIProvider {
    private static final String DEFAULT_MODEL = "gemini-2.5-flash-lite";
    private static final int MAX_TOKENS = 512;

