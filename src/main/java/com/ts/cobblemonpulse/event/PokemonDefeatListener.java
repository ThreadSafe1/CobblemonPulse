package com.ts.cobblemonpulse.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.ts.cobblemonpulse.broadcast.BroadcastManager;
import com.ts.cobblemonpulse.config.MessageType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * PokemonDefeatListener listens for Pokémon defeat (faint) events in battles.
 * <p>
 * When a wild Pokémon is defeated by a player, this listener triggers
 * and broadcasts a message to all connected players.
 * <p>
 * Only wild Pokémon are considered (Pokémon with no owner).
 */
public class PokemonDefeatListener {

    /**
     * Registers the listener for Pokémon defeat events.
     * <p>
     * Uses CobblemonJavaHooks to subscribe from Java to the Kotlin Observable
     * provided by CobblemonEvents.
     * <p>
     * Steps performed in the listener:
     * 1. Get the defeated BattlePokemon from the event.
     * 2. Get the PokemonEntity associated with the BattlePokemon.
     * 3. Retrieve the Pokemon data from the entity.
     * 4. Skip Pokémon that have an owner (only wild Pokémon are broadcasted).
     * 5. Get a player involved in the battle to determine the server.
     * 6. Retrieve the MinecraftServer instance.
     * 7. Call BroadcastManager to broadcast the defeat message to all players.
     */
    public static void register() {
        CobblemonJavaHooks.subscribeJava(
                CobblemonEvents.BATTLE_FAINTED, // Observable for Pokémon faint events
                Priority.NORMAL,                // Subscription priority
                event -> {                      // Java Consumer to handle the event
                    // 1️⃣ Get the defeated Pokémon in the battle
                    BattlePokemon battlePokemon = event.getKilled();

                    // 2️⃣ Get the entity of the defeated Pokémon
                    PokemonEntity pokemonEntity = battlePokemon.getEntity();
                    if (pokemonEntity == null) return; // Exit if entity is null

                    // 3️⃣ Retrieve the Pokémon object
                    Pokemon pokemon = pokemonEntity.getPokemon();

                    // 4️⃣ Skip if Pokémon has an owner (we only broadcast wild Pokémon defeats)
                    LivingEntity owner = pokemon.getOwnerEntity();
                    if (owner != null) return;

                    // 5️⃣ Get one player involved in the battle
                    ServerPlayerEntity player = event.getBattle().getPlayers().getFirst();

                    // 6️⃣ Get the server instance
                    MinecraftServer server = player.getWorld().getServer();
                    if (server == null) return; // Exit if server is not available

                    // 7️⃣ Broadcast the defeat message to all players
                    BroadcastManager.broadcast(
                            server,
                            MessageType.DEFEAT, // Use the DEFEAT message type
                            player,             // Player involved in the battle
                            pokemon             // The Pokémon that was defeated
                    );
                }
        );
    }
}
