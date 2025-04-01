package de.interaapps.pastefy.model.responses.paste;

import de.interaapps.pastefy.model.database.PasteComment;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.responses.user.PublicUserResponse;
import org.javawebstack.orm.Repo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PasteCommentResponse {
    public int id;
    public String content;
    public Integer parentId;
    public Integer lineFrom;
    public Integer lineTo;
    public String createdAt;
    public PublicUserResponse user;
    public List<PasteCommentResponse> replies = new ArrayList<>();

    public PasteCommentResponse(PasteComment comment, boolean fetchReplies) {
