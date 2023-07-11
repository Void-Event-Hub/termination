package synthesyzer.termination.network.packets.servertoclient;

import synthesyzer.termination.data.kills.MultiKill;

public record PlayerKillPacket(MultiKill multiKill, String victimName) {
}
