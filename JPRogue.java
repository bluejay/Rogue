import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import java.util.HashSet;
/*************************************************************************
 *  Rogue: Sun Tzu, the strategic general
 *         Implementation of rogue using minimax.
 *         
 *         
 *         Analysis included in README.
 *  Author: Jay Palekar
 *  
 *  Compilation:  javac Rogue.java
 * 
 *  April 2011 by J. Smith
 *    --  updated style/variable names
 *************************************************************************/

public class JPRogue implements Creature{
    private Game game;
    private Dungeon dungeon;
    private Graph<Site> map;

    /* Within corridors and rooms, the number of neighbors per site drops,
    *  so we can look more levels deep without a performance hit, improving
    *  decision making.
    */
    private static final int ROOM_DEPTH = 6;
    private static final int CORRIDOR_DEPTH = 8;
    
    private ArrayList<Site> corridorStarts;
    //private ArrayList<ArrayList<Site>> corridorLoops = new ArrayList<ArrayList<Site>>();

    private HashSet<Site> safeCorridorStarts = new HashSet<Site>();
    private HashSet<Site> viableCorridors    = new HashSet<Site>();
    private HashSet<Site> inLoop             = new HashSet<Site>();

    /*
     *  Constructor for the Rogue
     *  Tasks handled: 
     *   - Initialize the graph which will be used in handling connections
     *   - Launch a series of tests to gain heurisitic information on the graph 
     *     can be quickly accessed in later stages of the rogue including, 
     *     corridors that can aren't dead ends, and corridors with inbuilt loops.
     *   -
     */
    public JPRogue(Game game) {
        this.game    = game;
        this.dungeon = game.getDungeon();
        this.map   = new JPMap<Site>(dungeon.size() * dungeon.size());

        // Initialize Graph
        Site[] added = new Site[dungeon.size() * dungeon.size()];
        for(int i = 0; i < dungeon.size(); i += 1)
            for(int j = 0; j < dungeon.size(); j += 1) {
                Site s = new Site(i, j);
                map.addVertex(s);
                added[i * dungeon.size() + j] = s;
        }

        for(int i = 0; i < dungeon.size() * dungeon.size(); i += 1)
            for(int j = 0; j < dungeon.size() * dungeon.size(); j += 1) {
                if(dungeon.isLegalMove(added[i], added[j]))
                    map.addEdge(added[i], added[j]);
        }

        // Gather information about the graph
        findCorridors(added);
        System.out.println(viableCorridors);
        
    }

    public Site move() {
        Site monster = game.getMonsterSite();
        Site rogue   = game.getRogueSite();
        Site move    = null;

        // Generate the set of all possible single turn moves for Rogue
        ArrayList<Site> moves = new ArrayList<Site>(map.neighbors(rogue));
        moves.add(rogue); // Current position

        // Select the move which presents the maximum benefit for the rogue
        int    maxIndex = 0;
        double maxValue = Double.MIN_VALUE;
        // Fix a slowdown issue within room, by allowing different searching depths
        int    depth = dungeon.isRoom(rogue) ? ROOM_DEPTH : CORRIDOR_DEPTH;
        for(int i = 0; i < moves.size(); i += 1) {
            double val = minimax(moves.get(i), monster, Double.MIN_VALUE, Double.MAX_VALUE, depth); // depth must be even
            if(val >= maxValue) {
                maxValue = val;
                maxIndex = i;
            }
        }
        
        // A special case where when the rogue realizes it can't win it just sits still,
        // to counteract this, it'll try the move that's farthest from the monster.
        if(maxValue == Double.MIN_VALUE) {
            Site farthest = rogue;
            int distance = 0;
            for(Site neigh : map.neighbors(rogue)) {
                int d2 = neigh.manhattanTo(monster);
                if(d2 >= distance) {
                    farthest = neigh;
                    distance = d2;
                }
            }
            return farthest;
        }
        
        return moves.get(maxIndex);
    } 

