/** GAMEPLAY CLASS (SYSTEM)
 * @author Afonso Brás Sousa
 * @author Alexandre Cristóvão
 * Starts the game and manages the game state
 * Defines the format of the board, how many tiles and which are "special"
 * Defines 3 players and their playing order
 * Updates the position of each player after each turn
 */

public class Gameplay {
    //Constants
    private static final int BIRD_JUMP = 9; //How many tiles do players advance on a bird tile
    private static final int SPECIALDICE_LOW = 3; //Lowest dice value for special case
    private static final int SPECIALDICE_HIGH = 6; //Highest dice value for special case


    //Instance variables
    private final int lastTile; //how many tiles //Pre: >=10 && <=150
    private final int[] board; //tile array, saves each tile's "type"
    private final Player[] players; //players array in order
    private final int size; //number of players //Pre: >=3 && <=10
    private int nextPlayer; //defines who plays next
    private boolean deathOccurred;
    private int turnNumber;
    private int alivePlayers;
    private boolean cupOver;

    /** Constructor
     * Defines the inicial board state
     * @param board - integer array representing the board's tiles
     * @param playerOrder - string representing the players who'll play the game
     * pre: board must be valid according to the specifications mentioned in the BoardGen class
     * pre: playerOrder must have between 3 and 10 unique capital letters
     */
    public Gameplay(int[] board, String playerOrder) {
        this.board = board;
        lastTile = board.length-1;

        //Populates the player list in order of play and sets the first player to start
        players = populatePlayers(playerOrder);
        nextPlayer = 0;
        size = players.length;
        alivePlayers = size;
        turnNumber = 0;
        cupOver = false;
    }

    //Methods
    /**
     * Creates the player array
     * @param playersString - string with each player's character in their playing order
     *                      - Pre: 3-10 unique capital letters
     * @return array of players
     */
    private Player[] populatePlayers(String playersString) {
        char[] playerOrder = playersString.toCharArray();
        Player[] players = new Player[playerOrder.length];
        for (int i=0; i<playerOrder.length; i++) {
            char playerColor = playerOrder[i];
            players[i] = new Player(playerColor, i);
            }
        return players;
    }

    /**
     * Searches for a player by their color
     * @param searchColor - requested player's color
     * @return i - integer with index of the player in the players array
     * @return -1 if no player found
     */
    public int searchPlayer(char searchColor) {
        int i= players.length-1;
        while ( i>=0 && searchColor != players[i].getColor() ) {
            i--;
        }
        return i; //if not found ==> i=-1;
    }


    /** Player command
     * @return color of the next player to roll the dice
     */
    public char getNextPlayer() {
        return players[nextPlayer].getColor();
    }

    /** Square command
     * @param index - index of the requested player in the players array
     *              - Pre: index >=0 && index < size
     * @return position of the requested player
     */
    public int getPlayerSquare(int index) {
        return players[index].getPosition();
    }

    /** Status command
     * @param index - index of the requested player in the players array
     *              - Pre: index >=0 && index < size
     * @return boolean - can the requested player roll the dice when it's their turn?
     */
    public boolean getPlayerStatus(int index) {
        return players[index].canRollDice();
    }

    /**
     * @param index - index of the requested player in the players array
     *              - Pre: index >=0 && index < size
     * @return boolean - is the requested player dead?
     */
    public boolean getDeathCertificate(int index) {
        return players[index].getDeathOrder()!=0;
    }

    /**
     * Rolls the dice and processes one turn
     * @param diceLow - integer representing thrown dices' lowest value
     * @param diceHigh - integer representing thrown dices' highest value
     * Pre: both dice >=1 && <=6 (checked by Main.rolldice() )
     */
    public void processNextTurn(int diceLow, int diceHigh) {
        Player player = players[nextPlayer];
        int position = player.getPosition();
        int nextPosition, diceResult;

        //Special case - instant win
        if (turnNumber < alivePlayers && diceLow==SPECIALDICE_LOW && diceHigh==SPECIALDICE_HIGH) {
            nextPosition = lastTile;
        } else {
            diceResult = diceLow + diceHigh;
            nextPosition = nextValidPosition(player, position, diceResult);
            turnNumber++;
        }

        player.movePlayer(nextPosition);

        //Post Movement
        checkForWinner(nextPosition, player);
    }

