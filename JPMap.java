import java.util.Queue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * An implementation of a Graph using both HashMaps and
 * a double array list to store indices and edges between nodes.
 * 
 * @author Jay Palekar
 * @version 6/4/12
 */
public class JPMap<E> implements Graph<E> {
    private int capacity = 0;
    private int size = 0;

    // Hashmaps allow for quick convertion between nodes and 
    // their corresponding indices.
    private HashMap<E, Integer> verticesIndices;
    private HashMap<Integer, E> vertices;

    private final int[][] data;
    private HashSet<E> marked;

    @SuppressWarnings({"unchecked"})
    public JPMap(int capacity) {
        this.capacity = capacity;
        this.vertices = new HashMap<Integer, E>(capacity);
        this.verticesIndices = new HashMap<E, Integer>(capacity);
        this.marked = new HashSet<E>();
        this.data = new int[capacity][capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean hasVertex(E vertex) {
        return verticesIndices.containsKey(vertex);
    }

    @SuppressWarnings("unchecked")
    public Queue<E> neighbors(E vertex) {
        int k = 0;
        LinkedList<E> neighbors = new LinkedList<E>();
        k = verticesIndices.get(vertex);
        
        for(int i = 0; i < size; i += 1) {
            if(data[k][i] > 0) {
                E neighbor = vertices.get(i);
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    public void addVertex(E vertex) {
        verticesIndices.put(vertex, size);
        vertices.put(size, vertex);
        size += 1;
    }

    public void addEdge(E fromVertex, E toVertex) {
        int a = verticesIndices.get(fromVertex);
        int b = verticesIndices.get(toVertex);
        
        // Increment both directions, since the graph is bidirectional
        data[a][b] += 1;
        data[b][a] += 1;
    }

    public void clearMarks() {
        this.marked = new HashSet<E>();
    }
    public boolean isMarked(E vertex) {
        return marked.contains(vertex);
    }

    public void mark(E vertex) {
        marked.add(vertex);
    }
}
