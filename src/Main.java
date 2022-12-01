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
- Tabela de classificação / comando classificação
    - 1º jogador eliminado é o último classificado, 2º o penúltimo etc etc
    - Dos vivos fica em primeiro quem tiver ganho mais jogos, em caso de empate é quem estiver mais perto da última casa, em caso de empate é quem joga primeiro

CONCLUÍDO
v Boards.txt (SECA)

- CORRIGIR (ERROS 1º PROJECTO)
    - PRÉ-CONDIÇÕES MÉTODOS PÚBLICOS - fazer no fim
    - VARRER SE A JOGADA FOI VENCEDORA NO FINAL DE CADA JOGADA (em vez de varrer no final se existe algum vencedor)
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
    //TODO comando classificação
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
     * @param board - the game board
     * @param in - Scanner input
     */
    private static void executeCmdLoop(Gameplay game, Scanner in) {
        String cmd, arg;
        do {
            cmd = in.next(); //command
            arg = in.nextLine(); //argument
            switch (cmd) {
                case CMD_PLAYER:
                    //invalidates the command if there's anything written after "player"
                    if (!arg.equals("")) {System.out.println("Invalid command");}
                    else {printNextPlayer(game);}
                    break;
                case CMD_SQUARE: printPlayerSquare(game, arg); break;
                case CMD_STATUS: printPlayerStatus(game, arg); break;
                //TODO comando ranking
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
        int[] dice = new int[diceString.length-1]; //ignores the first element (space)
        for (int i = 0; i < diceString.length-1; i++) {
            dice[i] = Integer.valueOf(diceString[i+1]);
        }
        return dice;
    }

    /** Player command
     * Prints the next player to roll the dice
     * @param board - the game board
     */
    private static void printNextPlayer(Gameplay game) {
        if (game.isGameOver()) {
            System.out.println("The game is over");1
        } else {
            System.out.printf("Next to play: %c\n", game.getNextPlayer());
        }
    }

    /** Square command
     * Prints the position (tile) of the requested player
     * @param board - the game board
     * @param player - the requested player's color
     */
    private static void printPlayerSquare(Gameplay game, String player) {
        if (player.length()!=2) { //space + 1 character, otherwise invalid player
            System.out.println("Nonexistent player");
        } else {
            char color = player.charAt(1);
            int index = game.searchPlayer(color);
            if (index == -1) { //player not found
                System.out.println("Nonexistent player");
            } else {
                //The position P of the player object corresponds to the square P+1
                System.out.printf("%c is on square %d\n", color, game.getPlayerSquare(index) + 1);
            }
        }
    }

    /** Status command
     * Prints if the requested player can roll the dice when it's their turn
     * @param board - the game board
     * @param player - the requested player's color
     */
    private static void printPlayerStatus(Gameplay game, String player) {
        if (player.length()!=2) { //space + 1 character, otherwise invalid player
            System.out.println("Nonexistent player");
        } else {
            char color = player.charAt(1);
            int index = game.searchPlayer(color);
            if (index == -1) { //player not found
                System.out.println("Nonexistent player");
            } else if (game.isGameOver()) {
                System.out.println("The game is over");
            } else if (game.getPlayerStatus(index)) {
                System.out.printf("%c can roll the dice\n", color);
            } else {
                System.out.printf("%c cannot roll the dice\n", color);
            }
        }
    }

    /** Dice command
     * Processes if the dice roll is valid and updates the board accordingly
     * @param board - the game board
     * @param dice1 - the first dice's value
     * @param dice2 - the second dice's value
     */
    private static void rollDice(Gameplay game, int dice1, int dice2) {
        int diceLow, diceHigh; //sorts dice
        if (dice1<dice2) {diceLow = dice1; diceHigh = dice2;}
        else {diceLow = dice2; diceHigh = dice1;}

        if (diceLow<1 || diceHigh > 6) {
            System.out.println("Invalid dice");
        } else if (game.isGameOver()) {
            System.out.println("The game is over");
        } else {
            game.processNextTurn(diceLow, diceHigh);
        }
    }

    /** Exit command
     * Checks if the game is over and prints who won (if available)
     * @param board - the game board
     */
    private static void printExitStatus(Gameplay game) {
        if (game.isGameOver()) {
            System.out.printf("%c won the game!\n",game.getWinner());
        } else {
            System.out.println("The game was not over yet...");
        }
    }
}