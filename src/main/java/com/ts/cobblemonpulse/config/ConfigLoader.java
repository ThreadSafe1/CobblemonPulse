package com.ts.cobblemonpulse.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ts.cobblemonpulse.loader.LangLoader;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * ConfigLoader handles loading and saving of CobblemonPulse configuration files.
 * <p>
 * Responsibilities:
 * - Reads configuration from a JSON file in the Fabric config directory.
 * - Populates the static Config class's message groups (SPAWN, CATCH, DEFEAT).
 * - Initializes default values if the file does not exist.
 */
public class ConfigLoader {

    /**
     * Gson instance used for JSON parsing and writing.
     * - Pretty printing enabled for readability.
     * - HTML escaping disabled to allow Minecraft formatting codes.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Configuration file location in the Fabric config directory.
     */
    private static final File FILE =
            FabricLoader.getInstance().getConfigDir().resolve("cobblemonpulse/config.json").toFile();

    /**
     * Loads the configuration from the JSON file.
     * <p>
     * Steps:
     * 1. If the file doesn't exist, create the directories and save default configuration.
     * 2. Read the JSON file into a Root object.
     * 3. Assign the message groups to the Config class's static fields.
     * 4. Load the language file if specified.
     */
    public static void load() {
        try {
            // Create config file and directories if missing
            if (!FILE.exists()) {
                FILE.getParentFile().mkdirs();
                saveDefault(); // Generate default JSON
            }

            // Parse JSON file into Root structure
            Root root = GSON.fromJson(new FileReader(FILE), Root.class);

            // Assign parsed message groups to Config class
            Config.SPAWN = root.spawn;
            Config.CATCH = root.capture;
            Config.DEFEAT = root.defeat;

            // Load language if specified
            if (root.language != null && !root.language.isEmpty()) {
                LangLoader.load(root.language); // Load only once
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log errors during loading
        }
    }

    /**
     * Saves the default configuration JSON to disk.
     * <p>
     * Creates default messages, hover text, and commands for all events.
     *
     * @throws Exception if file writing fails
     */
    private static void saveDefault() throws Exception {
        // Base hover text lines displayed on hover
        List<String> base = List.of(
                "&7Pokémon: &f{pokemon} lv.{level}",
                "&7Types: &a{type}",
                "&7Ability: &f{ability}",
                "&7Form: &a{form}",
                "&7Nature: &f{nature}",
                "&7Gender: &a{gender}",
                "&7World: &f{world}",
                "&7Biome: &a{biome}",
                "&7Location: &f{x} {y} {z}"
        );

        // Create Root object with default messages for SPAWN, CATCH, DEFEAT
        Root root = new Root(
                "en_us", // Default language
                defaultGroup(
                        "&2A wild &6{pokemon} &2has appeared near &b{player}&2!",
                        base,
                        List.of("/tell {player} A wild {pokemon} has appeared near {player}!")
                ),
                defaultGroup(
                        "&6{pokemon} &awas caught by &b{player}&a!",
                        base,
                        List.of("/tell {player} {pokemon} was caught by {player}")
                ),
                defaultGroup(
                        "&4{pokemon} &chas been defeated by &b{player}&c!",
                        base,
                        List.of("/tell {player} {pokemon} has been defeated by {player}!")
                )
        );

        // Write the Root object as JSON to file
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(root, writer);
        }
    }

    /**
     * Creates a default MessageGroup for all Pokémon rarities (normal, shiny, legendary, mythical).
     * <p>
     * Each rarity type can have a custom prefix and shares the same hover text and commands.
     *
     * @param baseMessage Base message text for normal Pokémon
     * @param baseHover   Base hover text lines
     * @param baseCommand Base commands to execute for the message
     * @return Config.MessageGroup containing MessageSections for all rarities
     */
    private static Config.MessageGroup defaultGroup(String baseMessage, List<String> baseHover, List<String> baseCommand) {
        return new Config.MessageGroup(
                // Normal Pokémon
                new Config.MessageSection(
                        true,
                        baseMessage,
                        baseHover,
                        true,
                        "minecraft:entity.experience_orb.pickup",
                        1.0f,
                        1.0f,
                        true,
                        baseCommand
                ),
                // Shiny Pokémon
                new Config.MessageSection(
                        true,
                        "&#FFD700✨ Shiny! " + baseMessage,
                        baseHover,
                        true,
                        "minecraft:entity.experience_orb.pickup",
                        1.0f,
                        1.0f,
                        true,
                        baseCommand
                ),
                // Legendary Pokémon
                new Config.MessageSection(
                        true,
                        "&#FF5555⭐ Legendary! " + baseMessage,
                        baseHover,
                        true,
                        "minecraft:entity.experience_orb.pickup",
                        1.0f,
                        1.0f,
                        true,
                        baseCommand
                ),
                // Mythical Pokémon
                new Config.MessageSection(
                        true,
                        "&#AA00FF✦ Mythical! " + baseMessage,
                        baseHover,
                        true,
                        "minecraft:entity.experience_orb.pickup",
                        1.0f,
                        1.0f,
                        true,
                        baseCommand
                ),
                // UltraBeast Pokémon
                new Config.MessageSection(
                        true,
                        "&#AA00FF✦ UltraBeast! " + baseMessage,
                        baseHover,
                        true,
                        "minecraft:entity.experience_orb.pickup",
                        1.0f,
                        1.0f,
                        true,
                        baseCommand
                )
        );
    }

    /* ========================
       JSON Root Structure
       ======================== */

    /**
     * Represents the root structure of the configuration JSON.
     * <p>
     * Fields correspond to top-level JSON keys:
     * - language: default language code (e.g., "en_us")
     * - spawn: MessageGroup for Pokémon spawn events
     * - capture: MessageGroup for Pokémon capture events
     * - defeat: MessageGroup for Pokémon defeat events
     */
    private record Root(
            String language,
            Config.MessageGroup spawn,
            Config.MessageGroup capture,
            Config.MessageGroup defeat
    ) {
    }
}
