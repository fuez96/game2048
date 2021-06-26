package spw4.game2048;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTest {
    /**
     * System under test.
     */
    private Game sut;

    @Mock
    private Random randomMock;

    @BeforeEach
    void setUp() {
        sut = new Game();
    }

    @DisplayName("Game.ctor returns new game instance")
    @Test
    void ctorReturnsNewGameInstance() {
        assertAll(
                () -> assertNotNull(sut),
                () -> assertTrue(sut instanceof Game),
                () -> assertFalse(sut.isOver(false)),
                () -> assertFalse(sut.isWon()),
                () -> assertEquals(0, sut.getScore()),
                () -> assertEquals(0, sut.getMoves())
        );
    }

    @DisplayName("Game.initialize() restarts a completely new game in same instance")
    @Test
    void initializeRestartsANewGameInSameInstance() {
        // arrange
        sut.move(Direction.up);
        sut.move(Direction.down);
        sut.move(Direction.left);
        sut.move(Direction.right);
        when(randomMock.nextInt(4))
                .thenReturn(0).thenReturn(0)    // x,y for first tile
                .thenReturn(2).thenReturn(0);   // x,y for second tile
        when(randomMock.nextInt(10))
                .thenReturn(5)                  // first tile value -> 2
                .thenReturn(0);                 // second tile value -> 4
        sut.random = randomMock;
        // act
        sut.initialize();
        // assert
        assertAll(
                () -> assertNotNull(sut),
                () -> assertTrue(sut instanceof Game),
                () -> assertFalse(sut.isOver(false)),
                () -> assertFalse(sut.isWon()),
                () -> assertEquals(0, sut.getScore()),
                () -> assertEquals(0, sut.getMoves()),
                () -> assertEquals(2, sut.getTiles()[0][0]),
                () -> assertEquals(4, sut.getTiles()[2][0])
        );
    }

    @DisplayName("Game.initialize() restarts a completely new game in same instance with same random values")
    @Test
    void initializeRestartsANewGameInSameInstanceWithSameRandomValues() {
        // arrange
        when(randomMock.nextInt(4))
                .thenReturn(0).thenReturn(0)    // x,y for first tile
                .thenReturn(1).thenReturn(0);   // x,y for second tile
        when(randomMock.nextInt(10))
                .thenReturn(5)                  // first tile value -> 2
                .thenReturn(0);                 // second tile value -> 4
        sut.random = randomMock;
        // act
        sut.initialize();
        // assert
        assertAll(
                () -> assertNotNull(sut),
                () -> assertTrue(sut instanceof Game),
                () -> assertFalse(sut.isOver(false)),
                () -> assertFalse(sut.isWon()),
                () -> assertEquals(0, sut.getScore()),
                () -> assertEquals(0, sut.getMoves()),
                () -> assertEquals(2, sut.getTiles()[0][0]),
                () -> assertEquals(4, sut.getTiles()[1][0])
        );
    }

    @DisplayName("Game.toString() returns valid game tile representation of current status")
    @Test
    void toStringReturnsValidGameTileRepresentationOfCurrentStatus() {
        sut.setTiles(new int[][]{
                {0, 0, 0, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 2},
        });
        assertEquals("Moves: 0 Score: 0\n" +
                ". . . 2\n" +
                ". . . .\n" +
                ". . . .\n" +
                ". . . 2", sut.toString());
    }

    @DisplayName("Game.isOver(initNewTile = false) returns true if no free tile is available any more")
    @Test
    void isOverReturnsTrueIfNoFreeTileIsAvailable() {
        sut.setTiles(new int[][]{
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {4, 8, 64, 4},
                {2, 4, 32, 2},
        });
        assertTrue(sut.isOver(false));
    }

    @DisplayName("Game.isOver(initNewTile = true) returns true and does not create new tile when no free position is available")
    @Test
    void isOverReturnsTrueAndNotCreatesNewTileWhenNoFreePositionAvailable() {
        sut.setTiles(new int[][]{
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {2, 8, 64, 4},
                {2, 4, 32, 2},
        });
        assertTrue(sut.isOver(true));
    }

    @DisplayName("Game.isOver(initNewTile = true) returns false and creates new tile on free position")
    @Test
    void isOverReturnsFalseAndCreatesNewTileOnFreePosition() {
        // arrange
        when(randomMock.nextInt(4))
                .thenReturn(2).thenReturn(0);    // x,y for tile
        when(randomMock.nextInt(10))
                .thenReturn(0);                 // tile value -> 4
        sut.random = randomMock;
        sut.setTiles(new int[][]{
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {0, 8, 64, 4},
                {2, 4, 32, 2},
        });
        // assert
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {4, 8, 64, 4},
                {2, 4, 32, 2},
        }));
    }

    @DisplayName("Game.isOver() returns false if free tiles are available")
    @Test
    void isOverReturnsFalseIfFreeTilesAreAvailable() {
        sut.setTiles(new int[][]{
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {4, 8, 64, 4},
                {0, 4, 32, 2},
        });
        assertFalse(sut.isOver(false));
    }

    @DisplayName("Game.isWon() returns true if one tile is >= 2048")
    @Test
    void isWonReturnsTrueIfOneTileIsLargerThanOrEquals2048() {
        sut.setTiles(new int[][]{
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {4, 8, 64, 4},
                {2, 4, 2048, 2},
        });
        assertTrue(sut.isWon());
    }

    @DisplayName("Game.isWon() returns false if not one tile is >= 2048")
    @Test
    void isWonReturnsFalseIfNotOneTileIsLargerThanOrEquals2048() {
        sut.setTiles(new int[][]{
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {4, 8, 64, 4},
                {2, 4, 32, 2},
        });
        assertFalse(sut.isWon());
    }

    @DisplayName("Game.compareBoards() compares to game boards if all of their tiles are equal")
    @Test
    void areBoardsEqualsReturnTrue() {
        Game toCompare = new Game();

        sut.setTiles(new int[][]{
            {4, 8, 2, 32},
            {32, 128, 4, 2},
            {4, 8, 64, 4},
            {0, 4, 32, 2},
        });
        toCompare.setTiles(new int[][]{
                {4, 8, 2, 32},
                {32, 128, 4, 2},
                {4, 8, 64, 4},
                {0, 4, 32, 2},
        });
        assertTrue(sut.compareBoards(toCompare));

        toCompare.setTiles(new int[][]{
                {0, 8, 2, 32},
                {32, 0, 4, 2},
                {4, 8, 0, 4},
                {0, 4, 32, 0},
        });
        assertFalse(sut.compareBoards(toCompare));
    }

    @DisplayName("Game.move() moves all tiles to the end of the board in desired direction")
    @ParameterizedTest(name = "from tiles = {0}, to tiles = {1}, direction to move = {2}")
    @MethodSource
    void isMovedToTheEndOfBoard(int[][] fromTiles, int[][] toTiles, Direction direction) {
        // arrange
        sut.setTiles(fromTiles);
        Game sutCompare = new Game();
        sutCompare.setTiles(toTiles);
        // act
        sut.move(direction);
        // assert
        assertTrue(sut.compareBoards(sutCompare));
    }

    static Stream<Arguments> isMovedToTheEndOfBoard() {
        return Stream.of(
                // right
                Arguments.of(new int[][]{
                        {0, 0, 0, 2},
                        {0, 4, 2, 0},
                        {2, 0, 0, 0},
                        {0, 0, 2, 0}
                }, new int[][]{
                        {0, 0, 0, 2},
                        {0, 0, 4, 2},
                        {0, 0, 0, 2},
                        {0, 0, 0, 2}
                }, Direction.right),
                // left
                Arguments.of(new int[][]{
                        {0, 0, 0, 2},
                        {0, 4, 2, 0},
                        {2, 0, 0, 0},
                        {0, 0, 2, 0}
                }, new int[][]{
                        {2, 0, 0, 0},
                        {4, 2, 0, 0},
                        {2, 0, 0, 0},
                        {2, 0, 0, 0}
                }, Direction.left),
                // up
                Arguments.of(new int[][]{
                        {0, 0, 0, 2},
                        {0, 2, 0, 0},
                        {2, 0, 0, 0},
                        {4, 0, 2, 0}
                }, new int[][]{
                        {2, 2, 2, 2},
                        {4, 0, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                }, Direction.up),
                // down
                Arguments.of(new int[][]{
                        {0, 4, 0, 2},
                        {0, 2, 0, 0},
                        {2, 0, 0, 0},
                        {0, 0, 2, 0}
                }, new int[][]{
                        {0, 0, 0, 0},
                        {0, 0, 0, 0},
                        {0, 4, 0, 0},
                        {2, 2, 2, 2}
                }, Direction.down)
        );
    }

    @DisplayName("Game.move() moves all tiles to the end of the board in desired direction and merges 2 equal tiles from the opposite direction")
    @ParameterizedTest(name = "from tiles = {0}, to tiles = {1}, direction to move = {2}")
    @MethodSource
    void isGettingMerged(int[][] fromTiles, int[][] toTiles, Direction direction) {
        // arrange
        sut.setTiles(fromTiles);
        Game sutCompare = new Game();
        sutCompare.setTiles(toTiles);
        // act
        sut.move(direction);
        // assert
        assertTrue(sut.compareBoards(sutCompare));
    }

    static Stream<Arguments> isGettingMerged() {
        return Stream.of(
                // right 1
                Arguments.of(new int[][] {
                        {0, 0, 0, 2},
                        {2, 2, 0, 0},
                        {2, 2, 0, 2},
                        {2, 2, 2, 2}
                }, new int[][] {
                        {0, 0, 0, 2},
                        {0, 0, 0, 4},
                        {0, 0, 2, 4},
                        {0, 0, 4, 4}
                }, Direction.right),
                // right 2
                Arguments.of(new int[][] {
                        {2, 4, 2, 4},
                        {2, 2, 4, 0},
                        {2, 2, 4, 4},
                        {0, 0, 0, 0}
                }, new int[][] {
                        {2, 4, 2, 4},
                        {0, 0, 4, 4},
                        {0, 0, 4, 8},
                        {0, 0, 0, 0}
                }, Direction.right),
                // left 1
                Arguments.of(new int[][] {
                        {2, 0, 0, 0},
                        {2, 2, 0, 0},
                        {2, 2, 0, 2},
                        {2, 2, 2, 2}
                }, new int[][] {
                        {2, 0, 0, 0},
                        {4, 0, 0, 0},
                        {4, 2, 0, 0},
                        {4, 4, 0, 0}
                }, Direction.left),
                // left 2
                Arguments.of(new int[][] {
                        {2, 4, 2, 4},
                        {2, 2, 4, 0},
                        {2, 2, 4, 4},
                        {0, 0, 0, 0}
                }, new int[][] {
                        {2, 4, 2, 4},
                        {8, 0, 0, 0},
                        {8, 4, 0, 0},
                        {0, 0, 0, 0}
                }, Direction.left),
                // up 1
                Arguments.of(new int[][] {
                        {2, 0, 0, 0},
                        {2, 2, 0, 0},
                        {2, 2, 0, 2},
                        {2, 2, 2, 2}
                }, new int[][] {
                        {4, 4, 2, 4},
                        {4, 2, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                }, Direction.up),
                // up 2
                Arguments.of(new int[][] {
                        {2, 4, 2, 4},
                        {2, 2, 4, 0},
                        {2, 2, 4, 4},
                        {0, 0, 0, 0}
                }, new int[][] {
                        {4, 4, 2, 8},
                        {2, 4, 8, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                }, Direction.up),
                // down 1
                Arguments.of(new int[][] {
                        {2, 0, 0, 0},
                        {2, 2, 0, 0},
                        {2, 2, 0, 2},
                        {2, 2, 2, 2}
                }, new int[][] {
                        {0, 0, 0, 0},
                        {0, 0, 0, 0},
                        {4, 2, 0, 0},
                        {4, 4, 2, 4}
                }, Direction.down),
                // down 2
                Arguments.of(new int[][] {
                        {2, 4, 2, 4},
                        {2, 2, 4, 0},
                        {2, 2, 4, 4},
                        {0, 0, 0, 0}
                }, new int[][] {
                        {0, 0, 0, 0},
                        {0, 0, 0, 0},
                        {2, 0, 2, 0},
                        {4, 8, 8, 8}
                }, Direction.down)
        );
    }

    @DisplayName("Game.move() updates the score if there was a match by the matched values")
    @ParameterizedTest(name = "tiles = {0}, direction to move = {1}, expected score = {2}")
    @MethodSource
    void isScoreGettingUpdatedRight(int[][] tiles, Direction direction, int score) {
        sut.setTiles(tiles);
        sut.move(direction);
        assertEquals(score, sut.getScore());
    }

    static Stream<Arguments> isScoreGettingUpdatedRight() {
        return Stream.of(
                Arguments.of(new int[][] {
                        {0, 0, 0, 2},
                        {2, 2, 0, 0},
                        {2, 2, 0, 2},
                        {2, 2, 2, 2}
                }, Direction.right, 16),
                Arguments.of(new int[][] {
                        {2, 4, 2, 4},
                        {2, 2, 4, 0},
                        {2, 2, 4, 4},
                        {0, 0, 0, 0}
                }, Direction.left, 24),
                Arguments.of(new int[][] {
                        {0, 0, 0, 2},
                        {2, 2, 0, 0},
                        {2, 2, 0, 2},
                        {2, 2, 2, 2}
                }, Direction.up, 12),
                Arguments.of(new int[][] {
                        {2, 4, 2, 4},
                        {2, 2, 4, 0},
                        {2, 2, 4, 4},
                        {0, 0, 0, 0}
                }, Direction.down, 32)
        );
    }

    @DisplayName("Game -> test if all 8 game steps from exercise description are equals")
    @Test
    public void testsIfAll8GameStepsFromExerciseDescriptionAreEquals() {
        // arrange random
        when(randomMock.nextInt(4))
                .thenReturn(3).thenReturn(0)    // x,y for tile
                .thenReturn(0).thenReturn(0)    // x,y for tile
                .thenReturn(1).thenReturn(2)    // x,y for tile
                .thenReturn(2).thenReturn(0)    // x,y for tile
                .thenReturn(1).thenReturn(0)    // x,y for tile
                .thenReturn(2).thenReturn(2)    // x,y for tile
                .thenReturn(3).thenReturn(0);   // x,y for tile
        when(randomMock.nextInt(10))
                .thenReturn(1)                  // tile value -> 2
                .thenReturn(1)                  // tile value -> 2
                .thenReturn(1)                  // tile value -> 2
                .thenReturn(1)                  // tile value -> 2
                .thenReturn(1)                  // tile value -> 2
                .thenReturn(0)                  // tile value -> 4
                .thenReturn(1);                 // tile value -> 2
        sut.random = randomMock;
        sut.setTiles(new int[][]{
                {0, 0, 0, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 2},
        });
        // act + assert -> step 1
        sut.move(Direction.down);
        assertEquals(1, sut.getMoves());
        assertEquals(4, sut.getScore());
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 0, 0, 4},
        }));
        // act + assert -> step 2
        sut.move(Direction.right);
        assertEquals(2, sut.getMoves());
        assertEquals(4, sut.getScore());
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 2, 4},
        }));
        // act + assert -> step 3
        sut.move(Direction.down);
        assertEquals(3, sut.getMoves());
        assertEquals(4, sut.getScore());
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {0, 0, 0, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0},
                {2, 0, 2, 4},
        }));
        // act + assert -> step 4
        sut.move(Direction.right);
        assertEquals(4, sut.getMoves());
        assertEquals(8, sut.getScore());
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 2},
                {2, 0, 0, 0},
                {0, 0, 4, 4},
        }));
        // act + assert -> step 5
        sut.move(Direction.right);
        assertEquals(5, sut.getMoves());
        assertEquals(16, sut.getScore());
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {0, 0, 0, 0},
                {2, 0, 0, 2},
                {0, 0, 0, 2},
                {0, 0, 0, 8},
        }));
        // act + assert -> step 6
        sut.move(Direction.down);
        assertEquals(6, sut.getMoves());
        assertEquals(20, sut.getScore());
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 4, 4},
                {2, 0, 0, 8},
        }));
        // act + assert -> step 7
        sut.move(Direction.right);
        assertEquals(7, sut.getMoves());
        assertEquals(28, sut.getScore());
        assertFalse(sut.isOver(true));
        assertTrue(Arrays.deepEquals(sut.getTiles(), new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 8},
                {2, 0, 2, 8},
        }));
    }
}