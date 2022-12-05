/** MAIN CLASS
 * @author
 * Responsible for interacting with the end user
 * Receives player input, processes commands and generates program output
*/

/* TODO
DÚVIDAS:
- Gameplay gere 1 jogo e Main gere o torneio? OU gameplay gere ambos?

IMPLEMENTAR TORNEIO
- J jogadores - J-1 jogos
- Eliminar 1 jogador em cada jogo
- Ganha o torneio quem for o único jogador vivo restante
- Alterar os restantes comandos para mostrar “The cup is over” ou “Eliminated player” quando necessário
v Tabela de classificação / comando classificação
    - CONVERTER ISALIVE BOOLEAN PARA INT
    - 1º jogador eliminado é o último classificado, 2º o penúltimo etc etc
    v Dos vivos fica em primeiro quem tiver ganho mais jogos, em caso de empate é quem estiver mais perto da última casa, em caso de empate é quem joga primeiro

CONCLUÍDO
v Boards.txt (SECA)

- CORRIGIR (ERROS 1º PROJECTO)
    - PRÉ-CONDIÇÕES MÉTODOS PÚBLICOS - fazer no fim
    v VARRER SE A JOGADA FOI VENCEDORA NO FINAL DE CADA JOGADA (em vez de varrer no final se existe algum vencedor)
 */

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    //Constants
    private static final String CMD_PLAYER = "player";
    private static final String CMD_SQUARE = "square";
    private static final String CMD_STATUS = "status";
    private static final String CMD_DICE = "dice";
    private static final String CMD_EXIT = "exit";
    private static final String CMD_RANKING = "ranking";
    private static final String BOARD_FILE_NAME = "boards.txt";

    /** MAIN
     * Receives input, processes the player order, creates the board, starts the interpreter
     */
    // TODO alterar o input para esta ordem: ordemjogadores; numTabuleiro; comandos...; exit

    public static void main(String[] args) throws FileNotFoundException {
        //Input start
        Scanner in = new Scanner(System.in);

        //Processes the player order
        String playerOrder = in.next(); in.nextLine(); //Pre: 3-10 different capital letters

        //Creates the board
        int boardNumber = in.nextInt();in.nextLine();
        int[] board = new BoardGen(BOARD_FILE_NAME, boardNumber).getBoard();

        //Starts a game
        Gameplay game = new Gameplay(board, playerOrder);

        //Processes commands
        executeCmdLoop(game, in);
        in.close();
    }

    /* Methods */

    /** Command interpreter
     * Interprets and executes commands while cmd !=exit
     * Prints output
     * @param game - the game state
     * @param in - Scanner input
     */
    private static void executeCmdLoop(Gameplay game, Scanner in) {
        String cmd, arg;
        do {
            cmd = in.next(); //command
            arg = in.nextLine().trim(); //argument
            switch (cmd) {
                case CMD_PLAYER:
                    //invalidates the command if there's anything written after "player"
                    if (!arg.equals("")) {System.out.println("Invalid command");}
                    else {printNextPlayer(game);}
                    break;
                case CMD_SQUARE: printPlayerSquare(game, arg); break;
                case CMD_STATUS: printPlayerStatus(game, arg); break;
                case CMD_RANKING: printPlayerRanking(game); break;
                case CMD_DICE:
                    int[] dice = splitArg(arg); //Pre: 2 integers
                    rollDice(game, dice[0], dice[1]);
                    break;
                case CMD_EXIT: printExitStatus(game); break;
                default:
                    System.out.println("Invalid command");
            }
        } while (!cmd.equals(CMD_EXIT));
    }

    /**
     * Splits the dice command argument into 2 integers
     * @param arg - the argument after the "dice" command
     * pre: arg == "space + integer1 + space + integer2"
     * @return dice - array with 2 integers (dice values)
     */
    private static int[] splitArg(String arg) {
        String[] diceString = arg.split(" ");
        int[] dice = new int[diceString.length];
        for (int i = 0; i < diceString.length; i++) {
            dice[i] = Integer.valueOf(diceString[i+1]);
        }
        return dice;
    }

    /** Player command
     * Prints the next player to roll the dice
     * @param game - the game state
     */
    private static void printNextPlayer(Gameplay game) {
        if (game.isTournamentOver()) {
            System.out.println("The cup is over");
        } else {
            System.out.printf("Next to play: %c\n", game.getNextPlayer());
        }
    }

    /** Square command
     * Prints the position (tile) of the requested player
     * @param game - the game state
     * @param player - the requested player's color
     */
    private static void printPlayerSquare(Gameplay game, String player) {
        if (player.length()!=1) { //1 character, otherwise invalid player
            System.out.println("Nonexistent player");
        } else {
            char color = player.charAt(0);
            int index = game.searchPlayer(color);
            if (index == -1) { //player not found
                System.out.println("Nonexistent player");
            } else if (!game.getPlayerHealth(index))    {
                System.out.println("Eliminated player");
            } else {
                //The position P of the player object corresponds to the square P+1
                System.out.printf("%c is on square %d\n", color, game.getPlayerSquare(index) + 1);
            }
        }
    }

    /** Status command
     * Prints if the requested player can roll the dice when it's their turn
     * @param game - the game state
     * @param player - the requested player's color
     */
    private static void printPlayerStatus(Gameplay game, String player) {
        if (player.length()!=1) { //1 character, otherwise invalid player
            System.out.println("Nonexistent player");
        } else {
            char color = player.charAt(0);
            int index = game.searchPlayer(color);
            if (index == -1) { //player not found
                System.out.println("Nonexistent player");
            } else if (game.isTournamentOver()) {
                System.out.println("The cup is over");
            }   else if (!game.getPlayerHealth(index))  {
                System.out.println("Eliminated player");
            } else if (game.getPlayerStatus(index)) {
                System.out.printf("%c can roll the dice\n", color);
            } else {
                System.out.printf("%c cannot roll the dice\n", color);
            }
        }
    }

    //TODO comando-ranking
    private static void printPlayerRanking(Gameplay game) {
        PlayerIterator it = game.rankIt();
        while (it.hasNext()) { //Run iterator
            Player pl = it.next();
            if (pl.isAlive()) {
                System.out.printf("%c: %d games won; on square %d.\n", pl.getColor(), pl.getScore(), pl.getPosition());
            } else {
                System.out.printf("%c: %d games won; eliminated.\n", pl.getColor(), pl.getScore());
            }
        }

    }


    /** Dice command
     * Processes if the dice roll is valid and updates the board accordingly
     * @param game - the game state
     * @param dice1 - the first dice's value
     * @param dice2 - the second dice's value
     */
    private static void rollDice(Gameplay game, int dice1, int dice2) {
        int diceLow, diceHigh; //sorts dice
        if (dice1<dice2) {diceLow = dice1; diceHigh = dice2;}
        else {diceLow = dice2; diceHigh = dice1;}

        if (diceLow<1 || diceHigh > 6) {
            System.out.println("Invalid dice");
        } else if (game.isTournamentOver()) {
            System.out.println("The cup is over");
        } else {
            game.processNextTurn(diceLow, diceHigh);
        }
    }

    /** Exit command
     * Checks if the game is over and prints who won (if available)
     * @param game - the game state
     */
    private static void printExitStatus(Gameplay game) {
        if (game.isTournamentOver()) {
            System.out.printf("%c won the cup!\n",game.getWinner()); //TODO get cup winner
        } else {
            System.out.println("The cup was not over yet...");
        }
    }
}