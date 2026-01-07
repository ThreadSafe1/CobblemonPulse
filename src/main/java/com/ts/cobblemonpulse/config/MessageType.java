package com.ts.cobblemonpulse.config;

/**
 * MessageType represents the type of Pokémon-related events
 * for which chat messages can be generated.
 */
public enum MessageType {
    /**
     * Event triggered when a Pokémon spawns in the world
     */
    SPAWN,

    /**
     * Event triggered when a Pokémon is caught by a player
     */
    CAPTURED,

    /**
     * Event triggered when a Pokémon is defeated by a player
     */
    DEFEAT
}