    /**
     *  Implementation of the minimax decision making algorithm.
     *  Algorithm begins by generating a tree, (the tree is embedded by our recursion) 
     *  then going through each level searching for the choice that would most likely be
     *  made by each player, until determining the best move for the player using it.
     *  
     *  Within the code, I used Alpha-Beta pruning which prevents us from evaluating any
     *  node whose values will never be applicable and thus gives us huge (45-50%) speed ups.
     */
    private double minimax(Site rogue, Site monster, double alpha, double beta, int depth) {
        /*
         * The following two blocks of code are to prevent possible fallings 
         * wherein the rogue tries to pass within close distance of the monster
         * on it's way to whatever it's target. I could put them in the scoring method,
         * but it speeds things up to terminate the tree early on.
         */
        for(Site neigh : map.neighbors(monster)) {
            if(rogue.equals(neigh)) return Double.MIN_VALUE;
        }

        if(dungeon.isCorridor(rogue) && !viableCorridors.contains(rogue))
            return Double.MIN_VALUE;

        /*
         * If we have reached a terminal node in our tree, then the value of the move 
         * can't be determined, so we instead use an heuristic algorithm to judge the board.
         */
        if(depth <= 0) {
            return score(rogue, monster);
        }

        /* We'll consider our rogue to be our maximizing player, meaning that it 
         *  tries to get the maximum possible heuristic value when it makes a move.
         *  Since, according to the minimax theorem, if a player's best strategy is V, 
         *  then it's opponents best strategy is -V, Monster is trying to select the move 
         *  which generates the least possible value for the rogue, making it the 
         *  minimizing player.
         *  
         *  In essence, each possible sub-move is checked, and the best one for each player is 
         *  chosen and set as the that would happen for this turn. The turn before this is set 
         *  according to the value chosen here.
         *  
         *  Alpha and beta represent minimum "barriers" which prevent unnecessary evaluation of
         *  code. Technically, they represent the value, V, for the rogue and monster respectively.
         */
        if(depth % 2 == 0) {
            Queue<Site> neighbors = map.neighbors(rogue);
            neighbors.add(rogue); // Allow for no move to be made

            for(Site neigh : neighbors) {
                alpha = Math.max(alpha, minimax(neigh, monster, alpha, beta, depth - 1));
                if(beta <= alpha) return beta;
            }

            return alpha;
        } else {
            Queue<Site> neighbors = map.neighbors(monster);
            neighbors.add(monster);

            beta = Double.MAX_VALUE;

            for(Site neigh : neighbors) {
                beta = Math.min(beta, minimax(rogue, neigh, alpha, beta, depth - 1));
                if(beta <= alpha) return beta;
            }

            return beta;
        }
    }

    /*
    * This method still needs a lot of fine tuning. It's the weighing mechanism for my rogue,
    *  and allows the rogue to distinguish between good and bad positions to be in, allowing
    *  it to make good decisions.
    *  
    *  Note: Since this heuristic algorithm isn't perfect, it sometimes comes up with strange moves,
    *        but as I improve this, those will become less frequent.
    */
    private double score(Site rogue, Site monster) {
        if(inLoop.contains(rogue))
            return 1000 * (monster.manhattanTo(rogue) - 1);
        if(safeCorridorStarts.contains(rogue))
            return 500 * (monster.manhattanTo(rogue) - 1);
        if(viableCorridors.contains(rogue))
            return 250 * monster.manhattanTo(rogue);
        if(dungeon.isRoom(rogue) && !viableCorridors.contains(rogue))
            return Double.MIN_VALUE;
        if(dungeon.isRoom(rogue) && dungeon.isWall(rogue))
            return 0;
        if(dungeon.isRoom(rogue))
            return (monster.manhattanTo(rogue) - 1);

        return 0;     
    }

