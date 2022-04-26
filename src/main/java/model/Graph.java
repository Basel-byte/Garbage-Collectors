package model;

import java.util.*;

public class Graph {
    private Map<Node, List<Node>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addNode(Node v) {
        adjacencyList.putIfAbsent(v, new ArrayList<>());
    }

    private void addEdge(Node u, Node v) {
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public void buildGraph(List<List<String>> records) {
        for (List<String> record : records) {
            Node u = new Node(Integer.parseInt(record.get(0)));
            Node v = new Node(Integer.parseInt(record.get(1)));
            addEdge(u, v);
        }
    }

    public Map<Node, List<Node>> getAdjacencyList() {
        return adjacencyList;
    }

}