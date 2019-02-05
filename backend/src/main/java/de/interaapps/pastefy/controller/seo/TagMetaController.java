package de.interaapps.pastefy.controller.seo;

import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.model.database.algorithm.TagListing;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.PathPrefix;
import org.javawebstack.http.router.router.annotation.params.Path;
import org.javawebstack.http.router.router.annotation.verbs.Get;

@PathPrefix("/tags")
public class TagMetaController extends HttpController {
    private static final int DESCRIPTION_MAX_LENGTH = 180;

    private final SeoRenderer seo = new SeoRenderer();

    @Get("/{tag}")
    public String tagMeta(Exchange exchange, @Path("tag") String tag) {
        return seo.render(exchange, () -> {
            if (!Pastefy.getInstance().publicPastesEnabled() || tag == null || tag.trim().isEmpty()) return null;

            TagListing tagListing = TagListing.getOrCreate(tag);
            String tagName = getTagName(tagListing);
            String title = tagName + " | Pastefy";
            String description = getDescription(tagListing, tagName);
            String path = "/tags/" + seo.pathSegment(tagListing.tag);

            SeoRenderer.SeoPage page = seo.page(path, title, description)
                    .content(getSeoContent(tagListing, tagName));

