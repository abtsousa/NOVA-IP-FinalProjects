/** BOARD CLASS (SYSTEM)
 * @author Afonso Brás Sousa
 * Starts the game and manages the game state
 * Defines the format of the board, how many tiles and which are "special"
 * Defines 3 players and their playing order
 * Updates the position of each player after each turn
 */

/* TODO
ALTERAR JOGO
- Criar método que dê reset ao jogo quando alguém ganha
- Ganha o jogo quem atingir a última casa ou quem conseguir 6+3 na primeira jogada
- Multa com pena de 1-4 jogadas
- 3 tipos de penalização - caranguejo, inferno (volta à 1ª casa) e morte (expulsa o jogador)
    - Se alguém cair na morte o jogo continua excepto se só sobrar 1 jogador
- VARRER SE A JOGADA FOI VENCEDORA NO FINAL DE CADA JOGADA (em vez de varrer no final se existe algum vencedor) -- erro do 1º projecto
 */

public class Gameplay {
    //Constants
    private static final int NUMBER_OF_PLAYERS = 3;
    private static final int BIRD_TILE_MULT = 9; //defines a bird tile every N tiles
    private static final char BIRD_CHAR = 'B'; //bird character
    private static final char PENALTY_CHAR = 'P'; //penalty character
    private static final char FALL_CHAR = 'F'; //fall character

    //Instance variables
    private final int tileNumber; //how many tiles //Pre: >=10 && <=150
    private final char[] boardTiles; //tile array, saves each tile's "type"
    private final Player[] players; //players array in order
    private int nextPlayer; //defines who plays next

    /** Constructor
     * Defines the inicial board state
     * @param playerOrder - the order in which each player plays
     *   pre: 3 capital unique letters
     * @param tileNumber - the number of tiles of the board
     *   pre: >=10 && <=150
     * @param penaltyTiles - which tiles are marked as "penalty"
     *   pre: >=1 && <=tileNumber-2 && size>=1 && size<=(tileNumber/3)
     * @param fallTiles - which tiles are marked as "fall"
     *   pre: >=1 && <=tileNumber-2 && size>=1 && size<=(tileNumber/3)
     */
    public Gameplay(String playerOrder, int tileNumber, int[] penaltyTiles, int[] fallTiles) {
        this.tileNumber = tileNumber;

        //TODO a maioria do que aqui está passa a ser feito pelo BoardGen e pode ser apagado até ter apenas 2 argumentos (playerOrder e board)

        //Initializes the board
        boardTiles = new char[tileNumber];

        //Populates the board with "special" tiles
        populateBoard(BIRD_CHAR, birdTiles());
        populateBoard(PENALTY_CHAR, penaltyTiles);
        populateBoard(FALL_CHAR, fallTiles);

        //Populates the player list in order of play and sets the first player to start
        players = populatePlayers(playerOrder);
        nextPlayer = 0;
    }

    /**
     * Creates the player array
     * @param playersString - each player's letter in order
     *   pre: 3 unique capital letters
     * @return players - array with each player object, in order of play
     */
    private Player[] populatePlayers(String playersString) {
    char[] playerOrder = playersString.toCharArray();
    Player[] players = new Player[NUMBER_OF_PLAYERS];
    for (int i=0; i<NUMBER_OF_PLAYERS; i++) {
        char playerColor = playerOrder[i];
        players[i] = new Player(playerColor);
        }
    return players;
    }

    /**
     * Searches for a player by their color
     * @param searchColor - requested player's color
     * @return i - integer with index / position of the player in the players array
     * @return -1 if no player found
     */
    public int searchPlayer(char searchColor) {
        int i=NUMBER_OF_PLAYERS-1;
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
     * @return position of the requested player
     */
    public int getPlayerSquare(int index) {
        return players[index].getPosition();
    }

    /** Status command
     * @param index - index of the requested player in the players array
     * @return boolean - can the requested player roll the dice when it's their turn?
     */
    public boolean getPlayerStatus(int index) {
        return players[index].canPlay();
    }

    /** Dice command
     * Rolls the dice and processes one turn
     * @param diceResult - sum of the dice values
     *   pre: diceResult == valid integer between 2 and 12
     */
    public void processNextTurn(int diceResult) {
        Player player = players[nextPlayer];
        int position = player.getPosition();

        //Movement start
        int nextPosition = Math.min(position + diceResult, tileNumber-1); //no out-of-bounds
        char type = getSquareType(nextPosition);

        switch (type) {
            case FALL_CHAR:
                nextPosition = Math.max(position - diceResult, 0); //no out-of-bounds
                break;
            case BIRD_CHAR:
                nextPosition = Math.min(nextPosition + 9, tileNumber-1); //no out-of-bounds
                break;
            case PENALTY_CHAR:
                player.applyPenalty(2);
                break;
        }

        player.movePlayer(nextPosition);

        passTurn();
    }

    /**
     * @return Returns the "type" of the requested square
     * @param square - index of the requested square
     */
    private char getSquareType(int square) {
        return boardTiles[square];
    }

    /**
     * Passes the turn to the next player
     */
    private void passTurn() {
        nextPlayer++;
        if (nextPlayer>=NUMBER_OF_PLAYERS) {nextPlayer=0;}
        checkTurnSkip(); //checks if next player has a penalty
    }

    /**
     * Checks if the next player is fined
     * Skips their turn if they are and lowers their penalty by 1
     */
    private void checkTurnSkip() {
        Player player = players[nextPlayer];
        if (!player.canPlay()) { //if the player cannot play
            player.lowerPenalty(); //lower their penalty by 1
            passTurn();
        }
    }

    /**
     * Searches for a winner
     * @return index of the winner in the players array
     * @return -1 if no winner found
     */
    private int searchForWinner() { //Pre: only 1 winner allowed
        int i=NUMBER_OF_PLAYERS-1;
        while (i>=0 && players[i].getPosition()+1!=tileNumber) { //position N == square N+1
            i--;
        }
        return i; //if not found ==> i == -1;
    }

    /**
     * @return Returns the winning player's color
     * pre: isGameOver == TRUE
     */
    public char getWinner() {
        return players[searchForWinner()].getColor();
    }

    /**
     * @return boolean - is the game over?
     */
    public boolean isGameOver() {
        return (searchForWinner() != -1);
    }
}
