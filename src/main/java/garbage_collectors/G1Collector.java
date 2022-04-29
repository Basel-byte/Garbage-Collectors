package garbage_collectors;

import model.Block;
import model.Interval;
import model.Node;

import java.util.*;

public class G1Collector {
    private final Map<Integer, Interval> objectsMemoryLocationMap;
    private final List<Node> objectsInStack; // TODO
    private final Map<Node, List<Node>> adjacencyList;
    private final int blockSize;
    private Map<Integer, Integer> objectsBlockIndexMap;
    private LinkedHashMap<Integer, Interval> sortedMap;

    Block[] blocks = new Block[16];

    public G1Collector(Map<Integer, Interval> objectsMemoryLocationMap, List<Integer> objectsInStack, Map<Node, List<Node>> adjacencyList, int heapSize) {
        this.objectsMemoryLocationMap = objectsMemoryLocationMap;
        this.objectsInStack = castToNodesList(objectsInStack);
        this.adjacencyList = adjacencyList;
        this.blockSize = heapSize / 16;
    }

    public void implementG1Collector() {
        initializeBlocks();
        addObjectsToBlocks();
        mark();
        freeGarbageBlocks();
        defragment();
    }

    private List<Node> castToNodesList(List<Integer> integerList) {
        List<Node> nodes = new ArrayList<>();
        for (Integer integer : integerList) {
            nodes.add(new Node(integer));
        }
        return nodes;
    }

    private void initializeBlocks() {
        int startingAddress = 0;
        for (int i=0; i < 16; i++) {
            Block block = new Block(i, startingAddress, startingAddress + blockSize - 1);
            blocks[i] = block;
            startingAddress += blockSize;
        }
    }

    private void addObjectsToBlocks() {
        for (Map.Entry<Integer, Interval> entry: objectsMemoryLocationMap.entrySet()){
            int objectId = entry.getKey();
            int objectStartingIndex = entry.getValue().getStart();
            int blockIndex = (int) Math.floor(objectStartingIndex / blockSize);

            Node object = new Node(objectId);
            // add to garbage for mark algorithm
            blocks[blockIndex].addObjectToGarbage(object, entry.getValue());
            objectsBlockIndexMap.put(objectId, blockIndex);
        }
    }

    private void mark() {
        for (Node object : objectsInStack) {
            dfs(object);
        }
    }

    private void dfs(Node object) {
        int blockIndex = objectsBlockIndexMap.get(object.getId());
        blocks[blockIndex].moveObjectToUsed(object);
        blocks[blockIndex].removeObjectFromGarbage(object);

        for (Node childNode : adjacencyList.get(object)) {
            int childNodeBlock = objectsBlockIndexMap.get(childNode.getId());

            boolean childIsInGarbage = true;
            for (Node o: blocks[childNodeBlock].getUsedObjects())
                if (o.getId() == childNode.getId()) childIsInGarbage = false;

            if (childIsInGarbage)
                dfs(childNode);
        }
    }

    private void freeGarbageBlocks() {
        for (Block block : blocks) {
            if(block.isFree())
                continue; // skip

            if (block.getUsedObjects().size() == 0) {
                block.sweep();
                block.setFree();
            }
        }
    }

    private void defragment() {
        for (Block block : blocks) {
            if(block.isFree())
                continue;

            for (Node object: block.getUsedObjects()) {
                Block firstFreeBlock = getFirstFreeBlock(object);

                // move object from "block" to "firstFreeBlock"

                block.removeObjectFromUsed(object);
                if (block.getUsedObjects().size() == 0)
                    block.sweep();

                Interval oldInterval = block.getObjectsIdAddressMap().get(object.getId());
                int objectSize = oldInterval.getEnd() - oldInterval.getStart() + 1;

                int newObjStartIndex = ( firstFreeBlock.getIndex() * blockSize ) + block.getFirstAvailableIndex();
                Interval newInterval = new Interval(newObjStartIndex, newObjStartIndex + objectSize -1);
                firstFreeBlock.addObjectToUsed(object, newInterval);
                block.setFirstAvailableIndex(block.getFirstAvailableIndex() + objectSize);
            }
        }
    }

    private Block getFirstFreeBlock(Node object) {
        for (Block currentBlock: blocks){
            if (currentBlock.isFree() && blockCanFitObject(currentBlock, object))
                return currentBlock;
        }
        return null;
    }

    private boolean blockCanFitObject(Block block, Node object) {
        // assuming no object can be larger than a block size
        if(block.getUsedObjects().size() == 0) return true;

        Interval objInterval = block.getObjectsIdAddressMap().get(object.getId());
        int objectSize = objInterval.getEnd() - objInterval.getStart() + 1;
        return (blockSize - block.getFirstAvailableIndex()) - objectSize >= 0;
    }

}
