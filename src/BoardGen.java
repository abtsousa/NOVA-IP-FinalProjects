import java.util.Scanner;
import java.io.*;

//Generates board from the text file
public class BoardGen {
    private static final int BIRD_TILE_MULT = 9; //defines a bird tile every N tiles
    private static final int INT_BIRD = 1; //bird tile
    private static final String FALL_CRAB = "crab";
    private static final int INT_FALL_CRAB = 2; //fall-crab tile
    private static final String FALL_DEATH = "death";
    private static final int INT_FALL_DEATH = 3; //fall-death character
    private static final String FALL_HELL = "hell";
    private static final int INT_FALL_HELL = 4; //fall-hell character
    //normal tiles are ZERO
    //penalty tiles are NEGATIVE NUMBERS (ex: -3 == penalty 3)

    private int[] board;

    public BoardGen(String path) throws FileNotFoundException {
        Scanner in = new Scanner (new FileReader(path));
        //Receives the number of tiles
        int tileNumber = in.nextInt();
        in.nextLine(); //Pre: >=10 && <=150

        //Creates the board (position 0 = tile 1)
        board = new int[tileNumber];

        //Populates the board
        populateBird(board);
        populatePenalty(in, board); //Pre: >=1 && <=(tileNumber/3)
        populateFall(in, board); //Pre: >=1 && <=(tileNumber/3)

        in.close();
    }

    public int[] getBoard() {
        return board;
    }

    private static void populateBird(int[] board) {
        for (int i=0; i < (board.length-1)/ BIRD_TILE_MULT; i++) {  //goes to C-1
            board[(i+1)*BIRD_TILE_MULT-1] = INT_BIRD;
        }
    }

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
