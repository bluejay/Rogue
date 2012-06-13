import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Scanner;
/* ************************************************************************
 * 
 *  Compilation:  javac Monster.java
 * 
 *  April 2011
 *    J. Smith
 *    -- updated variable names/style
 *************************************************************************/
 /**
  * Monster that tries to catch a Rogue
  * 
  * @author Steven Weiner
  * @version May 31, 2011
  * 
  */

public class Monster {
    private Game game;
    private Dungeon dungeon;
    private JPMap<Site> graph;
    private SiteGraphSearcher graphSearcher;
    private Site[] vertices;

    /**
     * Constructs a monster in Game game
     */
    public Monster(Game game) {
        dungeon = game.getDungeon();
        graph = new JPMap<Site>(dungeon.size()*dungeon.size());
        graphSearcher = new SiteGraphSearcher(graph);
        
        this.game    = game;
        this.dungeon = game.getDungeon();
        vertices = new Site[dungeon.size()*dungeon.size()];
        int numVertices= 0;
        for(int i= 0; i<dungeon.size(); i++) {
            for(int j= 0; j<dungeon.size(); j++) {
                Site site = new Site(i,j);
                graph.addVertex(site);
                vertices[numVertices] = site;
                numVertices++;
            }
        }
        for(int i= 0; i<numVertices; i++) {
            for(int j= 0; j<numVertices; j++) {
                Site s = vertices[i];
                Site t = vertices[j];
                if(dungeon.isLegalMove(s,t))
                    graph.addEdge(s,t);
            }
        }
    }

    /**
     * @return the monster's next move as a Site. Picks next move by first checking if there is a definite way 
     * to win in 5 steps, and if not, just picking the Site on the shortest path to the Rogue's location. 
     * If there is no path to the Rogue returns null, resulting in a NullPointerException in Dungeon.
     */
    public Site move() {
        Site monster = game.getMonsterSite();
        Site rogue   = game.getRogueSite();
        
        for(Site neighbor: graph.neighbors(monster))//prevents it from having to go through the
                                                    //other method and succesfully hides a small error.
                                                    //It would almost definitely still win, just not quite as fast.
            if(neighbor.equals(rogue))
                return neighbor;
        
        Site move = graphSearcher.bestMovebidiIDDFS(monster, rogue, 5);//raise the number to win faster. But actually,
                                                                        //since the time complexity is really high
                                                                        //and depends on the number, it will probably
                                                                        //win much slower. It will be in fewer steps,
                                                                        //though.
        if(move!=null) return move;
        else           return graphSearcher.siteOnShortestPath(monster, rogue);
    }
    
    /**
     * @return ArrayList<Site> containing corners
     * Currently does not make sure it's an actual corner. Only finds if there are only three room exits.
     */
    private ArrayList<Site> cornerFinder() {
        ArrayList<Site> corners = new ArrayList<Site>();
        for(Site vertex: vertices) {
            if(!dungeon.isRoom(vertex)) continue;
            
            Queue<Site> tempNeighbors = graph.neighbors(vertex);
            ArrayList<Site> neighbors = new ArrayList<Site>();
            for(Site neighbor: tempNeighbors) if(!neighbor.equals(vertex)) neighbors.add(neighbor);
            
            boolean itsacorner = true;
            if(neighbors.size()<=3) {
                for(Site neighbor: neighbors) {
                    if(!dungeon.isRoom(neighbor)) {
                        itsacorner = false;
                        break;
                    }
                }
                if(itsacorner) corners.add(vertex);
            }
        }
        return corners;
    }
}
