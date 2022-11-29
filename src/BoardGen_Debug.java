import java.io.*;
import java.util.Scanner;
public class BoardGen_Debug {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
        String path = in.nextLine().trim();
        BoardGen board = new BoardGen(path);
        board.getBoard();
    }
}
