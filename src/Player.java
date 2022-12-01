/** PLAYER CLASS
 * @author Afonso Brás Sousa
 * Initializes each player
 * Object with 3 variables stored: color (char), position (int), penalty (int)
 */

/* TODO
- Gravar no objecto Player o número de vitórias de um jogador e se este está vivo ou morto
- Criar métodos para passar esses dados para as restantes classes
 */

public class Player {
    //Constants
    private static final int START_POSITION = 0;
    private static final int START_PENALTY = 0;

    //Variables that define each player
    private final char color; //Pre: unique character ('A' - 'Z')
    private int position; //Pre: >=0 && <=Board.tileNumber-1
    private int penalty; //Pre: >=0

    //Constructor

    /** Constructor
     * Creates player object
     * @param color - character representing the player's color
     * pre: color must be a unique capital letter
     */
    public Player(char color) {
        this.color = color;
        this.position = START_POSITION; //assumes default position
        this.penalty = START_PENALTY; //assumes default penallty
    }

    /** Getters
     * @return char - the player's color
     */
    public char getColor() {
        return color;
    }

    /**
     * @return int - the player's position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return boolean - if the player can play
     */
    public boolean canPlay() {
        return penalty==0;
    }

    /**
     * Updates the player's position
     * @param newPosition - integer with the player's new position
     * pre: must be a valid position ( checked by Board.processNextTurn() )
     */
    public void movePlayer(int newPosition) {
        position = newPosition;
    }

    /**
     * Lowers the player's penalty by 1
     * pre: penalty > 0
     */
    public void lowerPenalty() {
        penalty--;
    }

    /**
     * Applies a penalty to the player
     * @param penalty - the penalty to be applied
     * pre: penalty > 0
     */
    public void applyPenalty(int penalty) {this.penalty = penalty;}

    //TODO pre
    public void addPoint() {
        //TODO
    }

    //TODO pre
    public boolean isDead() {
        //TODO
        return false;
    }
}