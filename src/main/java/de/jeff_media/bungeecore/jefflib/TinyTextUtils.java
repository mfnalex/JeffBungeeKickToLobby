package de.jeff_media.bungeecore.jefflib;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Bungee version of JeffLib's TextUtils. Contains only part of it
 */
@UtilityClass
public class TinyTextUtils {

    private static final int MIN_BANNER_WIDTH = 30;
    private static final char BANNER_CHAR = '#';
    private static final String EMPTY = "";
    private static final String REGEX_HEX = "[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]";

    private static final String REGEX_HEX_GRADIENT = "<#([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F])>(.*?)<#/([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F])>";
    private static final Pattern PATTERN_HEX_GRADIENT = Pattern.compile(REGEX_HEX_GRADIENT);

    private static final String REGEX_AMPERSAND_HASH = "&#(" + REGEX_HEX + ")";
    private static final Pattern PATTERN_AMPERSAND_HASH = Pattern.compile(REGEX_AMPERSAND_HASH);
    private static final String REGEX_XML_LIKE_HASH = "<#(" + REGEX_HEX + ")>";
    private static final Pattern PATTERN_XML_LIKE_HASH = Pattern.compile(REGEX_XML_LIKE_HASH);

    private static String addAmpersandsToHex(final String hex) {
        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex-Codes must always have 6 letters.");
        }
        final char[] chars = hex.toCharArray();
        final StringBuilder sb = new StringBuilder("&x");
        for (final char aChar : chars) {
            sb.append("&").append(aChar);
        }
        return sb.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private static String replaceRegexWithGroup(final CharSequence text, final Pattern pattern, final int group, final Function<String, String> function) {
        final Matcher matcher = pattern.matcher(text);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, function.apply(matcher.group(group)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Replaces color codes using &amp;. Also supports hex colors using <pre>&amp;x&amp;r&amp;r&amp;g&amp;g&amp;b&amp;b</pre>, <pre>&amp;#rrggbb</pre> and <pre>&lt;#rrggbb></pre>,
     * and gradients using <pre>&lt;#rrggbb> &lt;#/rrggbb></pre>
     *
     * @param text Text to translate
     * @return Translated text
     */
    public static String color(String text) {
        //text = replaceRegexWithGroup(text, PATTERN_AMPERSAND_HASH, 1, TinyTextUtils::addAmpersandsToHex);
        text = text.replace("&&", "{ampersand}");
        text = replaceGradients(text);
        text = replaceRegexWithGroup(text, PATTERN_XML_LIKE_HASH, 1, TinyTextUtils::addAmpersandsToHex);
        text = replaceRegexWithGroup(text, PATTERN_AMPERSAND_HASH, 1, TinyTextUtils::addAmpersandsToHex);
        text = ChatColor.translateAlternateColorCodes('&', text);
        text = text.replace("{ampersand}", "&");
        return text;
    }

    private static String replaceGradients(String text) {

        text = text.replaceAll("<#/([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F])>", "<#/$1><#$1>");

        final Matcher matcher = PATTERN_HEX_GRADIENT.matcher(text);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final HexColor startColor = new HexColor(matcher.group(1));
            final HexColor endColor = new HexColor(matcher.group(3));
            final String partText = matcher.group(2);
            matcher.appendReplacement(sb, HexColor.applyGradient(partText, startColor, endColor));
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        while (result.matches(".*&x&[0-9a-zA-Z]&[0-9a-zA-Z]&[0-9a-zA-Z]&[0-9a-zA-Z]&[0-9a-zA-Z]&[0-9a-zA-Z]$")) {
            result = result.substring(0, result.length() - 14);
        }
        return result;
    }

    /**
     * Replaces Emojis, PlacederholderAPI placeholders and colors (see {@link #color(String)})
     *
     * @param text Text to translate
     * @return Translated text
     */
    public static String format(final String text) {
        return color(text);
    }

}