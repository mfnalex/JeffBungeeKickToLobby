package de.jeff_media.bungeecore.bungee.jefflib;

import java.util.regex.Pattern;

public class Validate {
    public static void inclusiveBetween(int start, int end, int value) {
        if (value < start || value > end ) {
            throw new IllegalArgumentException("Not in boundaries: " + start+", "+end+", " + value);
        }
    }

    public static void matchesPattern(String string, String regex) {
        if(!Pattern.matches(regex, string)) {
            throw new IllegalArgumentException("Does not match pattern: " + regex + ", " + string);
        }
    }
}
