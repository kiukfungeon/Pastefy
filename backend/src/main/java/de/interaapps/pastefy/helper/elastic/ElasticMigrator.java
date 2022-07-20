package de.interaapps.pastefy.helper.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.util.ObjectBuilder;
import de.interaapps.pastefy.Pastefy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ElasticMigrator {
    private final Pastefy pastefy;
    private ElasticsearchClient client;
    public ElasticMigrator(Pastefy pastefy, ElasticsearchClient client) {
        this.pastefy = pastefy;
        this.client = client;
    }

    public Map<String, Map<String,  Function<Property.Builder, ObjectBuilder<Property>>>> builders = new HashMap<>();

    {
        Map<String,  Function<Property.Builder, ObjectBuilder<Property>>> pastes = new HashMap<>();

        pastes.put("key", p -> p.keyword(k -> k));
        pastes.put("title", p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k))));
        pastes.put("content", p -> p.text(t -> t));

        pastes.put("version", p -> p.integer(t -> t));
        pastes.put("starCount", p -> p.integer(k -> k));
        pastes.put("engagementScore", p -> p.integer(k -> k));

        pastes.put("userId", p -> p.keyword(t -> t));
        pastes.put("forkedFrom", p -> p.keyword(k -> k));
        pastes.put("visibility", p -> p.keyword(k -> k));
        pastes.put("folder", p -> p.keyword(k -> k));
        pastes.put("type", p -> p.keyword(k -> k));
        pastes.put("storageType", p -> p.keyword(k -> k));

        pastes.put("tags", p -> p.keyword(k -> k));
        pastes.put("starredBy", p -> p.keyword(k -> k));

        pastes.put("encrypted", p -> p.boolean_(k -> k));

        pastes.put("expireAt", p -> p.date(d -> d));
