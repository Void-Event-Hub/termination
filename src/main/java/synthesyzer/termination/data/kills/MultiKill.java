package synthesyzer.termination.data.kills;

public enum MultiKill {

    SINGLE_KILL("Single Kill"),
    DOUBLE_KILL("Double Kill"),
    TRIPLE_KILL("Triple Kill"),
    QUADRA_KILL("Quadra Kill"),
    PENTA_KILL("Penta Kill");

    private String title;

    MultiKill(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
