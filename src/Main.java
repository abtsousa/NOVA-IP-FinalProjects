/** MAIN CLASS
 * @author
 * Responsible for interacting with the end user
 * Receives player input, processes commands and generates program output
*/

/* TODO
- J jogadores - J-1 jogos
- Eliminar 1 jogador em cada jogo
- Ganha o jogo quem atingir a última casa ou quem conseguir 6+3 na primeira jogada
- Ganha o torneio quem for o único jogador
- Multa com pena de 1-4 jogadas
- 3 tipos de penalização - caranguejo, inferno (volta à 1ª casa) e morte (expulsa o jogador)
    - Se alguém cair na morte o jogo continua excepto se só sobrar 1 jogador
- Tabela de classificação / comando classificação
    - 1º jogador eliminado é o último classificado, 2º o penúltimo etc etc
    - Dos vivos fica em primeiro quem tiver ganho mais jogos, em caso de empate é quem estiver mais perto da última casa, em caso de empate é quem joga primeiro
- Boards.txt (SECA)
    - não especifica o nº de tabuleiros (while….exception)
- Alterar os restantes comandos para mostrar “The cup is over” ou “Eliminated player” quando necessário

- CORRIGIR (ERROS 1º PROJECTO)
    - PRÉ-CONDIÇÕES MÉTODOS PÚBLICOS
    - VARRER SE A JOGADA FOI VENCEDORA NO FINAL DE CADA JOGADA (em vez de varrer no final se existe algum vencedor)
 */

import java.util.Scanner;

public class Main {
    //Constants
    private static final String CMD_PLAYER = "player";
    private static final String CMD_SQUARE = "square";
    private static final String CMD_STATUS = "status";
    private static final String CMD_DICE = "dice";
    private static final String CMD_EXIT = "exit";

    /** MAIN
     * Receives input, processes the player order, creates the board, starts the interpreter
     */
    public static void main(String[] args) {
        //Input start
        Scanner in = new Scanner(System.in);

        //Processes the player order
        String playerOrder = in.next(); in.nextLine(); //Pre: 3 different capital letters

        //Receives the number of tiles
        int tileNumber = in.nextInt(); in.nextLine(); //Pre: >=10 && <=150

        //Receives "special" tiles
        int[] penaltyTiles = saveTileArray(in); //Pre: >=1 && <=(tileNumber/3)
        int[] fallTiles = saveTileArray(in); //Pre: >=1 && <=(tileNumber/3)

        //Creates the board
        Gameplay board = new Gameplay(playerOrder, tileNumber, penaltyTiles, fallTiles);

        //Processes commands
        executeCmdLoop(board, in);
        in.close();
    }

    /* Methods */

    /**
     * Receives which tiles are "special" tiles and saves them into an array
     * @param in - Scanner input
     * pre: integer (array size) + \n + integers separated by space
     * @return tileArray - array with each integer
     */
    private static int[] saveTileArray(Scanner in) {
        int size = in.nextInt(); in.nextLine();
        int[] tileArray = new int[size];
        for (int i=0; i<size; i++) {
            tileArray[i] = in.nextInt();
        }
        in.nextLine();
        return tileArray;
    }

    /** Command interpreter
     * Interprets and executes commands while cmd !=exit
     * Prints output
     * @param board - the game board
     * @param in - Scanner input
     */
    private static void executeCmdLoop(Gameplay board, Scanner in) {
        String cmd, arg;
        do {
            cmd = in.next(); //command
            arg = in.nextLine(); //argument
            switch (cmd) {
                case CMD_PLAYER:
                    //invalidates the command if there's anything written after "player"
                    if (!arg.equals("")) {System.out.println("Invalid command");}
                    else {printNextPlayer(board);}
                    break;
                case CMD_SQUARE: printPlayerSquare(board, arg); break;
                case CMD_STATUS: printPlayerStatus(board, arg); break;
                case CMD_DICE:
                    int[] dice = splitArg(arg); //Pre: 2 integers
                    rollDice(board, dice[0], dice[1]);
                    break;
                case CMD_EXIT: printExitStatus(board); break;
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
    private static void printNextPlayer(Gameplay board) {
        if (board.isGameOver()) {
            System.out.println("The game is over");
        } else {
            System.out.printf("Next to play: %c\n", board.getNextPlayer());
        }
    }

    /** Square command
     * Prints the position (tile) of the requested player
     * @param board - the game board
     * @param player - the requested player's color
     */
    private static void printPlayerSquare(Gameplay board, String player) {
        if (player.length()!=2) { //space + 1 character, otherwise invalid player
            System.out.println("Nonexistent player");
        } else {
            char color = player.charAt(1);
            int index = board.searchPlayer(color);
            if (index == -1) { //player not found
                System.out.println("Nonexistent player");
            } else {
                //The position P of the player object corresponds to the square P+1
                System.out.printf("%c is on square %d\n", color, board.getPlayerSquare(index) + 1);
            }
        }
    }

    /** Status command
     * Prints if the requested player can roll the dice when it's their turn
     * @param board - the game board
     * @param player - the requested player's color
     */
    private static void printPlayerStatus(Gameplay board, String player) {
        if (player.length()!=2) { //space + 1 character, otherwise invalid player
            System.out.println("Nonexistent player");
        } else {
            char color = player.charAt(1);
            int index = board.searchPlayer(color);
            if (index == -1) { //player not found
                System.out.println("Nonexistent player");
            } else if (board.isGameOver()) {
                System.out.println("The game is over");
            } else if (board.getPlayerStatus(index)) {
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
    private static void rollDice(Gameplay board, int dice1, int dice2) {
        if (dice1<1 || dice1 >6 || dice2<1 || dice2>6) {
            System.out.println("Invalid dice");
        } else if (board.isGameOver()) {
            System.out.println("The game is over");
        } else {
            int diceResult = dice1 + dice2;
            board.processNextTurn(diceResult);
        }
    }

    /** Exit command
     * Checks if the game is over and prints who won (if available)
     * @param board - the game board
     */
    private static void printExitStatus(Gameplay board) {
        if (board.isGameOver()) {
            System.out.printf("%c won the game!\n",board.getWinner());
        } else {
            System.out.println("The game was not over yet...");
        }
    }
}