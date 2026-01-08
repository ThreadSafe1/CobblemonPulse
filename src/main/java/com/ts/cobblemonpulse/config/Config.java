package com.ts.cobblemonpulse.config;

import java.util.List;

/**
 * Config class holds all configurable message data for CobblemonPulse.
 * <p>
 * It organizes messages by event types (spawn, catch, defeat) and by
 * Pokémon rarity context (normal, shiny, legendary, mythical, ultrabeast).
 */
public class Config {

    /* ========================
       Message Section Data Class
       ======================== */

    /**
     * Represents a single message configuration.
     * Includes broadcast settings, sound settings, commands, and optional hover text.
     *
     * @param broadcast_enabled Whether broadcasting is enabled for this message
     * @param broadcast_message The main message content
     * @param pokemon_hover     Optional hover text lines when the player hovers over the message
     * @param sound_enabled     Whether playing a sound is enabled
     * @param sound             The sound identifier (e.g., "minecraft:entity.player.levelup")
     * @param volume            Sound volume (1.0 = default)
     * @param pitch             Sound pitch (1.0 = default)
     * @param command_enabled   Whether executing commands is enabled
     * @param commands          List of server commands to run, supports placeholders like {player} and {pokemon}
     */
    public record MessageSection(
            boolean broadcast_enabled,
            String broadcast_message,
            List<String> pokemon_hover,
            boolean sound_enabled,
            String sound,
            float volume,
            float pitch,
            boolean command_enabled,
            List<String> commands
    ) {
    }

    /* ========================
       Actual Config Values
       ======================== */

    /**
     * Message groups for different events.
     * Each group holds messages for normal/shiny/legendary/mythical/ultrabeast Pokémon.
     */
    public static MessageGroup SPAWN;
    public static MessageGroup CATCH;
    public static MessageGroup DEFEAT;

    /* ========================
       Message Group (Normal/Shiny/Legendary/Mythical)
       ======================== */

    /**
     * Groups multiple MessageSections by Pokémon rarity.
     *
     * @param normal      Standard Pokémon messages
     * @param shiny       Shiny Pokémon messages
     * @param legendary   Legendary Pokémon messages
     * @param mythical    Mythical Pokémon messages
     * @param ultrabeast  UltraBeast Pokémon messages
     */
    public record MessageGroup(
            MessageSection normal,
            MessageSection shiny,
            MessageSection legendary,
            MessageSection mythical,
            MessageSection ultrabeast
    ) {
    }

    /* ========================
       Core Method ⭐
       ======================== */

    /**
     * Retrieves the appropriate MessageSection based on the event type
     * and Pokémon context (shiny, legendary, mythical, ultrabeast).
     * <p>
     * Priority order when selecting a message section:
     * Mythical > UltraBeast > Legendary > Shiny > Normal
     *
     * @param type The type of event (SPAWN, CAPTURED, DEFEAT)
     * @param ctx  Context object containing information about the Pokémon's rarity
     * @return The appropriate MessageSection matching the Pokémon context,
     *         or normal if no special rarity applies
     */
    public static MessageSection getSection(
            MessageType type,
            PokemonContext ctx
    ) {
        // Determine the correct message group based on the event type
        MessageGroup group = switch (type) {
            case SPAWN -> SPAWN;
            case CAPTURED -> CATCH;
            case DEFEAT -> DEFEAT;
        };

        // Select the highest-priority rarity message section available
        if (ctx.mythical && group.mythical() != null) {
            return group.mythical(); // Mythical has the highest priority
        }
        if (ctx.ultrabeast && group.ultrabeast() != null) {
            return group.ultrabeast(); // UltraBeast is checked
        }
        if (ctx.legendary && group.legendary() != null) {
            return group.legendary(); // Legendary comes next
        }
        if (ctx.shiny && group.shiny() != null) {
            return group.shiny(); // Shiny is checked after legendary/mythical/ultrabeast
        }

        // Default to the normal message section if no special rarity matches
        return group.normal();
    }
}
