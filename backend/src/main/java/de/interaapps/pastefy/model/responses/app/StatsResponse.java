package de.interaapps.pastefy.model.responses.app;

import com.google.gson.annotations.SerializedName;
import de.interaapps.pastefy.Pastefy;
import de.interaapps.pastefy.model.database.Folder;
import de.interaapps.pastefy.model.database.Paste;
import de.interaapps.pastefy.model.database.User;
import de.interaapps.pastefy.model.database.algorithm.TagListing;
import org.javawebstack.orm.Repo;

import java.io.IOException;

public class StatsResponse {

    private int createdPastes;
    private int loggedInPastes;
    private int userCount;
    private int tagCount;
    private int folderCount;

