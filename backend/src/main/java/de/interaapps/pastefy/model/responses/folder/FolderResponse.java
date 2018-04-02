package de.interaapps.pastefy.model.responses.folder;

import de.interaapps.pastefy.model.database.Folder;
import de.interaapps.pastefy.model.responses.paste.PasteResponse;

import java.util.List;
import java.util.stream.Collectors;

public class FolderResponse {
    public boolean exists = false;
    public String id;
    public String name;
    public String userId;
    public List<FolderResponse> children;
    public List<PasteResponse> pastes;
