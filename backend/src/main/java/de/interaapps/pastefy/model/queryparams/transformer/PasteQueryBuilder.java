package de.interaapps.pastefy.model.queryparams.transformer;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.util.ObjectBuilder;
import de.interaapps.pastefy.helper.RequestHelper;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.PasteStar;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.database.algorithm.PublicPasteEngagement;
import de.interaapps.pastefy.model.elastic.ElasticPaste;
import de.interaapps.pastefy.model.queryparams.ListQueryParameters;
import de.interaapps.pastefy.model.queryparams.PasteQueryParameters;
import de.interaapps.pastefy.model.responses.paste.PasteResponse;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.query.Query;
import org.javawebstack.orm.query.QueryGroup;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PasteQueryBuilder extends ListQueryTransformer<Paste, PasteResponse, ElasticPaste> {
    public PasteQueryBuilder() {
        super(Repo.get(Paste.class), ElasticPaste.class, "pastefy_pastes");
    }

    @Override
    protected void applyORMOrder(ListQueryParameters params, Query<Paste> query, String field, boolean asc) {
        if (field.equals("engagementScore")) {

            Query<PublicPasteEngagement> publicPasteEngagements = Repo.get(PublicPasteEngagement.class)
                    .query()
                    .limit(params.pageLimit)
                    .offset(params.pageLimit * (params.page - 1))
                    .order("score", !asc)
                    .whereExists(Paste.class, q -> q
                            .where(Paste.class, "id", "=", PublicPasteEngagement.class, "pasteId")
                    );

            if (params.filters.has("createdAt")) {
                publicPasteEngagements.where("createdAt", ">", params
