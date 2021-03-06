package garbage_collectors;

import model.Interval;
import model.Node;

import java.util.*;

public abstract class MarkCollector {
    final Map<Integer, Interval> objectsMemoryLocationMap;
    LinkedHashMap<Integer, Interval> sortedMap;
    private final List<Integer> objectsInStack;
    private final Map<Node, List<Node>> adjacencyList;
    private final Set<Node> setOfUsedObjects;

    public MarkCollector(Map<Integer, Interval> objectsMemoryLocationMap, List<Integer> objectsInStack, Map<Node, List<Node>> adjacencyList) {
        this.objectsMemoryLocationMap = objectsMemoryLocationMap;
        this.objectsInStack = objectsInStack;
        this.adjacencyList = adjacencyList;
        setOfUsedObjects = new HashSet<>();
    }

    void markObjects() {
        for (int object : objectsInStack)
            dfs(new Node(object));
    }

    void dfs(Node v) {
        setOfUsedObjects.add(v);
        for (Node u : adjacencyList.get(v)) {
            if (!setOfUsedObjects.contains(u))
                dfs(u);
        }
    }

    void sweepUnusedObjects() {
        Set<Map.Entry<Integer, Interval>> entries = new HashSet<>(objectsMemoryLocationMap.entrySet());
        for (Map.Entry<Integer, Interval> entry : entries) {
            if (!setOfUsedObjects.contains(new Node(entry.getKey())))
                objectsMemoryLocationMap.remove(entry.getKey());
        }
    }

    public LinkedHashMap<Integer, Interval> getSortedMap() {
        return sortedMap;
    }
}