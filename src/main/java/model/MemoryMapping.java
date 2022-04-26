package model;

import java.util.*;

public class MemoryMapping {
    private Map<Integer, Interval> objectsMemoryLocationMap;
    private List<Integer> listOfObjectsInStack;



    public void mapObjectsLocation(List<List<String>> records, Graph graph) {
        objectsMemoryLocationMap = new HashMap<>();
        for (List<String> heapRecord : records) {
            int key = Integer.parseInt(heapRecord.get(0));
            int start = Integer.parseInt(heapRecord.get(1));
            int end = Integer.parseInt(heapRecord.get(2));
            objectsMemoryLocationMap.put(key, new Interval(start, end));
            graph.addNode(new Node(key));
        }
    }

    public void StoreStackObjectsInList(List<String> objectsList) {
        listOfObjectsInStack = new ArrayList<>();
        for (String object : objectsList)
            listOfObjectsInStack.add(Integer.valueOf(object));
    }


    public Map<Integer, Interval> getObjectsMemoryLocationMap() {
        return objectsMemoryLocationMap;
    }

    public List<Integer> getListOfObjectsInStack() {
        return listOfObjectsInStack;
    }

}