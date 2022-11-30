import java.util.Scanner;
import java.io.*;

//Generates board from the text file
//Constructor reads from file and generates an array
//Array can be called using method getBoard()
//TODO definir o path - "boards.txt" em vez de um path genérico (para já fica assim porque facilita a testagem)

public class BoardGen {
    public static final int BIRD_TILE_MULT = 9; //defines a bird tile every N tiles
    public static final int INT_BIRD = 1; //bird tile
    public static final String FALL_CRAB = "crab";
    public static final int INT_FALL_CRAB = 2; //fall-crab tile
    public static final String FALL_DEATH = "death";
    public static final int INT_FALL_DEATH = 3; //fall-death character
    public static final String FALL_HELL = "hell";
    public static final int INT_FALL_HELL = 4; //fall-hell character
    //normal tiles are ZERO
    //penalty tiles are NEGATIVE NUMBERS (ex: -3 == penalty 3)

    private int[] board;

    public BoardGen(String path, int boardNumber) throws FileNotFoundException {
        Scanner file = new Scanner (new FileReader(path));

        //Skips lines until the correct board is read
        skipUntil(file, boardNumber);

        //Receives the number of tiles
        int tileNumber = file.nextInt();
        file.nextLine(); //Pre: >=10 && <=150

        //Creates the board (position 0 = tile 1)
        board = new int[tileNumber];

        //Populates the board
        populateBird(board);
        populatePenalty(file, board); //Pre: >=1 && <=(tileNumber/3)
        populateFall(file, board); //Pre: >=1 && <=(tileNumber/3)

        file.close();
    }

    private void skipUntil(Scanner in, int boardNumber) {
        for (int i=1; i<boardNumber; i++) {
            in.nextLine();
            skipLines(in,in.nextInt());
            skipLines(in,in.nextInt());
        }
    }

    private void skipLines(Scanner in, int lines) {
        for (int i=0; i<=lines; i++) {in.nextLine();} //also skips the line from which the integer was read from
    }

    public int[] getBoard() {
        return board;
    }

    /**
     * Defines bird tiles every BIRD_TILE_MULT tiles
     */
    private static void populateBird(int[] board) {
        for (int i=0; i < (board.length-1)/ BIRD_TILE_MULT; i++) {  //goes to C-1
            board[(i+1)*BIRD_TILE_MULT-1] = INT_BIRD;
        }
    }

    /**
     * Populates the board with penalty tiles
     * The number of turns a player is penalized is saved in the tile as a NEGATIVE number
     * Positive numbers are reserved for the other tiles
     */
    private static void populatePenalty(Scanner in, int[] board) {
        int size = in.nextInt(); in.nextLine();
        for (int i=0; i<size; i++) {
            int position = in.nextInt()-1;
            board[position] = in.nextInt() * -1; //penalty numbers are NEGATIVE
            in.nextLine();
        }
    }

    private static void populateFall(Scanner in, int[] board) {
        int size = in.nextInt(); in.nextLine();
        for (int i=0; i<size; i++) {
            int position = in.nextInt()-1;
            String type = in.nextLine().trim();
            switch (type) {
                case FALL_CRAB: board[position]=INT_FALL_CRAB; break;
                case FALL_DEATH: board[position]=INT_FALL_DEATH; break;
                case FALL_HELL: board[position]=INT_FALL_HELL; break;
            }
        }
    }

}
