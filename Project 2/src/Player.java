/** PLAYER CLASS
 * @author Afonso Brás Sousa
 * @author Alexandre Cristóvão
 * Initializes each player
 * Object with 6 variables stored: color (char), position (int), penalty (int), score (int),
 * order of death (int) and order of play (int)
 */

public class Player {
    //Constants
    private static final int START_POSITION = 0;
    private static final int START_PENALTY = 0;
    private static final int START_SCORE = 0;
    private static final int START_DEATHORDER = 0;

    //Variables that define each player
    private final char color; //Pre: unique character ('A' - 'Z')
    private int position; //Pre: >=0 && <=Board.tileNumber-1
    private int penalty; //Pre: >=0
    private int score; //Pre: >=0
    private int deathOrder; //Pre: <=0 (0 == not dead; <0 == game in which they died)
    private final int playOrder; //Pre: >=0

    /** Constructor
     * Creates player object
     * @param color - character representing the player's color
     * @param order - integer representing the player's playing order
     * pre: color must be a unique capital letter
     */
    public Player(char color, int order) {
        this.color = color;
        this.position = START_POSITION; //assumes default position
        this.penalty = START_PENALTY; //assumes default penallty
        this.score = START_SCORE; //assumes default score
        this.deathOrder = START_DEATHORDER; //assumes deathOrder as 0
        this.playOrder = order;
    }

    //Methods
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
     * @return int - the game in which the player died (negative number) or 0 if not dead
     */
    public int getDeathOrder() {return deathOrder;}

    /**
     * @return boolean - if the player can roll the dice
     */
    public boolean canRollDice() {
        return penalty==0;
    }

    /**
     * @return int - the order in which the player plays
     */
    public int getPlayOrder()   {return playOrder;}

    /**
     * Updates the player's position
     * @param newPosition - integer with the player's new position
     * Pre: player is alive
     * pre: must be a valid position ( checked by Gameplay.processNextTurn() )
     */
    public void movePlayer(int newPosition) {
        position = newPosition;
    }

    /**
     * Lowers the player's penalty by 1
     * pre: player is alive
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
     * pre: player is alive
     */
    public void addPoint() {this.score++;}

    /**
     * Stops player from playing
     * pre: player is alive
     * @param gamesPlayed - the game in which the player died (0 = first game, 1 = second etc)
     */
    public void kill(int gamesPlayed) {
        deathOrder = (gamesPlayed+1)*-1;
    }

    /** Comparers
     */

    /**
     * Compares Player's deathOrder status with another player
     * @param other - the second player
     * @return >0 - P1 is alive or died last; <0 - P2 is alive or died last; =0 - both alive/dead
     */
    private int compareDeathOrder(Player other)  {
        if (deathOrder < 0 && other.deathOrder < 0) {return other.getDeathOrder() - deathOrder;}
        else {return deathOrder - other.getDeathOrder();}
    }

    /**
     * Compares Player's score with another player
     * @param other - the second player
     * @return positive if P1 > P2, negative if P2 > P1 and 0 if P1=P2
     */
    private int compareScore (Player other)  {
        return score-other.getScore();
    }

    /**
     * Compares Player's posiion with another player
     * @param other - the second player
     * @return positive if P1 > P2, negative if P2 > P1 and 0 if P1=P2
     */
    public int comparePosition (Player other)  {
        return position-other.getPosition();
    }

    /**
     * Compares Player's playing order with another player
     * @param other - the second player
     * @returns who plays FIRST (ascending order) - P1 if <0, P2 if >0
     */
    private int comparePlayOrder(Player other) {
        return other.getPlayOrder()-playOrder; //ascending order
    }

    /**
     * Compare a player's rank with another player
     * Combines many compare methods in the required order to give an accurate ranking of each
     * player.
     * Modularity allows for easy management / changing of criteria order if needed
     * @param other - the second player
     * @return positive if P1 > P2, negative if P2 > P1 and 0 if P1=P2
     */
    public int compareRank(Player other) {
        if (compareDeathOrder(other)!=0) {
            return compareDeathOrder(other);
        } else if (compareScore(other)!=0) {
            return compareScore(other);
        } else if (comparePosition(other)!=0) {
            return comparePosition(other);
        } else {
            return comparePlayOrder(other);
        }
    }

    /**
     * Compares alive players
     * Used at the end of a game to determine which alive player dies
     * pre: both players are alive
     * @param other - the second player
     * @return positive if P1 > P2, negative if P2 > P1 and 0 if P1=P2
     */
    public int aliveCompare(Player other) {
        if (comparePosition(other)!=0) {
            return comparePosition(other);
        } else return comparePlayOrder(other);
    }
}