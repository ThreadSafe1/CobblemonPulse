package com.ts.cobblemonpulse.util;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MessageUtils provides utility methods for constructing Minecraft chat messages
 * with colors, placeholders, and Pokémon hover text.
 * <p>
 * Features:
 * - Parses Minecraft color codes (both standard & codes and hex &#RRGGBB codes)
 * - Replaces placeholders like {player}, {pokemon}, etc.
 * - Builds hoverable text for Pokémon names
 */
public class MessageUtils {

    /* ========================
       Color Parsing
       ======================== */

    /**
     * Parses a string containing Minecraft color codes into a MutableText object.
     * Supports:
     * - Standard formatting codes (&a, &b, etc.)
     * - Hex codes (&#RRGGBB)
     *
     * @param input The raw string containing color codes
     * @return MutableText with the proper colors applied
     */
    public static MutableText parseColors(String input) {
        MutableText result = Text.empty();

        Formatting currentFormat = Formatting.RESET;
        Integer currentRgb = null;

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // HEX color: &#RRGGBB
            if (c == '&' && i + 7 < input.length() && input.charAt(i + 1) == '#') {
                flush(buffer, result, currentFormat, currentRgb);

                String hex = input.substring(i + 2, i + 8);
                currentRgb = Integer.parseInt(hex, 16);
                currentFormat = null;

                i += 7;
                continue;
            }

            // Standard color/formatting code: &a, &b, etc.
            if (c == '&' && i + 1 < input.length()) {
                Formatting format = Formatting.byCode(input.charAt(i + 1));
                if (format != null) {
                    flush(buffer, result, currentFormat, currentRgb);

                    currentFormat = format;
                    currentRgb = null;

                    i++;
                    continue;
                }
            }

            buffer.append(c);
        }

        flush(buffer, result, currentFormat, currentRgb);
        return result;
    }

    /**
     * Flushes the current text buffer into the result MutableText
     * applying the current formatting or hex color.
     *
     * @param buffer The current string buffer
     * @param result The final MutableText being built
     * @param format Current Minecraft formatting code
     * @param rgb    Current hex color, if any
     */
    private static void flush(
            StringBuilder buffer,
            MutableText result,
            Formatting format,
            Integer rgb
    ) {
        if (buffer.isEmpty()) return;

        MutableText text = Text.literal(buffer.toString());

        if (rgb != null) {
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            text = text.styled(style ->
                    style.withColor(ColorHelper.Argb.getArgb(r, g, b))
            );
        } else if (format != null) {
            text = text.formatted(format);
        }

        result.append(text);
        buffer.setLength(0);
    }

    /* ========================
       Hover Text Utilities
       ======================== */

    /**
     * Builds hover text from multiple lines and replaces placeholders.
     *
     * @param lines        List of hover text lines
     * @param placeholders Map of placeholder keys to replacement values
     * @return MutableText representing the hover content
     */
    public static MutableText buildHover(
            List<String> lines,
            Map<String, String> placeholders
    ) {
        MutableText hover = Text.empty();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            for (var e : placeholders.entrySet()) {
                line = line.replace("{" + e.getKey() + "}", e.getValue());
            }

            hover.append(parseColors(line));

            if (i < lines.size() - 1) {
                hover.append(Text.literal("\n"));
            }
        }
        return hover;
    }

    /**
     * Builds a Pokémon name text with a hover event.
     *
     * @param pokemon      Pokémon name
     * @param hoverLines   Hover text lines
     * @param placeholders Placeholder map for replacements
     * @return MutableText with hover functionality for the Pokémon name
     */
    public static MutableText pokemonWithHover(
            String pokemon,
            List<String> hoverLines,
            Map<String, String> placeholders
    ) {
        return parseColors(pokemon).styled(style ->
                style.withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                buildHover(hoverLines, placeholders)
                        )
                )
        );
    }

    /**
     * Finds the last color applied in the text.
     * Supports both hex codes (&#RRGGBB) and formatting codes (&a, &b, etc.).
     *
     * @param text Input string
     * @return TextColor of the last applied color, or null if none
     */
    private static TextColor findLastColor(String text) {
        // HEX color
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher hexMatcher = hexPattern.matcher(text);

        TextColor lastHex = null;
        while (hexMatcher.find()) {
            lastHex = TextColor.fromRgb(
                    Integer.parseInt(hexMatcher.group(1), 16)
            );
        }
        if (lastHex != null) return lastHex;

        // Standard formatting colors
        Formatting lastFormatting = null;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == '&') {
                Formatting f = Formatting.byCode(text.charAt(i + 1));
                if (f != null && f.isColor()) {
                    lastFormatting = f;
                }
            }
        }

        return lastFormatting != null ? TextColor.fromFormatting(lastFormatting) : null;
    }

    /* ========================
       Message Construction
       ======================== */

    /**
     * Builds a full chat message where the Pokémon name has hoverable text.
     *
     * @param message      Message template containing "{pokemon}" placeholder
     * @param pokemonName  Pokémon name to replace the placeholder
     * @param hoverLines   Hover text lines for the Pokémon
     * @param placeholders Map of additional placeholders to replace
     * @return MutableText representing the final message
     */
    public static MutableText buildMessageWithPokemonHover(
            String message,
            String pokemonName,
            List<String> hoverLines,
            Map<String, String> placeholders
    ) {
        String[] parts = message.split("\\{pokemon}", -1);
        MutableText result = Text.empty();

        if (!parts[0].isEmpty()) {
            result.append(parseColors(
                    replacePlaceholders(parts[0], placeholders)
            ));
        }

        TextColor inheritedColor = findLastColor(parts[0]);

        MutableText pokemonText =
                pokemonWithHover(pokemonName, hoverLines, placeholders);

        if (inheritedColor != null) {
            pokemonText = pokemonText.styled(style ->
                    style.withColor(inheritedColor)
            );
        }

        result.append(pokemonText);

        if (parts.length > 1 && !parts[1].isEmpty()) {
            result.append(parseColors(
                    replacePlaceholders(parts[1], placeholders)
            ));
        }

        return result;
    }

    /**
     * Replaces placeholders in a text with the corresponding values.
     *
     * @param text         Input string with placeholders like "{player}"
     * @param placeholders Map of placeholder keys and values
     * @return Text with placeholders replaced
     */
    private static String replacePlaceholders(String text, Map<String, String> placeholders) {
        String result = text;
        for (var e : placeholders.entrySet()) {
            if (e.getValue() != null) {
                result = result.replace("{" + e.getKey() + "}", e.getValue());
            }
        }
        return result;
    }
}
