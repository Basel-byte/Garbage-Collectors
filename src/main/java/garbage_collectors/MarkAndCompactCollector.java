package garbage_collectors;

import model.Interval;
import model.Node;

import java.util.*;

public class MarkAndCompactCollector extends MarkCollector {

    public MarkAndCompactCollector(Map<Integer, Interval> objectsMemoryLocationMap, List<Integer> objectsInStack, Map<Node, List<Node>> adjacencyList) {
        super(objectsMemoryLocationMap, objectsInStack, adjacencyList);
    }

    private List<Map.Entry<Integer, Interval>> compactHeap(List<Map.Entry<Integer, Interval>> entryList) {
        int objectSize = entryList.get(0).getValue().getEnd() - entryList.get(0).getValue().getStart();
        entryList.get(0).getValue().setStart(0);
        entryList.get(0).getValue().setEnd(objectSize);
        for (int i = 1; i < entryList.size(); i++) {
            objectSize = entryList.get(i).getValue().getEnd() - entryList.get(i).getValue().getStart();
            int start = entryList.get(i - 1).getValue().getEnd() + 1;
            int end = start + objectSize;
            entryList.get(i).getValue().setStart(start);
            entryList.get(i).getValue().setEnd(end);
        }
        return entryList;
    }

    public void implementMarkAndCompact() {
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
        entryList = compactHeap(entryList);
        for (Map.Entry<Integer, Interval> entry : entryList)
            sortedMap.put(entry.getKey(), entry.getValue());
    }
}