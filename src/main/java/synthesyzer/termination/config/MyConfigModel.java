package synthesyzer.termination.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import synthesyzer.termination.Termination;

@Modmenu(modId = Termination.MOD_ID)
@Config(name = Termination.MOD_ID, wrapperName = "MyConfig")
public class MyConfigModel {

    public int nucleusProtectionRadius = 10;
    public int playerDeathCooldown = 10;
    public int maxNucleusHealth = 150;
    public int damagePerNucleusBreak = 1;
    public int damagePerPlayerDeath = 0;
    public int expAwardedOnPlayerKill = 55;
    public boolean clearInventoryOnDeath = false;
    public double chanceToDropItemOnDeath = 0.4;
    public double damageDealtToToolsOnDeath = 0.1;
    public int minutesUntilPhase2 = 60;
    public int minutesUntilEndPhase = 1;
}

