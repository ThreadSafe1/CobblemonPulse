package com.ts.cobblemonpulse.loader;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * LangLoader handles loading and translating language strings for CobblemonPulse.
 * <p>
 * It reads JSON files from the mod's assets folder (e.g., `assets/cobblemon/lang/en_us.json`)
 * and provides static methods to translate Pokémon-related terms such as species,
 * abilities, natures, genders, forms, and types.
 */
public class LangLoader {

    /** Gson instance for reading JSON files */
    private static final Gson GSON = new Gson();

    /** Map holding key-value pairs loaded from the locale JSON file */
    private static Map<String, String> translations;

    /**
     * Loads a language file based on the given locale code.
     * <p>
     * Example: `load("en_us")` will load `assets/cobblemon/lang/en_us.json`.
     * If the file cannot be found or fails to load, an empty map is used.
     *
     * @param locale The locale code (e.g., "en_us", "ko_kr")
     */
    public static void load(String locale) {
        ClassLoader classLoader = LangLoader.class.getClassLoader();
        if (classLoader != null) {
            InputStream inputStream = classLoader.getResourceAsStream(
                    "assets/cobblemon/lang/" + locale + ".json"
            );
            if (inputStream != null) {
                try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                    // Deserialize JSON into a Map<String, String>
                    Type type = new TypeToken<Map<String, String>>() {}.getType();
                    translations = GSON.fromJson(reader, type);
                } catch (Exception e) {
                    e.printStackTrace();
                    translations = Map.of(); // fallback to empty map
                }
            }
        }
    }

    /**
     * Internal helper for translating a key using optional arguments.
     * <p>
     * If the key is missing in the translations map, the key itself is returned.
     *
     * @param key  The translation key (e.g., "cobblemon.species.pikachu.name")
     * @param args Optional arguments for formatting
     * @return The translated string with placeholders replaced
     */
    private static String translate(String key, Object... args) {
        String value = translations.getOrDefault(key, key); // Return key if translation missing
        return String.format(value.replace("%s", "%s"), args); // Apply formatting
    }

    /* ==============================
       Public Translation Methods
       ============================== */

    /** Translate Pokémon species name */
    public static String translateSpecies(String pokemonName) {
        return translate("cobblemon.species." + pokemonName.toLowerCase() + ".name");
    }

    /** Translate Pokémon ability name */
    public static String translateAbility(String abilityName) {
        return translate("cobblemon.ability." + abilityName.toLowerCase());
    }

    /** Translate Pokémon nature */
    public static String translateNature(String natureName) {
        return translate("cobblemon.nature." + natureName.toLowerCase());
    }

    /** Translate Pokémon gender */
    public static String translateGender(String genderName) {
        return translate("cobblemon.gender." + genderName.toLowerCase());
    }

    /** Translate Pokémon form (e.g., Alolan, Galarian) */
    public static String translateForm(String formName) {
        return translate("cobblemon.ui.pokedex.info.form." + formName.toLowerCase());
    }

    /** Translate Pokémon type (e.g., Fire, Water) */
    public static String translateType(String typeName) {
        return translate("cobblemon.type." + typeName.toLowerCase());
    }
}
