package garbage_collectors;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import model.Interval;
import model.Node;

import java.util.*;

public class CopyCollector {
    private final Map<Integer, Interval> fromHeap;
    private final List<Integer> objectsInStack;
    private final int heapSize;
    private LinkedHashMap<Integer, Interval> toHeap;
    private final Set<Node> setOfCopiedObjects;
    private final Map<Node, List<Node>> adjacencyList;

    public CopyCollector(Map<Integer, Interval> fromHeap, List<Integer> objectsInStack, Map<Node, List<Node>> adjacencyList){
        this.fromHeap = fromHeap;
        this.objectsInStack = objectsInStack;
        this.adjacencyList = adjacencyList;
        this.setOfCopiedObjects = new HashSet<>();
        this.heapSize = 1000;
    }

    public LinkedHashMap<Integer, Interval> CopyGCOnTrack(){
        int startOFInterval = 1;
        toHeap = new LinkedHashMap<>();
        if (fromHeap.size()>heapSize/2){
            System.out.println("Copy GC cannot perform. Exceeded half heap size!");
            return toHeap;
        }
        for (Integer currentObjectID : objectsInStack){
            Node currentNode = new Node(currentObjectID);
            if (setOfCopiedObjects.contains(currentNode)) continue;
            int currentIntervalSize = fromHeap.get(currentObjectID).getSize();
            int endOfInterval = startOFInterval + currentIntervalSize;
            toHeap.put(currentObjectID, new Interval(startOFInterval,endOfInterval));
            setOfCopiedObjects.add(currentNode);
            startOFInterval = copyChildren(currentNode,endOfInterval+1);
        }
        return toHeap;
    }

    private int copyChildren(Node parentNode, int startOFInterval) {
        int endOfInterval = 0;
        for (Node childNode : adjacencyList.get(parentNode)) {
            if (setOfCopiedObjects.contains(childNode)) continue;
            int currentIntervalSize = fromHeap.get(childNode.getId()).getSize();
            endOfInterval = startOFInterval + currentIntervalSize;
            toHeap.put(childNode.getId(), new Interval(startOFInterval, endOfInterval));
            setOfCopiedObjects.add(childNode);
            startOFInterval = copyChildren(childNode,endOfInterval + 1);
        }
        return startOFInterval;
    }
}