    /** Returns the next valid position for the play
     * @param player - indicates current player
     * @param position - indicates player's position
     * @param diceResult - indicates total sum of dice thrown
     * @return the player's next position
     */
    private int nextValidPosition (Player player, int position, int diceResult) {
        int nextPosition = Math.min(position + diceResult, lastTile); //no out-of-bounds
        int type = getSquareType(nextPosition);
        if (type<0) {player.applyPenalty(type*(-1));} //penalty tile
        switch (type) {
            case BoardGen.INT_BIRD: //bird tile
                return Math.min(nextPosition+BIRD_JUMP, lastTile); //no o.o.b.
            case BoardGen.INT_FALL_CRAB: //crab tile
                return Math.max(position - diceResult, 0); //no out-of-bounds
            case BoardGen.INT_FALL_HELL: //hell tile
                return 0;
            case BoardGen.INT_FALL_DEATH: //death tile
                if (!deathOccurred) {
                    player.kill(size - alivePlayers);
                    deathOccurred = true;
                    if (alivePlayers == 2) { //if there were only 2 alive players left
                        cupOver = true;
                        int winner = searchPlayer(getWinner());
                        players[winner].addPoint();
                        alivePlayers--;
                    }
                }
            default: return nextPosition;
        }
    }

    /** Checks if the move was a winning move
     * @param position - indicates player's position
     * @param player - indicates current player
     */
    private void checkForWinner(int position, Player player) {
        if (position == lastTile) {
            player.addPoint();

            if (!deathOccurred) {
                Player lastPlayer = aliveIt().next();
                lastPlayer.kill(size - alivePlayers);
            }

            alivePlayers--;

            if (alivePlayers > 1) {resetGame();}
            else {cupOver = true;}

        } else {
            passTurn();
        }
    }

    /**
     * @param square - index of the requested square
     * @return Returns the "type" of the requested square
     */
    private int getSquareType(int square) {
        return board[square];
    }

    /**
     * Passes the turn to the next player
     */
    private void passTurn() {
        nextPlayer++;
        if (nextPlayer>= players.length) {nextPlayer=0;}
        checkTurnSkip(); //checks if next player has a penalty
    }

    /**
     * Checks if the next player is fined
     * Skips their turn if they are and lowers their penalty by 1
     */
    private void checkTurnSkip() {
        Player player = players[nextPlayer];
        if (player.getDeathOrder()!=0) { //if the player is dead
            passTurn();
        } else {
            if (!player.canRollDice()) { //if the player has any penalty
                player.lowerPenalty(); //lower their penalty by 1
                passTurn();
            }
        }
    }

    /**
     * Resets the game after a win
     */
    private void resetGame() {
        nextPlayer=0; // resets to 1st player
        while (getDeathCertificate(nextPlayer)) {nextPlayer++;} //skips to an alive player
        deathOccurred=false; // resets deathOccurred
        turnNumber=0; // resets turn number
        PlayerIterator it = iterator();
        while (it.hasNext()) {
            Player player = it.next();
            player.movePlayer(0);
            player.applyPenalty(0);
        }
    }

    /**
     * @return boolean - is the cup over?
     */
    public boolean isCupOver() {
        return cupOver;
    }

    /**
     * Pre: isCupOver==true;
     * @return char - Returns cup Winner
     */
    public char getWinner() {

        return rankIt().next().getColor();
    }

    //Iterators

    /** Call iterator
     * @return iterator with all players
     */
    public PlayerIterator iterator() {
        return new PlayerIterator(players, size);
    }

    /** RANKING - Sortered iterator
     * @return iterator with all players sorted by ranking
     */
    public PlayerIterator rankIt() {
        Player[] rankedPlayers = new Player[size];
        for (int i = 0; i < size; i++) {
            rankedPlayers[i] = players[i];
        }
        sortRank(rankedPlayers, size);
        return new PlayerIterator(rankedPlayers, size);
    }

    /** ALIVE - Filtered and sortered iterator
     * @return iterator with all alive players sorted by position and play order
     */
    public PlayerIterator aliveIt() {
        Player[] aliveArray = new Player[alivePlayers];
        int j=0;
        for (int i = 0; i < size; i++) {
            if (!getDeathCertificate(i)) { //check for condition
                aliveArray[j++] = players[i];
            }
        }
        sortAlive(aliveArray, alivePlayers);
        return new PlayerIterator(aliveArray, j);
    }

    /**
     * Sorts a Player object array by their ranking
     * @param list - the Player object array to be ranked
     * @param size - the array size
     */
    private void sortRank(Player[] list, int size) {
        for (int i=0; i < size-1; i++) {
            int idx = i;
            for (int j=i+1; j< size; j++) {
                if (list[j].compareRank(list[idx]) > 0) {
                    idx = j;
                }
            }
            Player tmp = list[i];
            list[i] = list[idx];
            list[idx] = tmp;
        }
    }

    /**
     * Sorts a Player object array to determine who to kill at the end of a game
     * @param list - the Player object array to be ranked
     *             - Pre: contains alive players
     * @param size - the array size
     */
    private void sortAlive(Player[] list, int size) {
        for (int i=0; i < size-1; i++) {
            int idx = i;
            for (int j=i+1; j< size; j++) {
                if (list[j].aliveCompare(list[idx]) < 0) {
                    idx = j;
                }
            }
            Player tmp = list[i];
            list[i] = list[idx];
            list[idx] = tmp;
        }
    }
}