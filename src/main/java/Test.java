import garbage_collectors.*;
import model.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Test {
    private StringBuilder newHeapFilePath;
    private int setNum = 0;
    private void updatePath(int num){
        this.newHeapFilePath = new StringBuilder();
        this.newHeapFilePath.append("Input\\set" + this.setNum + "\\new_heap");
        switch (num){
            case 1: this.newHeapFilePath.append("_MarkAndSweep");
                    break;
            case 2: this.newHeapFilePath.append("_MarkAndCompact");
                    break;
            case 3: this.newHeapFilePath.append("_G1");
                    break;
            case 4: this.newHeapFilePath.append("_Copy");
                    break;
        }
        this.newHeapFilePath.append(".csv");
    }


    private void run(int setNum) throws IOException {
        this.setNum = setNum;
        String heapFilePath =     "Input\\set" + setNum + "\\heap.csv";
        String rootsFilePath =    "Input\\set" + setNum + "\\roots.txt";
        String pointersFilePath = "Input\\set" + setNum + "\\pointers.csv";
        int num = 1;

        int heapSize = 1600;
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
        this.updatePath(num++);
        MarkAndSweepCollector markAndSweepCollector = new MarkAndSweepCollector(objectsMemoryLocationsMap, objectsList, adjacencyList);
        markAndSweepCollector.implementMarkAndSweep();
        LinkedHashMap<Integer, Interval> newHeapMap = markAndSweepCollector.getSortedMap();
        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath.toString());

        // Run Mark And Compact Garbage Collector
        this.updatePath(num++);
        MarkAndCompactCollector markAndCompactCollector = new MarkAndCompactCollector(objectsMemoryLocationsMap, objectsList, adjacencyList);
        markAndCompactCollector.implementMarkAndCompact();
        newHeapMap = markAndCompactCollector.getSortedMap();
        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath.toString());

        // Run G1 Garbage Collector
        this.updatePath(num++);
        G1Collector g1Collector = new G1Collector(objectsMemoryLocationsMap, objectsList, adjacencyList, heapSize);
        g1Collector.implementG1Collector();
        newHeapMap = g1Collector.getSortedMap();
        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath.toString());


        // Run Copy Garbage Collector
        this.updatePath(num);
        CopyCollector copyCollector = new CopyCollector(objectsMemoryLocationsMap,objectsList,adjacencyList);
        newHeapMap = copyCollector.CopyGCOnTrack();
        fileUtil.writeInCSVFile(newHeapMap, newHeapFilePath.toString());

    }

    public static void main(String[] args) throws IOException {
        Test runner = new Test();
        //pass number of test to be executed
        runner.run(7);
    }
}
