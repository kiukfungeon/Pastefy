package de.interaapps.pastefy.helper;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractNull;

public class AbstractDataHelper {
    public static AbstractElement ifArrayConvertToArray(AbstractElement element) {
        if (element.isArray() || !element.isObject()) return element;

        for (String key : element.object().keys()) {
            if (!key.matches("^[0-9]+$")) return element;
        }
