/** BOARDGEN CLASS
 * @author Afonso Brás Sousa
 * @author Alexandre Cristóvão
 * Reads a file from a specified path and generates an array with the specified board
 * The resulting array can be accessed using method getBoard()
 * Ensures safe file management - only this class is allowed to read external files
 */

import java.util.Scanner;
import java.io.*;

public class BoardGen {
    //Board specification constants
    //The output constants are public so that the Gameplay class can correctly parse the array
    private static final int BIRD_MULT = 9; //defines a bird tile every N tiles
    public static final int INT_BIRD = 1; //bird tile
    private static final String FALL_CRAB = "crab";
    public static final int INT_FALL_CRAB = 2; //fall-crab tile
    private static final String FALL_HELL = "hell";
    public static final int INT_FALL_HELL = 3; //fall-hell tile
    private static final String FALL_DEATH = "death";
    public static final int INT_FALL_DEATH = 4; //fall-death tile
    //non-special tiles are NULL (zero) by default
    //penalty tiles are NEGATIVE NUMBERS that match the corresponding penalty (ex: -3 == penalty 3)

    private final int[] board;

    /** CONSTRUCTOR
     * Reads pre-formatted file; assumes correct format
     * @param path - the path specified by the Main class
     *             - pre: file exists and is in the correct path
     * @param boardNumber - which board should be read from
     *                    - pre: >=1
     * @throws FileNotFoundException - propagates the error to the Main class
     */
    public BoardGen(String path, int boardNumber) throws FileNotFoundException {
        Scanner file = new Scanner (new FileReader(path));

        //Skips lines until the correct board is read from
        skipUntil(file, boardNumber);

        //Receives the number of tiles
        int tileNumber = file.nextInt(); file.nextLine(); //Pre: >=10 && <=150

        //Creates the board (array index 0 = tile 1)
        board = new int[tileNumber];

        //Populates the board
        populateBird(board); //Pre: every BIRD_MULT tiles - doesn't read from file
        populatePenalty(file,board); //Pre: qty >=1 && <=(tileNumber/3); tile >=2 && <= tileNumber-1
        populateFall(file,board); //Pre: qty >=1 && <=(tileNumber/3); tile >=2 && <= tileNumber-1

        //Closes the file
        file.close();
    }

    /**
     * Skips lines until the correct board is read from.
     * The number of penalty and fall tiles of the incorrect boards are read to know how many lines
     * should be skipped.
     * @param in - the file Scanner
     * @param boardNumber - the number of the correct board
     *                    - Pre: >0
     */
    private void skipUntil(Scanner in, int boardNumber) {
        for (int i=1; i<boardNumber; i++) { //doesn't execute when boardNumber == 1
            in.nextLine(); //skips the line with the tileNumber of incorrect board
            skipLines(in,in.nextInt()); //skips N+1 lines where N == number of penalty tiles
            skipLines(in,in.nextInt()); //skips N+1 lines where N == number of fall tiles
        }
    }

    /**
     * Skips N+1 lines.
     * (Also skips the line from which the integer N was read from.)
     * @param in - file Scanner
     * @param lines - how many lines to skip
     *              - Pre: lines > 0
     */
    private void skipLines(Scanner in, int lines) {
        for (int i=0; i<=lines; i++) {in.nextLine();}
    }

    /**
     * Returns the board array with the specified format
     * @return board - the board array
     */
    public int[] getBoard() {
        return board;
    }

    /**
     * Defines bird tiles every BIRD_TILE_MULT tiles
     * @param board - the board array
     */
    private static void populateBird(int[] board) {
        for (int i = 0; i < (board.length-1)/ BIRD_MULT; i++) {  //goes to C-1
            board[(i+1)* BIRD_MULT -1] = INT_BIRD;
        }
    }

    /**
     * Populates the board with penalty tiles
     * The number of turns a player is penalized is saved in the tile as a NEGATIVE integer
     * Non-negative numbers are reserved for all the other tiles
     * @param in - file Scanner
     * @param board - the board array
     */
    private static void populatePenalty(Scanner in, int[] board) {
        int size = in.nextInt(); in.nextLine();
        for (int i=0; i<size; i++) {
            int position = in.nextInt()-1;  //tile N == array position N-1
            board[position] = in.nextInt() * -1; //penalty numbers are NEGATIVE
            in.nextLine();
        }
    }

    /**
     * Populates the board with fall tiles according to each tile's subtype
     * @param in - file Scanner
     * @param board - the board array
     */
    private static void populateFall(Scanner in, int[] board) {
        int size = in.nextInt(); in.nextLine();
        for (int i=0; i<size; i++) {
            int position = in.nextInt()-1; //tile N == array position N-1
            String type = in.nextLine().trim();
            switch (type) {
                case FALL_CRAB: board[position]=INT_FALL_CRAB; break; //crab tiles
                case FALL_HELL: board[position]=INT_FALL_HELL; break; //hell tiles
                case FALL_DEATH: board[position]=INT_FALL_DEATH; break; //death tiles
            }
        }
    }
}