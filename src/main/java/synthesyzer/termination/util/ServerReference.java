package synthesyzer.termination.util;

import net.minecraft.server.MinecraftServer;

/**
 * DANGEROUS: DO NOT USE THIS CLASS UNLESS YOU KNOW WHAT YOU ARE DOING
 */
public class ServerReference {

    private static MinecraftServer server;

    public static void setServer(MinecraftServer server) {
        ServerReference.server = server;
    }

    public static MinecraftServer getServer() {
        return server;
    }

}
