package com.ts.cobblemonpulse;

import com.ts.cobblemonpulse.config.ConfigLoader;
import com.ts.cobblemonpulse.event.PokemonCaptureListener;
import com.ts.cobblemonpulse.event.PokemonDefeatListener;
import com.ts.cobblemonpulse.event.PokemonSpawnListener;
import com.ts.cobblemonpulse.util.PermissionUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

/**
 * Main class for the CobblemonPulse Fabric mod.
 *
 * Responsibilities:
 * - Load configuration on mod initialization
 * - Register Pokémon event listeners (spawn, capture, defeat)
 * - Register commands, e.g., /cobblemonpulse to reload configuration
 */
public class Cobblemonpulse implements ModInitializer {

    /**
     * Called when the mod is initialized.
     * <p>
     * Performs the following actions:
     * 1. Loads configuration from JSON via ConfigLoader
     * 2. Registers Pokémon event listeners for spawn, capture, and defeat
     * 3. Registers the /cobblemonpulse command for reloading the configuration
     */
    @Override
    public void onInitialize() {
        // Load the configuration from the JSON file
        ConfigLoader.load();

        // Register Pokémon event listeners
        PokemonSpawnListener.register();
        PokemonCaptureListener.register();
        PokemonDefeatListener.register();

        // Register /cobblemonpulse command for admin use
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("cobblemonpulse")
                            // Requires "cobblemonpulse.admin" permission
                            .requires(source -> PermissionUtils.hasPermission(source, "cobblemonpulse.admin"))
                            .executes(context -> {
                                // Reload the configuration
                                ConfigLoader.load();

                                // Provide feedback to the command sender
                                context.getSource().sendFeedback(
                                        () -> Text.literal("[CobblemonPulse] configuration has been reloaded!"),
                                        false
                                );

                                return 1; // Command success
                            })
            );
        });
    }
}
