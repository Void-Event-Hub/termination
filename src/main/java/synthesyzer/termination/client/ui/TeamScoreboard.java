package synthesyzer.termination.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import synthesyzer.termination.Termination;
import synthesyzer.termination.client.ClientTeamData;
import synthesyzer.termination.client.ColorUtil;
import synthesyzer.termination.data.team.TeamData;

import java.util.Comparator;
import java.util.List;

public class TeamScoreboard {

    public static void render(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!client.options.playerListKey.isPressed()) {
            return;
        }

        PlayerEntity player = client.player;

        if (player == null) {
            return;
        }

        if (player.getScoreboardTeam() == null) {
            return;
        }

        var playerTeam = ClientTeamData.getTeamData(player.getScoreboardTeam().getName());
        if (playerTeam == null) {
            return;
        }

        int maxHealth = 150; // TODO: get from config
        int i = 0;

        List<TeamData> civilizations = ClientTeamData.getTeamData()
                .values()
                .stream()
                .sorted(Comparator.comparingInt(TeamData::getHealth).reversed())
                .toList();

        for (TeamData teamData : civilizations) {
            if (teamData.getName() == null) {
                Termination.LOGGER.info("Team name is null!");
                continue;
            }
            Formatting color = player.getScoreboard().getTeam(teamData.getName()).getColor();
            String name = teamData.getName();
            int health = teamData.getHealth();

            int x = 10;
            int y = 10 * i + 30;

            client.textRenderer.drawWithShadow(matrixStack, name, x, y, ColorUtil.toHex(teamData.isDead() ? Formatting.DARK_GRAY : color));
            if (!teamData.isDead()) {
                client.textRenderer.drawWithShadow(matrixStack, "[" + health + "/" + maxHealth + "]", x + 70, y, ColorUtil.toHex(Formatting.WHITE));
            }

            i++;
        }
    }

}