    /*
     * Before the game starts, this algorithm is the starting point from which 
     * the rogue does research about the board, doing it once as opposed to each
     * turn greatly speeds up the process, and allows for more levels of depth.
     * 
     * It begins by taking in the vertices off all the corridor starts (cooridors connected 
     * to rooms) and then fills up hashtables with advantageous nodes.
     */
    private void findCorridors(Site[] vertices) {
        // Find the nodes from which corridors start, these can be used to process 
        // corridors in general
        corridorStarts = new ArrayList<Site>();
        for(Site site : vertices) {
            if(dungeon.isCorridor(site))
                for(Site neighbor : map.neighbors(site)) {
                    if(dungeon.isRoom(neighbor)) {
                        corridorStarts.add(site);
                        break;
                    }
            }
        }

        // Find loops within corridors (good example is dungeon O)
        for(Site start : corridorStarts) {
            findCorridorLoops(start, start, new ArrayList<Site>());
        }

        // Find corridors which aren't dead ends, in general, this will mean 
        // that there is a way out once you get to the other room
        for(Site start : corridorStarts) {
            findConnectedStarts(start, start, new ArrayList<Site>());
        }

        // Another special case, if a corridor connects to rooms, but is only one long,
        // then it is still viable, so add it to our algorithm.
        findSinglePassageways(corridorStarts);
    }

    /*
     * Using a depth first search to loops within the corridor. A good example being Corridor O.
     * Once this is done, we can add them to our hash table for quick look ups. I chose a depth
     * first search since it would be easy to keep track of the nodes while processing them, and also
     * because I was searching for all the loops, not just the shortest. In addition, I use an arraylist,
     * and just add and delete from the back to prevent a memory hit.
     */
    private void findCorridorLoops(Site current, Site start, ArrayList<Site> visited) {
        int index = visited.indexOf(current);
        if((index != -1) && visited.size() > 0) {
            if((visited.size() - index) > 2 && visited.size() > 2)  {
                visited.add(current);
                //System.out.println(visited);
                //corridorLoops.add(new ArrayList<Site>(visited));
                safeCorridorStarts.add(visited.get(0));
                viableCorridors.addAll(visited);

                // Add all the elements within the loop, not the elements leading to them.
                inLoop.addAll(visited.subList(visited.indexOf(visited.get(visited.size() - 1)), 
                              visited.size() - 1));
                visited.remove(visited.size() - 1);
                return;
            }
        }
        else {
            visited.add(current);
            for(Site s : map.neighbors(current)) {
                if(dungeon.isCorridor(s)) findCorridorLoops(s, start, visited);
            }
            visited.remove(visited.size() - 1);
        }
    }

    /*
     * Similar to the above function in pretty much every respect, but instead of looking for loops
     * it checks for corridors that don't lead to dead ends. 
     */
    private void findConnectedStarts(Site current, Site start, ArrayList<Site> visited) {
        int index = visited.indexOf(current);
        if((index != -1) && visited.size() > 0) {
            if(corridorStarts.indexOf(current) != -1 && visited.size() > 2)  {
                //visited.add(current);
                //corridorLoops.add(new ArrayList<Site>(visited));
                safeCorridorStarts.add(visited.get(0));
                viableCorridors.addAll(visited);
                //System.out.println(visited);
                //visited.remove(visited.size() - 1);
                return;
            }
        }
        else {
            visited.add(current);
            for(Site s : map.neighbors(current)) {
                if(dungeon.isCorridor(s)) findConnectedStarts(s, start, visited);
            }
            visited.remove(visited.size() - 1);
        }
    }
    
    /*
     *  An easy iteration through all the corridor starts to see it any of them
     *  are one long corridors between two rooms. Dungeon Q is an example of there
     *  this is necessary.
     */
    private void findSinglePassageways(ArrayList<Site> corridorStarts) {
        for(Site start : corridorStarts) {
            int i = 0;
            for(Site neigh : map.neighbors(start)) {
                if(dungeon.isRoom(neigh)) i += 1;
            }
            if(i > 1) {
                viableCorridors.add(start);
                safeCorridorStarts.add(start);
            }

        }
    }
}