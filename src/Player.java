/** PLAYER CLASS
 * @author Afonso Brás Sousa
 * @author Alexandre Cristóvão
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
    private static final int START_SCORE = 0;
    private static final boolean START_ISALIVE = true;

    //Variables that define each player
    private final char color; //Pre: unique character ('A' - 'Z')
    private int position; //Pre: >=0 && <=Board.tileNumber-1
    private int penalty; //Pre: >=0
    private int score; //Pre: >=0
    private boolean isAlive;
    private int order; //Pre: >0

    //Constructor

    /** Constructor
     * Creates player object
     * @param color - character representing the player's color
     * pre: color must be a unique capital letter
     */
    public Player(char color, int order) {
        this.color = color;
        this.position = START_POSITION; //assumes default position
        this.penalty = START_PENALTY; //assumes default penallty
        this.score = START_SCORE; //assumes default score
        this.isAlive = START_ISALIVE; //assumes default isAlive
        this.order = order;
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
     * @return int - the player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * @return boolean - if the player can play
     */
    public boolean isAlive() {return isAlive;}

    /**
     * @return boolean - if the player can roll the dice
     */
    public boolean canRollDice() {
        return penalty==0;
    }

    /**
     * @return int - the player's creation order
     */
    public int getOrder()   {return order;}

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

    /**
     * Adds a point to the player's score
     */
    public void addPoint() {this.score++;}

    /**
     * Stops player from playing
     */
    public void kill() {
        isAlive=false;
    }

    /**
     * Compares Player's isAlive status
     */
    private int compareAlive (Player other)  {
        if (isAlive==other.isAlive()) {
            return 0;
        } else if (isAlive)   {
            return 1;
        }  else {
            return -1;
        }
    }

    /**
     * Compares Player's score
     */
    private int compareScore (Player other)  {
        return score-other.getScore();
    }

    /**
     * Compares Player's posiion
     */
    private int comparePosition (Player other)  {
        return position-other.getPosition();
    }

    private int comparePlayOrder(Player other) {
        return other.getOrder()-order;
    }

    public int nestedCompare(Player other) {
        if (compareAlive(other)!=0) {
            return compareAlive(other);
        } else if (compareScore(other)!=0) {
            return compareScore(other);
        } else if (comparePosition(other)!=0) {
            return comparePosition(other); //ordem crescente
        } else {
            return comparePlayOrder(other);
        }
    }
}