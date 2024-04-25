package de.interaapps.pastefy.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import org.javawebstack.webutils.config.Config;

import java.util.stream.Collectors;

public class AnthropicAIProvider implements AIProvider {
    private static final String DEFAULT_MODEL = Model.CLAUDE_3_HAIKU_20240307.asString();
    private static final long MAX_TOKENS = 512L;

    private final AnthropicClient client;
