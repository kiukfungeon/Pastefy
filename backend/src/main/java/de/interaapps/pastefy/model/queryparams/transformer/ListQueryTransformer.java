package de.interaapps.pastefy.model.queryparams.transformer;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.helper.AbstractDataHelper;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.queryparams.ListQueryParameters;
import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.query.QueryGroup;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ListQueryTransformer<K extends Model, T, E> {
    private final Repo<K> repo;
    private Class<E> elasticClass;
    private final String elasticIndexName;

    public ListQueryTransformer(Repo<K> repo, Class<E> elasticClass, String elasticIndexName) {
        this.repo = repo;
        this.elasticClass = elasticClass;
        this.elasticIndexName = elasticIndexName;
    }

    protected void applyORMOrder(ListQueryParameters params, Query<K> query, String field, boolean asc) {
        query.order(field, !asc);
    }

    public Query<K> buildORMQuery(ListQueryParameters params) {
        Query<K> query = repo.query();

        if (params.sort != null) {
            String[] sort = params.sort.split(",");
            for (String string : sort) {
                applyORMOrder(params, query, string.replace("+", ""), string.contains("+"));
            }
        } else {
            applyORMOrder(params, query, "createdAt", true);
        }

        if (params.search != null) {
            query.search(params.search);
        }
        if (params.filters != null) {
            query.and(q -> applyFiltersToQuery(q, new AbstractObject().set("$and", params.filters), false));
        }
        return query;
    }

    protected void ormFieldFilter(QueryGroup<K> query, String operator, String key, AbstractElement value, boolean isOrFilter) {
        if (operator.equals("$eq")) {
            if (isOrFilter) query.orWhere(key, value.string());
            else query.where(key, value.string());
