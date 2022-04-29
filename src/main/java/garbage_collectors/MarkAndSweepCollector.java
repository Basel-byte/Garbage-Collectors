package garbage_collectors;

import model.Interval;
import model.Node;

import java.util.*;

public class MarkAndSweepCollector extends MarkCollector {

    public MarkAndSweepCollector(Map<Integer, Interval> objectsMemoryLocationMap, List<Integer> objectsInStack, Map<Node, List<Node>> adjacencyList) {
        super(objectsMemoryLocationMap, objectsInStack, adjacencyList);
    }

    public void implementMarkAndSweep() {
        markObjects();
        sweepUnusedObjects();
        Set<Map.Entry<Integer, Interval>> entries = objectsMemoryLocationMap.entrySet();
        sortedMap = new LinkedHashMap<>(entries.size());
        List<Map.Entry<Integer, Interval>> entryList = new ArrayList<>(entries);
        entryList.sort(new Comparator<Map.Entry<Integer, Interval>>() {
            @Override
            public int compare(Map.Entry<Integer, Interval> o1, Map.Entry<Integer, Interval> o2) {
                int start1 = o1.getValue().getStart();
                int start2 = o2.getValue().getStart();
                return Integer.compare(start1, start2);
            }
        });
        for (Map.Entry<Integer, Interval> entry : entryList)
            sortedMap.put(entry.getKey(), entry.getValue());
    }

}