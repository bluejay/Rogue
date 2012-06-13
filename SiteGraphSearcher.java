import java.util.*;

/**
 * Provides various information about the provided graph helpful to a Monster.
 * 
 * @author Steven Weiner
 * @version May 31, 2011
 */
public class SiteGraphSearcher {
    private Graph<Site> graph;

    /**
     * Constructs a SiteGraphSearcher from the given Graph graph.
     */
    public SiteGraphSearcher(Graph<Site> graph) {
        this.graph = graph;
    }

    /**
     * Returns the site on the shortest path to target from node. Seems to be of quadratic complexity.
     * 
     * @return the next Site on the shortest path from node to target
     * @param node Starting position of Monster
     * @param target Starting position of Rogue
     */
    public Site siteOnShortestPath(Site node, Site target) {
        //return bidiShortestSiteStack(node, target).pop();//here in case you want to test the other one
        return pathFromStack(node, shortestSiteStack(node, target)).pop();
    }

    /**
     * Implements a bidirectional depth-limited iterative deepening depth-first search to find the 
     * next best move for a Monster starting on node.
     * It has to be depth-limited because there are boards that are solved for Rogues.
     * An iterative deepening search has the main advantages of both breadth-first and depth-first
     * searches; it will find the shortest path, and it seems to only have linear space complexity.
     * Thus, it will not run into a StackOverflow or HeapOverflow error like other people were
     * unless an extraordinarily large maximum depth is given.
     * However, since it depends on isBestMovebidiDLS, it's time complexity is probably O(k^n*n^2),
     * so since the n^2 is pretty much negligible that's O(k^n) where n is the maximum depth. 
     * This is a pretty high time complexity.
     * Could be made faster by using the manhattanDistance method to cut out moves that take us 
     * farther from the Rogue than we already are.
     * However, as the necessary maximum depth decreases, so does the time it takes to find the best move.
     * If the maxDepth is sufficiently increased, a Monster correctly using this method should
     * always win on a board that is solved for a Monster.
     * 
     * @param node Starting position of Monster
     * @param target Starting poistion of Rogue
     * @param maxDepth Maximum depth to search to
     * @return next best move if there is a way to win after maxDepth steps. Else null.
     */
    public Site bestMovebidiIDDFS(Site node, Site target, int maxDepth) {
        for(int i= 0; i<maxDepth; i++) {
            for(Site from: graph.neighbors(node)) {
                if(isBestMovebidiDLS(from, target, i))
                    return from;
            }
        }
        return null;
    }

    /**
     * Implements a bidirectional depth-limited search to find if there is a way to always catch in
     * under depth moves a Rogue starting on target if the Monster starts at node.
     * From my observations and from what I know, although I have not done a detailed analysis I'm 
     * guessing that the time complexity is O(k^n) where n is the maximum depth. Actually, it seems like 
     * it's O(n^n), but I can't figure out why that would be, so O(k^n) is close enough. Actually, since
     * it keeps on having to redo things, like first do maximum depth 1 then 2 then 3 and so on, 
     * I think the complexity is probably actually a lot worse than that, but I don't feel like actually
     * calculating now. I may later though just for fun.
     * However, since it's a depth-first search and garbage collects at the end of each method in the 
     * recursion, the space complexity is probably O(n), where n is still the maximum depth.
     * Thus, it will not run into a StackOverflow or HeapOverflow error like other people were
     * unless an extraordinarily large maximum depth is given. It further reduces possibility of
     * a StackOverflow by using booleans to check if it is the best move rather than Sites and actually
     * keeping track of the moves. While this does increase the chance that a HeapOverflow error will
     * occur, this is very unlikely. By the time it would have run out of heap space the user would
     * have long since become quite impatient and annoyed at the delay and quit, so a HeapOverflow 
     * error should not be a problem, except in the case of a very patient person who wants to 
     * see if my Monster can beat his/her Rogue. And in that case, he/she would be disappointed anyway
     * if he/she reduced the maxDepth by even 1, because it would.
     * 
     * @param node The Monster's starting location
     * @param target the Rogue's starting location
     * @param depth the maximum depth to check to
     * @return true if there is a way to catch the Rogue within depth steps. Else false.
     */
    public boolean isBestMovebidiDLS(Site node, Site target, int depth) {
        if(depth >= 0) {
            boolean worked = true;
            for(Site tChild: graph.neighbors(target)) //base case
                worked = worked && graph.neighbors(node).contains(tChild);

            if(worked) return worked;
            else {
                for(Site nChild: graph.neighbors(node)) {
                    boolean worked2= true;
                    for(Site tChild: graph.neighbors(target)) {
                        boolean temp= isBestMovebidiDLS(nChild, tChild, depth-1);
                        if(!temp) {
                            worked2= temp;
                            break;
                        }
                    }
                    if(worked2) return worked2;
                }
            }
        }
        return false;
    }

