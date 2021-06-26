package spw4.game2048;

import java.util.Arrays;
import java.util.Random;

public class Game {

    /**
     * Size of tile board e.g. 4x4
     */
    public static final int SIZE = 4;
    /**
     * Score which needs to be reached to win the game.
     */
    public static final int WIN_SCORE = 2048;

    private int[][] tiles;
    public Random random = new Random();
    private int moves = 0;
    private int score = 0;

    public Game() {
        initialize();
    }

    public boolean compareBoards(Game toCompare) {
        return Arrays.deepEquals(this.tiles, toCompare.getTiles());
    }

    public int getScore() {
        return score;
    }

    public int getMoves() {
        return moves;
    }

    public int getValueAt(int x, int y) {
        return tiles[x][y];
    }

    public boolean isOver(boolean initNewTile) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (tiles[i][j] == 0) {
                    if (initNewTile) {
                        createRandomValueOnRandomEmptyTile();
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isWon() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (tiles[i][j] >= WIN_SCORE) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Moves: " + getMoves() + " Score: " + getScore() + "\n");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                s.append(tiles[i][j] == 0 ? "." : tiles[i][j]);
                if (j < SIZE - 1) {
                    s.append(" ");
                }
            }
            if (i < SIZE - 1) {
                s.append("\n");
            }
        }
        return s.toString();
    }

    public void initialize() {
        tiles = new int[4][4];

        createRandomValueOnRandomEmptyTile();
        createRandomValueOnRandomEmptyTile();

        score = 0;
        moves = 0;
    }

    public void move(Direction direction) {
        switch (direction) {
            case right:
                for (int x = 0; x < SIZE; x++) {
                    for (int y = SIZE-1; y >= 0; y--) {
                        if (tiles[x][y] != 0) {
                            int currentY = y;
                            if (currentY < SIZE-1) {
                                while (currentY+1 < SIZE) {
                                    if (tiles[x][currentY+1] == 0 || tiles[x][currentY+1] == tiles[x][y]) {
                                        currentY++;
                                    } else {
                                        break;
                                    }
                                }
                                if (currentY != y) {
                                    if (tiles[x][currentY] == tiles[x][y]) {
                                        tiles[x][currentY] = tiles[x][y] * 2;
                                        this.score += tiles[x][y] * 2;
                                    } else {
                                        tiles[x][currentY] = tiles[x][y];
                                    }
                                    tiles[x][y] = 0;
                                }
                            }
                        }
                    }
                }
                break;
            case left:
                for (int x = 0; x < SIZE; x++) {
                    for (int y = 0; y < SIZE; y++) {
                        if (tiles[x][y] != 0) {
                            int currentY = y;
                            if (currentY > 0) {
                                while (currentY-1 >= 0) {
                                    if (tiles[x][currentY-1] == 0 || tiles[x][currentY-1] == tiles[x][y]) {
                                        currentY--;
                                    } else {
                                        break;
                                    }
                                }
                                if (currentY != y) {
                                    if (tiles[x][currentY] == tiles[x][y]) {
                                        tiles[x][currentY] = tiles[x][y] * 2;
                                        this.score += tiles[x][y] * 2;
                                    } else {
                                        tiles[x][currentY] = tiles[x][y];
                                    }
                                    tiles[x][y] = 0;
                                }
                            }
                        }
                    }
                }
                break;
            case up:
                for (int y = 0; y < SIZE; y++) {
                    for (int x = 0; x < SIZE; x++) {
                        if (tiles[x][y] != 0) {
                            int currentX = x;
                            if (currentX > 0) {
                                while (currentX - 1 >= 0) {
                                    if (tiles[currentX-1][y] == 0 || tiles[currentX-1][y] == tiles[x][y]) {
                                        currentX--;
                                    } else {
                                        break;
                                    }
                                }
                                if (currentX != x) {
                                    if (tiles[currentX][y] == tiles[x][y]) {
                                        tiles[currentX][y] = tiles[x][y] * 2;
                                        this.score += tiles[x][y] * 2;
                                    } else {
                                        tiles[currentX][y] = tiles[x][y];
                                    }
                                    tiles[x][y] = 0;
                                }
                            }
                        }
                    }
                }
                break;
            case down:
                for (int y = 0; y < SIZE; y++) {
                    for (int x = SIZE-1; x >= 0; x--) {
                        if (tiles[x][y] != 0) {
                            int currentX = x;
                            if (currentX < SIZE-1) {
                                while (currentX+1 < SIZE) {
                                    if (tiles[currentX+1][y] == 0 || tiles[currentX+1][y] == tiles[x][y]) {
                                        currentX++;
                                    } else {
                                        break;
                                    }
                                }
                                if (currentX != x) {
                                    if (tiles[currentX][y] == tiles[x][y]) {
                                        tiles[currentX][y] = tiles[x][y] * 2;
                                        this.score += tiles[x][y] * 2;
                                    } else {
                                        tiles[currentX][y] = tiles[x][y];
                                    }
                                    tiles[x][y] = 0;
                                }
                            }
                        }
                    }
                }
                break;
        }
        moves++;
    }

    public int[][] getTiles() {
        return tiles;
    }



    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }

    private int getRandomTileValue() {
        int[] weightedTiles = { 4, 2, 2, 2, 2, 2, 2, 2, 2, 2 }; // 90% for 2, 10% for 4
        return weightedTiles[random.nextInt(10)];
    }

    private void createRandomValueOnRandomEmptyTile() {
        int x, y;
        do {
            x = random.nextInt(4);
            y = random.nextInt(4);
        } while (tiles[x][y] != 0);
        tiles[x][y] = getRandomTileValue();
    }
}
