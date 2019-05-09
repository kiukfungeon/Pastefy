package de.interaapps.pastefy.controller.pastes;

import de.interaapps.pastefy.controller.HttpController;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.User;
import io.undertow.util.FileUtils;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.text.StringEscapeUtils;
import org.javawebstack.http.router.Exchange;
import org.javawebstack.http.router.router.annotation.With;
import org.javawebstack.http.router.router.annotation.params.Attrib;
import org.javawebstack.http.router.router.annotation.params.Path;
import org.javawebstack.http.router.router.annotation.verbs.Get;
import org.javawebstack.http.router.util.MimeType;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class PasteThumbnailController extends HttpController {
    private String template = null;
