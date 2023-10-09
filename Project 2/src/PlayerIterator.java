/** PlayerIterator CLASS
 * @author Afonso Brás Sousa
 * @author Alexandre Cristóvão
 * Allows other classes to get each player in the necessary order and/or filters without
 * accessing or managing the array directly
 */

public class PlayerIterator {
    private final Player[] players;
    private final int size;
    private int nextIndex;

    /**
     * Constructor
     * @param players - the Player object array to be listed
     * @param size - the size of the array to be listed
     */
    public PlayerIterator(Player[] players, int size) {
        this.players = players;
        this.size = size;
        this.nextIndex = 0;
    }

    //Methods
    /**
     * @return true if there is any element left to list
     */
    public boolean hasNext() {
        return nextIndex < size;
    }

    /**
     * @return the next player object in the array
     */
    public Player next() {
        return players[nextIndex++];
    }
}