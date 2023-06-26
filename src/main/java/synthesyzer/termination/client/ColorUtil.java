package synthesyzer.termination.client;


import net.minecraft.util.Formatting;

public class ColorUtil {

    public static int toHex(Formatting formatting) {
        if (formatting == Formatting.BLACK) {
            return 0x000000;
        }
        if (formatting == Formatting.DARK_BLUE) {
            return 0x0000AA;
        }
        if (formatting == Formatting.DARK_GREEN) {
            return 0x00AA00;
        }
        if (formatting == Formatting.DARK_AQUA) {
            return 0x00AAAA;
        }
        if (formatting == Formatting.DARK_RED) {
            return 0xAA0000;
        }
        if (formatting == Formatting.DARK_PURPLE) {
            return 0xAA00AA;
        }
        if (formatting == Formatting.GOLD) {
            return 0xFFAA00;
        }
        if (formatting == Formatting.GRAY) {
            return 0xAAAAAA;
        }
        if (formatting == Formatting.DARK_GRAY) {
            return 0x555555;
        }
        if (formatting == Formatting.BLUE) {
            return 0x5555FF;
        }
        if (formatting == Formatting.GREEN) {
            return 0x55FF55;
        }
        if (formatting == Formatting.AQUA) {
            return 0x55FFFF;
        }
        if (formatting == Formatting.RED) {
            return 0xFF5555;
        }
        if (formatting == Formatting.LIGHT_PURPLE) {
            return 0xFF55FF;
        }
        if (formatting == Formatting.YELLOW) {
            return 0xFFFF55;
        }

        return 0xFFFFFF;
    }

}
