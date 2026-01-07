package com.ts.cobblemonpulse.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.ts.cobblemonpulse.broadcast.BroadcastManager;
import com.ts.cobblemonpulse.config.MessageType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * PokemonSpawnListener listens for Pokémon spawn events on the server.
 * <p>
 * When a wild Pokémon appears in the world, this listener broadcasts
 * a message to nearby players or the server depending on configuration.
 */
public class PokemonSpawnListener {

    /**
     * Registers the listener for Pokémon spawn events.
     * <p>
     * This method hooks into Fabric's server entity load events.
     * Only wild Pokémon (no owner) and newly spawned Pokémon (age ≤ 1) are considered.
     * The message is broadcasted to all players within a radius based on the server's view distance.
     */
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {

            // 1️⃣ Ignore client-side worlds; only process on server
            if (entity.getWorld().isClient()) return;

            // 2️⃣ Only process if the entity is a Pokémon
            if (!(entity instanceof PokemonEntity pokeEntity)) return;

            Pokemon pokemon = pokeEntity.getPokemon();

            // 3️⃣ Skip if the Pokémon has an owner (i.e., it is not wild)
            if (pokemon.getOwnerEntity() != null) return;

            // 4️⃣ Only broadcast newly spawned Pokémon (age <= 1 tick)
            if (pokeEntity.age > 1) return;

            // 5️⃣ Retrieve the server instance from the Pokémon's world
            MinecraftServer server = entity.getWorld().getServer();
            if (server == null) return; // Exit if server is not available

            // 6️⃣ Determine nearest player to the Pokémon
            // Get the server's view distance in chunks
            int viewDistance = server.getPlayerManager().getViewDistance();

            // Convert view distance to blocks (1 chunk = 16 blocks)
            double radius = viewDistance * 16;

            // Find the closest player within the radius
            ServerPlayerEntity nearest = (ServerPlayerEntity) entity.getWorld().getClosestPlayer(entity, radius);

            // 7️⃣ Broadcast the spawn message using BroadcastManager
            BroadcastManager.broadcast(
                    server,
                    MessageType.SPAWN, // Use SPAWN message type
                    nearest,           // Player to reference in the message
                    pokemon            // The Pokémon that spawned
            );
        });
    }
}
