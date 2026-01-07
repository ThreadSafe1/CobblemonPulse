package com.ts.cobblemonpulse.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.ts.cobblemonpulse.broadcast.BroadcastManager;
import com.ts.cobblemonpulse.config.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * PokemonCaptureListener listens for Pokémon capture events on the server.
 * <p>
 * Whenever a player successfully captures a Pokémon, this listener triggers
 * and broadcasts a message to all connected players using the BroadcastManager.
 */
public class PokemonCaptureListener {

    /**
     * Registers the listener for Pokémon capture events.
     * <p>
     * Uses the CobblemonJavaHooks helper to subscribe to the Kotlin Observable
     * from Java code with a standard Consumer lambda.
     * <p>
     * Steps performed in the listener:
     * 1. Retrieve the captured Pokémon from the event.
     * 2. Retrieve the player who captured the Pokémon.
     * 3. Retrieve the server instance from the player's world.
     * 4. Call BroadcastManager to broadcast the capture message to all players.
     */
    public static void register() {
        CobblemonJavaHooks.subscribeJava(
                CobblemonEvents.POKEMON_CAPTURED, // Observable for capture events
                Priority.NORMAL,                  // Subscription priority
                event -> {                        // Java Consumer to handle the event
                    // Get the captured Pokémon
                    Pokemon pokemon = event.getPokemon();

                    // Get the player who captured the Pokémon
                    ServerPlayerEntity player = event.getPlayer();

                    // Get the server instance
                    MinecraftServer server = player.getWorld().getServer();
                    if (server == null) return; // Exit if server is not available

                    // Broadcast the capture message to all players
                    BroadcastManager.broadcast(
                            server,
                            MessageType.CAPTURED, // Use the CAPTURED message type
                            player,               // The player who captured the Pokémon
                            pokemon               // The Pokémon that was captured
                    );
                }
        );
    }
}
