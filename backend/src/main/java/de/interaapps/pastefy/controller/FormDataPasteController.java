package de.interaapps.pastefy.controller;

import com.google.gson.Gson;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.responses.paste.MultiPastesElement;
import org.javawebstack.abstractdata.AbstractObject;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Attrib;
import org.javawebstack.http.router.router.annotation.verbs.Post;
import org.javawebstack.http.router.util.MimeType;
import org.javawebstack.webutils.util.IO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormDataPasteController extends HttpController {

    @Post("/")
    @Post("/api/asciicasts")
    @With({"rate-limiter", "auth-login-required-create", "awaiting-access-check", "blocked-check"})
    public String createPaste(Exchange exchange, @Attrib("user") User user) throws IOException {
