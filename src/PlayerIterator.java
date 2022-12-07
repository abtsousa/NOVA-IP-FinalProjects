public class PlayerIterator {
    private final Player[] players;
    private final int size;
    private int nextIndex;

    public PlayerIterator(Player[] players, int size) {
        this.players = players;
        this.size = size;
        this.nextIndex = 0;
    }

    public boolean hasNext() {
        return nextIndex < size;
    }

    public Player next() {
        return players[nextIndex++];
    }
}
