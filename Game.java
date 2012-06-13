import java.util.Scanner;
import java.io.File;
import java.util.*;
/*************************************************************************
 *  Compilation:  javac Game.java
 *  
 *  Execution:    
 *      Enter "dungeonX" for args[0] in main where X is [A..Z]
 *              indicating which dungeon text file to use
 *      Press ENTER between moves, the dungeon is redisplayed after each Monster/Rogue move
 *      
 *  Dependencies: Dungeon.java Site.java Monster.java Rogue.java
 *  
 *  
 *
 *  April 2011
 *   J. Smith
 *   -- fixed variable names, dependencies (removed N, this is stored in dungeon)
 *************************************************************************/

public class Game {

    // portable newline
    private final static String NEWLINE = System.getProperty("line.separator");

    private Dungeon dungeon;     // the dungeon
    private char monsterDisp;        // name of the monster (A - Z)
    private static final char ROGUE = '@';    // name of the rogue
    private Site monsterSite;    // location of monster
    private Site rogueSite;      // location of rogue
    private Monster monster;     // the monster
    private JPRogue rogue;         // the rogue

    // initialize board from file
    public Game(Scanner in) {

        // read in data
        int size = Integer.parseInt(in.nextLine());
        char[][] board = new char[size][size];
        for (int i = 0; i < size; i++) {
            String s = in.nextLine();
            for (int j = 0; j < size; j++) {
                board[i][j] = s.charAt(2*j);

                // check for monster's location
                if (board[i][j] >= 'A' && board[i][j] <= 'Z') {
                    monsterDisp = board[i][j];
                    board[i][j] = '.';
                    monsterSite = new Site(i, j);
                }

                // check for rogue's location
                if (board[i][j] == ROGUE) {
                    board[i][j] = '.';
                    rogueSite  = new Site(i, j);
                }
            }
        }
        dungeon = new Dungeon(board);
        monster = new Monster(this);
        rogue   = new JPRogue(this);
    }

    // return position of monster and rogue
    public Site getMonsterSite() { return monsterSite; }

    public Site getRogueSite()   { return rogueSite;   }

    public Dungeon getDungeon()  { return dungeon;     }

    // play until monster catches the rogue
    public void play() {
        Scanner user = new Scanner(System.in);
        for (int t = 1; true; t++) {
            System.out.println("Move " + t);
            System.out.println();

            // monster moves
            if (monsterSite.equals(rogueSite)) break;
            Site next = monster.move();
            if (dungeon.isLegalMove(monsterSite, next)) monsterSite = next;
            else throw new RuntimeException("Monster caught cheating");
            System.out.println(this);

            // rogue moves
            if (monsterSite.equals(rogueSite)) break;
            next = rogue.move();
            if (dungeon.isLegalMove(rogueSite, next)) rogueSite = next;
            else throw new RuntimeException("Rogue caught cheating");
            System.out.println(this);
            user.nextLine();
        }

        System.out.println("Caught by monster");
    }

    /*
     * Added by J. Smith
     * provide a method for moving the Monster and Rogue
     * Allows GUI game to run simulation without having to access monster/rogue
     */
    public void moveMonster ()
    {
        Site next = monster.move();
        if (dungeon.isLegalMove(monsterSite, next)) monsterSite = next;
        else throw new RuntimeException("Monster caught cheating");
    }

    public void moveRogue ()
    {
        Site next = rogue.move();
        if (dungeon.isLegalMove(rogueSite, next)) rogueSite = next;
        else throw new RuntimeException("Rogue caught cheating");
    }
    // string representation of game state (inefficient because of Site and string concat)
    public String toString() {
        String s = "";
        for (int i = 0; i < dungeon.size(); i++) {
            for (int j = 0; j < dungeon.size(); j++) {
                Site site = new Site(i, j);
                if (rogueSite.equals(monsterSite) && (rogueSite.equals(site))) s += "* ";
                else if (rogueSite.equals(site))                               s += ROGUE   + " ";
                else if (monsterSite.equals(site))                             s += monsterDisp + " ";
                else if (dungeon.isRoom(site))                                 s += ". ";
                else if (dungeon.isCorridor(site))                             s += "+ ";
                else if (dungeon.isWall(site))                                 s += "  ";
            }
            s += NEWLINE;
        }
        return s;
    }

    /**
     * Run by entering a file name for arg[0] in the form dungeonX where X is A..Z
     */
    public static void main(String[] args) throws Exception {
        Scanner stdin = new Scanner(new File("dungeons/" + args[0] + ".txt"));
        Game game = new Game(stdin);
        System.out.println(game);
        game.play();
    }

}
