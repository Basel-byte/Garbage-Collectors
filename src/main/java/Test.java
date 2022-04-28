import garbage_collectors.MarkAndCompactCollector;
import garbage_collectors.MarkAndSweepCollector;
import garbage_collectors.MarkCollector;
import model.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Test {


    private void run(int num) throws IOException {

        String heapFilePath = "Input\\set1\\heap.csv";
        String rootsFilePath = "Input\\set1\\roots.txt";
        String pointersFilePath = "Input\\set1\\pointers.csv";
        String newHeapFilePath = "Input\\set1\\new_heap" + num + ".csv";

        // Creating an instance of class FieUtil and start reading data from input files
        FileUtil fileUtil = new FileUtil();
        List<List<String>> heapRecords = fileUtil.readFromCSVFile(heapFilePath);
        List<String> objectsInStack = fileUtil.readFromTextFile(rootsFilePath);
        List<List<String>> pointerRecords = fileUtil.readFromCSVFile(pointersFilePath);

        // Creating objects of Graph and MemoryMapping classes
        Graph graph = new Graph();
        MemoryMapping memoryMapping = new MemoryMapping();
        memoryMapping.mapObjectsLocation(heapRecords, graph);
        memoryMapping.StoreStackObjectsInList(objectsInStack);
        graph.buildGraph(pointerRecords);

        // Getter methods to get the (object's identifier) to (interval in memory) map,
        // list of objects in roots file and get adjacency list representing graph of pointers of objects

        Map<Integer, Interval> objectsMemoryLocationsMap = memoryMapping.getObjectsMemoryLocationMap();
        List<Integer> objectsList = memoryMapping.getListOfObjectsInStack();
        Map<Node, List<Node>> adjacencyList = graph.getAdjacencyList();

        // Run Mark And Sweep Garbage Collector
        MarkAndSweepCollector markAndSweepCollector = new MarkAndSweepCollector(objectsMemoryLocationsMap, objectsList, adjacencyList);
        markAndSweepCollector.implementMarkAndSweep();
        LinkedHashMap<Integer, Interval> newHeapMap = markAndSweepCollector.getSortedMap();
        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath);

        // Run Mark And Compact Garbage Collector
        MarkAndCompactCollector markAndCompactCollector = new MarkAndCompactCollector(objectsMemoryLocationsMap, objectsList, adjacencyList);
        markAndCompactCollector.implementMarkAndCompact();
        newHeapMap = markAndCompactCollector.getSortedMap();
        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath);

        // Run G1 Garbage Collector


        // Run Copy Garbage Collector

    }

    public static void main(String[] args) throws IOException {
        Test runner = new Test();
        runner.run(2);
    }
}
