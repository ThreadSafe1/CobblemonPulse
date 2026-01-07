package com.ts.cobblemonpulse.config;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * PokemonContext holds all relevant information about a specific Pokémon event.
 * <p>
 * This context is used by Config and MessageBuilder to:
 * - Determine which message section to display (normal/shiny/legendary/mythical)
 * - Replace placeholders in message templates
 * - Provide additional data for hover text or commands
 */
public class PokemonContext {

    /** The name of the Pokémon involved in the event */
    public final String pokemon;

    /** The name of the player interacting with the Pokémon */
    public final String player;

    /** The server world where the event occurs */
    public final ServerWorld world;

    /** The exact block position of the Pokémon in the world */
    public final BlockPos pos;

    /** True if the Pokémon is shiny */
    public final boolean shiny;

    /** True if the Pokémon is legendary */
    public final boolean legendary;

    /** True if the Pokémon is mythical */
    public final boolean mythical;

    /** The Pokémon's current level */
    public final int level;

    /** The Pokémon's ability name (translated) */
    public final String ability;

    /** The Pokémon's form (e.g., Alolan, Galarian) */
    public final String form;

    /** The Pokémon's gender */
    public final String gender;

    /** The Pokémon's nature */
    public final String nature;

    /** The Pokémon's type(s) as a comma-separated string */
    public final String type;

    /**
     * Constructs a new PokemonContext instance with all relevant event data.
     *
     * @param pokemon   The Pokémon's name
     * @param player    The player's name
     * @param world     The server world where the event occurs
     * @param pos       The block position of the Pokémon
     * @param shiny     True if the Pokémon is shiny
     * @param legendary True if the Pokémon is legendary
     * @param mythical  True if the Pokémon is mythical
     * @param level     The Pokémon's level
     * @param ability   The Pokémon's ability name
     * @param form      The Pokémon's form
     * @param gender    The Pokémon's gender
     * @param nature    The Pokémon's nature
     * @param type      The Pokémon's type(s)
     */
    public PokemonContext(
            String pokemon,
            String player,
            ServerWorld world,
            BlockPos pos,
            boolean shiny,
            boolean legendary,
            boolean mythical,
            int level,
            String ability,
            String form,
            String gender,
            String nature,
            String type
    ) {
        this.pokemon = pokemon;
        this.player = player;
        this.world = world;
        this.pos = pos;
        this.shiny = shiny;
        this.legendary = legendary;
        this.mythical = mythical;
        this.level = level;
        this.ability = ability;
        this.form = form;
        this.gender = gender;
        this.nature = nature;
        this.type = type;
    }
}
