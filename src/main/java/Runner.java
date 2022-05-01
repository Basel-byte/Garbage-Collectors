import garbage_collectors.*;
import model.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Runner {

    private final String heapFilePath;
    private final String rootsFilePath;
    private final String pointersFilePath;
    private final String newHeapFilePath;
    private final String heapSize;

    public Runner(String[] args) {
        this.heapFilePath = args[0];
        this.rootsFilePath = args[1];
        this.pointersFilePath = args[2];
        this.newHeapFilePath = args[3];
        this.heapSize = args[4];
    }

    private void run() throws IOException {

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
//        MarkAndSweepCollector markAndSweepCollector = new MarkAndSweepCollector(objectsMemoryLocationsMap, objectsList, adjacencyList);
//        markAndSweepCollector.implementMarkAndSweep();
//        LinkedHashMap<Integer, Interval> newHeapMap = markAndSweepCollector.getSortedMap();
//        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath);

        // Run Mark And Compact Garbage Collector
//        MarkAndCompactCollector markAndCompactCollector = new MarkAndCompactCollector(objectsMemoryLocationsMap, objectsList, adjacencyList);
//        markAndCompactCollector.implementMarkAndCompact();
//        LinkedHashMap<Integer, Interval> newHeapMap = markAndCompactCollector.getSortedMap();
//        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath);

        // Run G1 Garbage Collector
        G1Collector g1Collector = new G1Collector(objectsMemoryLocationsMap, objectsList, adjacencyList, Integer.parseInt(heapSize));
        g1Collector.implementG1Collector();
        LinkedHashMap<Integer, Interval> newHeapMap = g1Collector.getSortedMap();
        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath);

        // Run Copy Garbage Collector
//        CopyCollector copyCollector = new CopyCollector(objectsMemoryLocationsMap,objectsList,adjacencyList);
//        LinkedHashMap<Integer, Interval> newHeapMap = copyCollector.CopyGCOnTrack();
//        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath);
    }

    public static void main(String[] args) throws IOException {
        Runner runner = new Runner(args);
        runner.run();
    }
}
