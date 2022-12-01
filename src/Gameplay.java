/** BOARD CLASS (SYSTEM)
 * @author Afonso Brás Sousa
 * Starts the game and manages the game state
 * Defines the format of the board, how many tiles and which are "special"
 * Defines 3 players and their playing order
 * Updates the position of each player after each turn
 */

/* TODO (v = done)
ALTERAR JOGO
- Criar método que dê reset ao jogo quando alguém ganha
v Ganha o jogo quem atingir a última casa ou quem conseguir 6+3 na primeira jogada
v Multa com pena de 1-4 jogadas
v 3 tipos de penalização - caranguejo, inferno (volta à 1ª casa) e morte (expulsa o jogador)
    - Se alguém cair na morte o jogo continua excepto se só sobrar 1 jogador
v VARRER SE A JOGADA FOI VENCEDORA NO FINAL DE CADA JOGADA (em vez de varrer no final se existe algum vencedor) -- erro do 1º projecto
 */

public class Gameplay {
    //Constants
    //TODO

    //Instance variables
    private final int lastTile; //how many tiles //Pre: >=10 && <=150
    private final int[] board; //tile array, saves each tile's "type"
    private final Player[] alivePlayers; //players array in order
    private int nextPlayer; //defines who plays next
    private boolean deathOccurred;

    /** Constructor
     * Defines the inicial board state
     * TODO @param etc
     */
    public Gameplay(int[] board, String playerOrder) {
        this.board = board;
        lastTile = board.length-1;

        //Populates the player list in order of play and sets the first player to start
        alivePlayers = populatePlayers(playerOrder);
        nextPlayer = 0;
    }

    /**
     * Creates the player array
     * TODO @param etc
     */
    private Player[] populatePlayers(String playersString) {
        char[] playerOrder = playersString.toCharArray();
        Player[] players = new Player[playerOrder.length];
        for (int i=0; i<playerOrder.length; i++) {
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
        int i=alivePlayers.length-1;
        while ( i>=0 && searchColor != alivePlayers[i].getColor() ) {
            i--;
        }
        return i; //if not found ==> i=-1;
    }


    /** Player command
     * @return color of the next player to roll the dice
     */
    public char getNextPlayer() {
        return alivePlayers[nextPlayer].getColor();
    }

    /** Square command
     * @param index - index of the requested player in the players array
     * @return position of the requested player
     */
    public int getPlayerSquare(int index) {
        return alivePlayers[index].getPosition();
    }

    /** Status command
     * @param index - index of the requested player in the players array
     * @return boolean - can the requested player roll the dice when it's their turn?
     */
    public boolean getPlayerStatus(int index) {
        return alivePlayers[index].canPlay();
    }

    /** Dice command
     * Rolls the dice and processes one turn
     * TODO @param
     */
    //TODO método enorme - separar em métodos mais pequenos
    public void processNextTurn(int diceLow, int diceHigh) {
        Player player = alivePlayers[nextPlayer];
        int position = player.getPosition();
        int nextPosition, diceResult;

        //Movement start
        if (position == 1 && diceLow == 3 && diceHigh == 6) { //special case - instant win
            nextPosition = lastTile;
        } else {
            diceResult = diceLow + diceHigh;
            nextPosition = Math.min(position + diceResult, lastTile); //no out-of-bounds
            int type = getSquareType(nextPosition);

            switch (type) {
                case BoardGen.INT_BIRD: //bird tile
                    nextPosition = Math.min(nextPosition+BoardGen.BIRD_MULT, lastTile); //no o.o.b.
                    break;
                case BoardGen.INT_FALL_CRAB: //crab tile
                    nextPosition = Math.max(position - diceResult, 0); //no out-of-bounds
                    break;
                case BoardGen.INT_FALL_HELL: //hell tile
                    nextPosition = 0;
                    break;
                case BoardGen.INT_FALL_DEATH: //death tile
                    if (!deathOccurred) {
                        killPlayer(player); //TODO mata o jogador e retira-o do array alivePlayers mas o jogo continua EXCEPTO se alivePlayers.length==1
                        deathOccurred = true;
                    }
                    break;
            }

            if (type<0) { //penalty tile TODO pre 1-4 etc
                int penalty = type*-1;
                player.applyPenalty(penalty);
            }
        }

        //TODO este check fica aqui ou fica no movePlayer? Ou no passTurn? Ou num método à parte -- acho a última a melhor
        if (nextPosition == lastTile) { //checks for winner
            player.addPoint(); //TODO
            resetGame(); //TODO dá reset às posições, multas, deathOccurred e nextPlayer, mata o perdedor
        } else {
            player.movePlayer(nextPosition); //TODO move mesmo que o jogador seja morto
            passTurn();
        }
    }

    /**
     * @return Returns the "type" of the requested square
     * @param square - index of the requested square
     */
    private int getSquareType(int square) {
        return board[square];
    }

    /**
     * Passes the turn to the next player
     */
    private void passTurn() {
        nextPlayer++;
        if (nextPlayer>=alivePlayers.length) {nextPlayer=0;}
        checkTurnSkip(); //checks if next player has a penalty
    }

    /**
     * Checks if the next player is fined
     * Skips their turn if they are and lowers their penalty by 1
     */
    private void checkTurnSkip() {
        Player player = alivePlayers[nextPlayer];
        if (!player.canPlay()) { //if the player cannot play
            player.lowerPenalty(); //lower their penalty by 1
            passTurn();
        }
    }

    //TODO pre
    private void killPlayer(Player player) {
        //TODO mata o jogador e retira-o do array alivePlayers mas o jogo continua EXCEPTO se alivePlayers.length==1
    }

    //TODO pre
    private void resetGame() {
        //TODO dá reset às posições, multas, deathOccurred e nextPlayer, mata o perdedor
    }

    /**
     * @return boolean - is the tournament over?
     */
    public boolean isTournamentOver() {
        //TODO
        return false;
    }

    public char getWinner() {
        //TODO get cup winner
        return '0';
    }
}