    private Stack<Site[]> shortestSiteStack(Site from, Site to) {
        Queue<Site[]> temp = new LinkedList<Site[]>();
        Stack<Site[]> moves = new Stack<Site[]>();

        graph.mark(from);
        for(Site site: graph.neighbors(from)) {
            if(!graph.isMarked(site)) {
                temp.add(new Site[] {from,site});
                graph.mark(site);
            }
        }

        while(!temp.isEmpty()) {
            Site[] sites = temp.poll();
            moves.push(sites);
            if(sites[1].equals(to)) break;
            for(Site site: graph.neighbors(sites[1])) {
                if(!graph.isMarked(site)) {
                    temp.add(new Site[] {sites[1],site});
                    graph.mark(site);
                }
            }
        }
        graph.clearMarks();

        return moves;
    }

    /**
     * Implements a bidirectional breadth-first search to find the shortest path between Sites
     * from and to. Currently not completely implemented. More detailed notes in the code for the method.
     * If it worked it would be quadratic (O(n^2)), but would still be a bit faster than a non-bidi search.
     * @param from Starting position
     * @param to Ending position
     * @return Stack of Sites containing the shortest path in order to be taken.
     */
    public Stack<Site> bidiShortestSiteStack(Site from, Site to) {
        boolean fFound= false, tFound= false;  
        Site fConnectSite = null, tConnectSite = null;
        Queue<Site[]> fTemp = new LinkedList<Site[]>(), tTemp = new LinkedList<Site[]>();  
        ArrayList<Site> fromList = new ArrayList<Site>(), toList = new ArrayList<Site>(); 
        Stack<Site[]> fMoves = new Stack<Site[]>(), tMoves = new Stack<Site[]>();
        Stack<Site> moves = new Stack<Site>();  

        graph.mark(from);
        for(Site site: graph.neighbors(from)) {
            if(site.equals(to)) {}
            fTemp.add(new Site[] {from,site});
            graph.mark(site);
            fromList.add(site);
        }   

        graph.mark(to);
        for(Site site: graph.neighbors(to)) {
            tTemp.add(new Site[] {to,site});
            if(graph.isMarked(site) && fromList.contains(site)) {
                tFound= true;
                break;
            }
            toList.add(site);
            graph.mark(site);
        } 

        if(tFound) {
            moves.add(to);
            return moves;
        }

        bigBreak:
        while(!fTemp.isEmpty() && !tTemp.isEmpty()) {
            Site[] fSites = fTemp.poll();
            fMoves.push(fSites);
            
            Site[] tSites = tTemp.poll();
            tMoves.push(tSites);
            
            for(Site site: graph.neighbors(fSites[1])) {    
                fTemp.add(new Site[] {fSites[1],site});
                if(graph.isMarked(site) && toList.contains(site)) {
                    fromList.add(site);
                    fConnectSite = site;
                    fFound = true;
                    break bigBreak;
                }
                else 
                    graph.mark(site); 
            }
            
            for(Site site: graph.neighbors(tSites[1])) {
                tTemp.add(new Site[] {tSites[1],site});//I chose to put them in the same way.
//While this does make it a bit more difficult to code, the consistency makes it less confusing.
                if(graph.isMarked(site) && fromList.contains(site)) {
                    toList.add(site);
                    tConnectSite = site;
                    tFound = true;
                    break bigBreak;
                }
                else 
                    graph.mark(site);
            }
        }
        if(fFound) {
            Stack<Site> fTempMoves = pathFromStack(from, fMoves);
            Site[] sites = null;
            while(!tMoves.isEmpty()) {
                sites = tMoves.pop();
                if(sites[1].equals(fConnectSite)) break;
            }
            fMoves.push(sites);
            Stack<Site> tTempMoves = pathFromStack(to, tMoves);
            //need to combine them
        }
        else if(tFound) {
            Stack<Site> tTempMoves = pathFromStack(to, tMoves);
            Site[] sites = null;
            while(!fMoves.isEmpty()) {
                sites = fMoves.pop();
                if(sites[1].equals(tConnectSite)) break;
            }
            tMoves.push(sites);
            Stack<Site> fTempMoves = pathFromStack(from, fMoves);
            //need to combine them
        }
        return moves;
    }

    private Stack<Site> pathFromStack(Site node, Stack<Site[]> moves) {
        Stack<Site> path = new Stack<Site>();
        
        Site[] tempMoves = moves.pop();
        Site[] tempMoves2 = null;   

        path.add(tempMoves[1]);
        while(!moves.isEmpty()) {   
            tempMoves2 = moves.pop();
            if(tempMoves[0].equals(node)) {
                path.push(tempMoves[0]);
                break;
            }
            if(tempMoves2[0].equals(node) && tempMoves[0].equals(tempMoves2[1])) {
                path.push(tempMoves[0]);
                break;
            }
            if(tempMoves[0].equals(tempMoves2[1])) {
                path.push(tempMoves[0]);
                tempMoves = tempMoves2;  
            }
        }
        return path;
    }
}