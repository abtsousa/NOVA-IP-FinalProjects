import java.io.*;
import java.util.Scanner;

//DEBUG
//PARA APAGAR NA VERSÃO FINAL

public class Ranking_Debug {
    public static void main(String[] args) throws FileNotFoundException {
        //Processes the player order
        String playerOrder = "abc"; //Pre: 3-10 different capital letters

        //Creates the board
        int boardNumber = 1;
        int[] board = new BoardGen("boards.txt", boardNumber).getBoard();

        //Starts a game
        Gameplay game = new Gameplay(board, playerOrder);
    }
}
