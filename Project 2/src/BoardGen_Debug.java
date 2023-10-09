import java.io.*;
import java.util.Scanner;

//DEBUG
//PARA APAGAR NA VERSÃO FINAL

public class BoardGen_Debug {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
        String path = in.nextLine().trim();
        int lines = in.nextInt();
        BoardGen board = new BoardGen(path,lines);
        board.getBoard();
    }
}
