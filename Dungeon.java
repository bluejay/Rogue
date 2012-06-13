/*************************************************************************
 *  Compilation:  javac Dungeon.java
 *
 *  April 2011
 *  Modified by J. Smith
 *      - improved variable names, structure
 *************************************************************************/


public class Dungeon {
    private boolean[][] isRoom;        // is v-w a room site?
    private boolean[][] isCorridor;    // is v-w a corridor site?
    private int size;                     // dimension of dungeon

    // initialize a new dungeon based on the given board
    public Dungeon(char[][] board) {
        size = board.length;
        isRoom     = new boolean[size][size];
        isCorridor = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if      (board[i][j] == '.') isRoom[i][j] = true;
                else if (board[i][j] == '+') isCorridor[i][j] = true;
            }
        }
    }

    // return dimension of dungeon
    public int size() { return size; }

    // does v correspond to a corridor site? 
    public boolean isCorridor(Site v) {
        int i = v.row();
        int j = v.col();
        if (i < 0 || j < 0 || i >= size() || j >= size()) return false;
        return isCorridor[i][j];
    }

    // does v correspond to a room site?
    public boolean isRoom(Site v) {
        int i = v.row();
        int j = v.col();
        if (i < 0 || j < 0 || i >= size() || j >= size()) return false;
        return isRoom[i][j];
    }

    // does v correspond to a wall site?
    public boolean isWall(Site v) {
        return (!isRoom(v) && !isCorridor(v));
    }

    // does v-w correspond to a legal move?
    // Jsmith:  Why isn't a corridor a legal move? ok -- caught by last two if's
    public boolean isLegalMove(Site v, Site w) {
        int i1 = v.row();
        int j1 = v.col();
        int i2 = w.row();
        int j2 = w.col();
        if (i1 < 0 || j1 < 0 || i1 >= size() || j1 >= size()) return false;
        if (i2 < 0 || j2 < 0 || i2 >= size() || j2 >= size()) return false;
        if (isWall(v) || isWall(w)) return false;
        if (Math.abs(i1 - i2) > 1)  return false;
        if (Math.abs(j1 - j2) > 1)  return false;
        if (isRoom(v) && isRoom(w)) return true;
        if (i1 == i2)               return true;
        if (j1 == j2)               return true;

        return false;
    }

}
