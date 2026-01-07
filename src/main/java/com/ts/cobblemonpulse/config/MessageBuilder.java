package com.ts.cobblemonpulse.config;

import com.ts.cobblemonpulse.util.MessageUtils;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * MessageBuilder is responsible for constructing chat messages
 * for CobblemonPulse based on event type and Pokémon context.
 * <p>
 * Responsibilities:
 * - Replaces placeholders in messages (e.g., {pokemon}, {level}, {player})
 * - Applies Pokémon rarity-specific message sections
 * - Attaches hover text for Pokémon details
 */
public class MessageBuilder {

    /**
     * Builds a Minecraft chat message for a given event type and Pokémon context.
     *
     * @param type The type of event (SPAWN, CATCH, DEFEAT)
     * @param ctx  Context information about the Pokémon and player (level, rarity, location, etc.)
     * @return A Text object representing the message ready to send to players
     */
    public static Text build(MessageType type, PokemonContext ctx) {

        // 1️⃣ Get the correct message section based on event type and Pokémon rarity
        Config.MessageSection section = Config.getSection(type, ctx);

        // 2️⃣ Prepare a map of placeholders to replace in the message text
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("pokemon", ctx.pokemon);                          // Pokémon name
        placeholders.put("level", String.valueOf(ctx.level));              // Pokémon level
        placeholders.put("type", ctx.type);                                // Pokémon type(s)
        placeholders.put("ability", ctx.ability);                          // Ability name
        placeholders.put("form", ctx.form);                                // Form name
        placeholders.put("gender", ctx.gender);                            // Gender
        placeholders.put("nature", ctx.nature);                            // Nature
        placeholders.put("player", ctx.player);                            // Player name
        placeholders.put("world", ctx.world.getRegistryKey().getValue().getPath()); // World name
        placeholders.put("x", String.valueOf(ctx.pos.getX()));             // X coordinate
        placeholders.put("y", String.valueOf(ctx.pos.getY()));             // Y coordinate
        placeholders.put("z", String.valueOf(ctx.pos.getZ()));             // Z coordinate
        placeholders.put("biome",
                ctx.world.getBiome(ctx.pos).getKey().get().getValue().getPath() // Biome at position
        );

        // 3️⃣ Build the final message using MessageUtils
        // - Replaces all placeholders
        // - Attaches hover text for detailed Pokémon info
        Text message = MessageUtils.buildMessageWithPokemonHover(
                section.broadcast_message(),   // Main message text
                ctx.pokemon,                   // Pokémon name for hover
                section.pokemon_hover(),       // Hover text lines
                placeholders                   // Map of placeholders to replace
        );

        // 4️⃣ Return the completed chat message
        return message;
    }
}
