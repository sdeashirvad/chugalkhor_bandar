package com.chugalkhorbandar.application.reporting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FriendlySenderParser {

    private static final Pattern FRIENDLY_PATTERN = Pattern.compile("^\\s*(.+?)\\s*<([^>]+)>\\s*$");

    private FriendlySenderParser() {}

    public static String parse(String rawFrom) {
        if (rawFrom == null || rawFrom.isBlank()) {
            return "";
        }
        Matcher matcher = FRIENDLY_PATTERN.matcher(rawFrom.trim());
        if (matcher.matches()) {
            String name = matcher.group(1).trim();
            String email = matcher.group(2).trim();
            return name + " <" + email + ">";
        }
        return rawFrom.trim();
    }
}
