package harnam.saggu;

import java.util.Random;
import java.util.Scanner;

public class Minesweeper {
    int width;
    int height;
    double difficulty;
    char[][] underlyingBoard;
    char[][] visibleBoard;
    boolean isAlive;

    public Minesweeper(int width, int height, double difficulty) {
        this.isAlive = true;
        this.underlyingBoard = new char[width][height];
        this.visibleBoard = new char[width][height];
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;

        int bombsToPlace = (int) (width * height * difficulty);
        Random random = new Random();
        while (bombsToPlace > 0) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (underlyingBoard[x][y] == (char) 0) {
                underlyingBoard[x][y] = 'M';
                bombsToPlace--;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                visibleBoard[i][j] = '#';
                if (underlyingBoard[i][j] == 'M') continue;

                int neighbouringBombs = 0;
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        if (k == 0 && l == 0) continue;
                        if (i + k < 0 || j + l < 0 || i + k >= width || j + l >= height) continue;
                        if (underlyingBoard[i + k][j + l] == 'M') {
                            neighbouringBombs++;
                        }
                    }
                }
                if (neighbouringBombs > 0) {
                    underlyingBoard[i][j] = (char) (48 + neighbouringBombs);
                } else {
                    underlyingBoard[i][j] = ' ';
                }
            }
        }

        startGame();
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        String lastMove = "";
        while (isAlive) {
            System.out.println("<==Minesweeper==>" + ((6 + height * 3 - (17 + ("w:" + width + ", h:" + height + ", diff:" + difficulty).length()) > 0)? ".".repeat(6 + height * 3 - (17 + ("w:" + width + ", h:" + height + ", diff:" + difficulty).length())) : "") + "w:" + width + ", h:" + height + ", diff:" + difficulty);
            printBoard();
            System.out.println("To reveal" + ((height * 3 - 8 > 0)? ".".repeat(height * 3 - 8) : ".") + "R x,y\n" + "To flag/un-flag" + ((height * 3 - 14 > 0 )? ".".repeat(height * 3 - 14) : ".") + "F x,y");
            System.out.println("Last move: " + lastMove);
            try {
                String input = scanner.nextLine();
                char starter = input.charAt(0);
                lastMove = input;
                if (input.matches("[RrFf]\\s*\\d+\\s*,\\s*\\d+")) {
                    input = input.replaceAll(".(?<![,\\d])", "");
                    String[] coords = input.split(",");
                    if (starter == 'F' || starter == 'f')
                        flagTile(Integer.parseInt(coords[1]) - 1, Integer.parseInt(coords[0]) - 1);
                    else revealTile(Integer.parseInt(coords[1]) - 1, Integer.parseInt(coords[0]) - 1);
                }
            } catch (Exception e) { }
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) { }
            System.out.println("clear");
            if (isComplete()) break;
        }
        printBoard();
        if (isComplete()) {
            System.out.println("Congrats!!!");
        } else {
            System.out.println("Better luck next time :(");
            printRawBoard();
        }
    }

    public boolean isComplete() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                char underlyingChar = underlyingBoard[i][j];
                char visibleChar = visibleBoard[i][j];

                if (underlyingChar == 'M') {
                    if (visibleChar != 'F') return false;
                } else {
                    if (underlyingChar != visibleChar) return false;
                }
            }
        }
        return isAlive;
    }

    public void flagTile(int x, int y) {
        if (visibleBoard[x][y] == 'F') {
            visibleBoard[x][y] = '.';
        } else {
            visibleBoard[x][y] = 'F';
        }
    }

    public void revealTile(int x, int y) {
        visibleBoard[x][y] = underlyingBoard[x][y];
        if (visibleBoard[x][y] == 'M') {
            isAlive = false;
        } else if (visibleBoard[x][y] == ' ') {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if ((i == 0 && j == 0) || x + i < 0 || y + j < 0 || x + i >= width || y + j >= height) continue;

                    if (visibleBoard[x + i][y + j] == '#') {
                        revealTile(x + i, y + j);
                    }
                }
            }
        }
    }

    public void printRawBoard() {
        System.out.println("#".repeat(6 + width * 3));
        for (int i = 0; i < width; i++) {
            System.out.print("###");
            for (int j = 0; j < height; j++) {
                System.out.print(" " + underlyingBoard[i][j] + " ");
            }
            System.out.println("###");
        }
        System.out.println("#".repeat(6 + width * 3));
    }

    public void printBoard() {
        String firstLine = "###";
        for (int i = 0; i < height; i++) {
            firstLine += String.format("%02d|", i + 1);
        }
        System.out.println(firstLine.substring(0, firstLine.length() - 1) + "####");
        for (int i = 0; i < width; i++) {
            System.out.printf("%02d#", i + 1);
            for (int j = 0; j < height; j++) {
                if (visibleBoard[i][j] == '#') {
                    System.out.print(" . ");
                } else {
                    System.out.print(" " + visibleBoard[i][j] + " ");
                }
            }
            System.out.println("###");
        }
        System.out.println("#".repeat(6 + width * 3));
    }
}
