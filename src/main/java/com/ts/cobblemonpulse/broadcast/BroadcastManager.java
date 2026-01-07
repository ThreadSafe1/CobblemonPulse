package com.ts.cobblemonpulse.broadcast;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.ts.cobblemonpulse.config.Config;
import com.ts.cobblemonpulse.config.MessageBuilder;
import com.ts.cobblemonpulse.config.MessageType;
import com.ts.cobblemonpulse.config.PokemonContext;
import com.ts.cobblemonpulse.loader.LangLoader;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.StringJoiner;

/**
 * BroadcastManager is responsible for sending messages to all players
 * currently connected to the Minecraft server, including playing
 * sounds and executing commands based on configuration.
 */
public class BroadcastManager {

    /**
     * Broadcasts a Pokémon-related message to all online players.
     *
     * @param server       The Minecraft server instance
     * @param messageType  The type of message (used to fetch configuration)
     * @param player       The player related to the event (can be null)
     * @param pokemon      The Pokémon involved in the broadcast (cannot be null)
     */
    public static void broadcast(MinecraftServer server, MessageType messageType, ServerPlayerEntity player, Pokemon pokemon) {
        // Initialize the player name, defaulting to empty if null
        String playerName = "";
        if (player != null) playerName = player.getName().getString();

        // If no Pokémon is provided, exit the method
        if (pokemon == null) return;

        // Translate the Pokémon species name
        String pokemonName = LangLoader.translateSpecies(pokemon.getSpecies().getName());

        ServerWorld world;
        BlockPos blockPos;

        // Get the world and position from the Pokémon entity if it exists
        PokemonEntity pokemonEntity = pokemon.getEntity();
        if (pokemonEntity != null) {
            world = (ServerWorld) pokemonEntity.getWorld();
            blockPos = pokemonEntity.getBlockPos();
        } else {
            // If the entity doesn't exist, fall back to the player world or overworld
            if (player == null) {
                world = server.getOverworld(); // Default to the overworld if no player
                blockPos = new BlockPos(0, 0, 0); // Default coordinates
            } else {
                world = player.getServerWorld();
                blockPos = player.getBlockPos();
            }
        }

        // Join all Pokémon types into a single comma-separated string
        StringJoiner type = new StringJoiner(", ");
        pokemon.getTypes().forEach(t -> {
            type.add(LangLoader.translateType(t.getName()));
        });

        // Create a context object containing all relevant Pokémon and player information
        PokemonContext ctx = new PokemonContext(
                pokemonName,
                playerName,
                world,
                blockPos,
                pokemon.getShiny(),
                pokemon.isLegendary(),
                pokemon.isMythical(),
                pokemon.getLevel(),
                LangLoader.translateAbility(pokemon.getAbility().getName()),
                LangLoader.translateForm(pokemon.getForm().getName()),
                LangLoader.translateGender(pokemon.getGender().name()),
                LangLoader.translateNature(pokemon.getNature().getName().getPath()),
                type.toString()
        );

        // Retrieve the configuration section for this message type
        Config.MessageSection section = Config.getSection(messageType, ctx);

        // Build the actual text message to send
        Text message = MessageBuilder.build(messageType, ctx);

        // Iterate over all connected players
        for (ServerPlayerEntity all : server.getPlayerManager().getPlayerList()) {
            // Send the message if broadcasting is enabled and the message is not empty
            if (section.broadcast_enabled() && !message.getString().isEmpty()) {
                all.sendMessage(message, false);
            }

            // Play a sound if sound broadcasting is enabled and a sound is configured
            if (section.sound_enabled() && section.sound() != null && !section.sound().isEmpty()) {
                // Convert the sound string into an Identifier
                Identifier id = Identifier.of(section.sound());

                // Get the sound event from the registry
                RegistryEntry<SoundEvent> soundEntry = Registries.SOUND_EVENT.getEntry(id)
                        .orElseThrow(() -> new IllegalArgumentException("Sound not found: " + id));

                // Send the sound packet to the player at their current location
                all.networkHandler.sendPacket(new PlaySoundS2CPacket(
                        soundEntry,
                        SoundCategory.MASTER,
                        all.getX(),
                        all.getY(),
                        all.getZ(),
                        section.volume(),
                        section.pitch(),
                        all.getWorld().random.nextLong() // Seed for pitch variation
                ));
            }
        }

        // Execute configured commands if command broadcasting is enabled
        if (section.command_enabled()) {
            String finalPlayerName = playerName;
            server.execute(() -> {
                for (String cmd : section.commands()) {
                    // Replace placeholders in commands with actual player and Pokémon names
                    cmd = cmd.replace("{player}", finalPlayerName)
                            .replace("{pokemon}", pokemonName);
                    // Execute the command on the server
                    server.getCommandManager().executeWithPrefix(server.getCommandSource(), cmd);
                }
            });
        }
    }
}
