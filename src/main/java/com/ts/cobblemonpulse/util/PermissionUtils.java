package com.ts.cobblemonpulse.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

/**
 * PermissionUtils provides a simple helper for checking
 * player or command source permissions using the Lucko Fabric Permissions API.
 * <p>
 * This utility abstracts away direct API calls and makes permission
 * checks cleaner throughout the CobblemonPulse plugin.
 */
public class PermissionUtils {

    /**
     * Checks if the given command source has the specified permission node.
     *
     * @param source The source of the command (player, console, etc.)
     * @param perm   The permission node to check, e.g., "cobblemon.broadcast.spawn"
     * @return true if the source has the permission, false otherwise
     */
    public static boolean hasPermission(ServerCommandSource source, String perm) {
        return Permissions.check(source, perm);
    }
}
