package synthesyzer.termination.client;

public class ClientMultiKillData {

    private static int tickOfMultiKill = 0;

    public static int getTickOfMultiKill() {
        return tickOfMultiKill;
    }

    public static void setTickOfMultiKill(int tickOfMultiKill) {
        ClientMultiKillData.tickOfMultiKill = tickOfMultiKill;
    }
}
