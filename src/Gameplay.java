/** BOARD CLASS (SYSTEM)
 * @author Afonso Brás Sousa
 * @author Alexandre Cristóvão
 * Starts the game and manages the game state
 * Defines the format of the board, how many tiles and which are "special"
 * Defines 3 players and their playing order
 * Updates the position of each player after each turn
 */

/* TODO (v = done)
ALTERAR JOGO
v Criar método que dê reset ao jogo quando alguém ganha
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
    private Player[] players; //players array in order
    private int size; //number of alive players
    private int nextPlayer; //defines who plays next
    private boolean deathOccurred;
    private int turnNumber;
    private int gamesPlayed;
    private boolean cupOver;

    /** Constructor
     * Defines the inicial board state
     * TODO @param etc
     */
    public Gameplay(int[] board, String playerOrder) {
        this.board = board;
        lastTile = board.length-1;

        //Populates the player list in order of play and sets the first player to start
        players = populatePlayers(playerOrder);
        nextPlayer = 0;
        size = players.length;
        gamesPlayed = 0;
        turnNumber = 0;
        cupOver = false;
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
            players[i] = new Player(playerColor, i);
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
        return players[index].canRollDice();
    }

    public int getPlayerHealth(int index) {
        return players[index].getDeathOrder();
    }
    /** Dice command
     * Rolls the dice and processes one turn
     * TODO @param
     */
    //TODO método enorme - separar em métodos mais pequenos
    public void processNextTurn(int diceLow, int diceHigh) {
        Player player = players[nextPlayer];
        int position = player.getPosition();
        int nextPosition, diceResult;

        //Movement start
        if (turnNumber < size - gamesPlayed && diceLow == 3 && diceHigh == 6) { //special case - instant win //TODO pode estar na posição 0 sem ser a 1ª jogada
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
                        player.kill(gamesPlayed);
                        deathOccurred = true;
                        if (size - gamesPlayed == 2) { //if there were only 2 alive players left
                            cupOver = true;
                            int winner = searchPlayer(getWinner());
                            players[winner].addPoint();
                            gamesPlayed++;
                        }
                    }
                    break;
            }

            if (type<0) { //penalty tile TODO pre 1-4 etc
                int penalty = type*-1;
                player.applyPenalty(penalty);
            }
            turnNumber++;
        }

        player.movePlayer(nextPosition); //TODO move mesmo que o jogador seja morto

        //TODO este check fica aqui ou fica no movePlayer? Ou no passTurn? Ou num método à parte -- acho a última a melhor

        if (nextPosition == lastTile) { //checks for winner
            player.addPoint();

            if (!deathOccurred) {
                Player lastPlayer = aliveIt().next();
                lastPlayer.kill(gamesPlayed);
            }

            gamesPlayed++;

            if (gamesPlayed < size - 1) {
                resetGame();
            } else {
                cupOver = true;
            }

        } else {
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

    //TODO pre
    private void resetGame() {
        nextPlayer=0; // resets to 1st player
        while (players[nextPlayer].getDeathOrder()!=0) {nextPlayer++;}
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

    public char getWinner() {
        /**
         * Pre: isCupOver==true;
         * @return char - Returns cup Winner
         */
        return rankIt().next().getColor();
    }

    //Call iterator
    public PlayerIterator iterator() {
        return new PlayerIterator(players, size);
    }

    //RANKING - Sortered iterator
    public PlayerIterator rankIt() {
        Player[] rankedPlayers = new Player[size];
        for (int i = 0; i < size; i++) {
            rankedPlayers[i] = players[i];
        }
        sort(rankedPlayers, size);
        return new PlayerIterator(rankedPlayers, size);
    }

    //ALIVE - Filtered and sortered iterator
    public PlayerIterator aliveIt() {
        Player[] alivePlayers = new Player[size - gamesPlayed]; //morre 1 jogador por cada jogo
        int j=0;
        for (int i = 0; i < size; i++) {
            if (players[i].getDeathOrder()==0) { //check for condition
                alivePlayers[j++] = players[i];
            }
        }
        sortAlive(alivePlayers, size - gamesPlayed);
        return new PlayerIterator(alivePlayers, j);
    }


    private void sort(Player[] list, int size) {
        for (int i=0; i < size-1; i++) {
            int idx = i;
            for (int j=i+1; j< size; j++) {
                if (list[j].rankedCompare(list[idx]) > 0) {
                    idx = j;
                }
            }
            Player tmp = list[i];
            list[i] = list[idx];
            list[idx] = tmp;
        }
    }

    //sorts alive players by position on the board
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
