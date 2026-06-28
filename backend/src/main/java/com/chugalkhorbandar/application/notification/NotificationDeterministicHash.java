package com.chugalkhorbandar.application.notification;

final class NotificationDeterministicHash {

    private NotificationDeterministicHash() {}

    static int hash(String left, String right, String salt) {
        String combined = (left == null ? "" : left) + "|" + (right == null ? "" : right) + "|" + (salt == null ? "" : salt);
        return Math.floorMod(combined.hashCode(), Integer.MAX_VALUE);
    }
}
