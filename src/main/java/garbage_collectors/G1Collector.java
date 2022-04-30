package garbage_collectors;

import model.Block;
import model.Interval;
import model.Node;

import java.util.*;

public class G1Collector {
    private final List<Node> objectsInStack;
    private final Map<Integer, Interval> objectsMemoryLocationMap;
    private final Map<Node, List<Node>> adjacencyList;
    private final Map<Integer, Integer> objectsBlockIndexMap;
    private final int blockSize;

    Block[] blocks = new Block[16];

    public G1Collector(Map<Integer, Interval> objectsMemoryLocationMap, List<Integer> objectsInStack, Map<Node, List<Node>> adjacencyList, int heapSize) {
        this.objectsMemoryLocationMap = objectsMemoryLocationMap;
        this.objectsInStack = castToNodesList(objectsInStack);
        this.adjacencyList = adjacencyList;
        this.blockSize = heapSize / 16;
        this.objectsBlockIndexMap = new HashMap<>();
    }

    private List<Node> castToNodesList(List<Integer> integerList) {
        List<Node> nodes = new ArrayList<>();
        for (Integer objectID : integerList) {
            nodes.add(new Node(objectID));
        }
        return nodes;
    }

    public void implementG1Collector() {
        initializeBlocks();
        addObjectsToBlocks();
        mark();
        freeGarbageBlocks();
        defragment();
    }

    private void initializeBlocks() {
        int startingAddress = 0;
        for (int i = 0; i < 16; i++) {
            Block block = new Block(i , startingAddress, (startingAddress + blockSize - 1) );
            blocks[i] = block;
            startingAddress += blockSize;
        }
    }

    private void addObjectsToBlocks() {
        for (Map.Entry<Integer, Interval> entry : objectsMemoryLocationMap.entrySet()) {
            // Create the object node
            int objectId = entry.getKey();
            Node object = new Node(objectId);
            // Calculate at which block it should be added
            int objectStartingIndex = entry.getValue().getStart();
            int blockIndex = objectStartingIndex / blockSize; // integer division >> gets floored
            // add to garbageList of the block for mark algorithm
            blocks[blockIndex].addObjectToGarbage(object, entry.getValue());
            // keeps track which objects are at which blocks
            objectsBlockIndexMap.put(objectId, blockIndex);
        }
    }

    private void mark() {
        for (Node node : objectsInStack)
            dfs(node);
    }

    private void dfs(Node object) {
        if (objectInGarbage(object)) {
            int blockIndex = objectsBlockIndexMap.get(object.getId());
            blocks[blockIndex].moveObjectToUsed(object);
            blocks[blockIndex].removeObjectFromGarbage(object);
        }

        for (Node childNode : adjacencyList.get(object)) {
            if (objectInGarbage(childNode))
                dfs(childNode);
        }
    }

    private boolean objectInGarbage(Node object) {
        int objectBlockIndex = objectsBlockIndexMap.get(object.getId());
        for (Node o : blocks[objectBlockIndex].getUsedObjects()) {
            if (o.getId() == object.getId())
                return false;
        }
        return true;
    }

    private void freeGarbageBlocks() {
        for (Block block : blocks) {
            if (block.isFree())
                continue; // skip

            if (block.getUsedObjects().isEmpty()) {
                block.sweep();
                block.setFree();
            }
        }
    }

    private void defragment() {
        for (Block currentBlock : blocks) {
            if (currentBlock.isFree()) continue;

            // use iterator to avoid modifying the list while iterating over it
            Iterator<Node> iterator = currentBlock.getUsedObjects().iterator();
            while (iterator.hasNext()) {
                Node object = iterator.next();
                Block firstFreeBlock = getFirstFreeBlock(object);

                /* iterator.remove():
                 *         removes from the last object returned from iterator.next()
                 *  the removing from the list has to be done with
                 *  the iterator to avoid ConcurrentModificationException
                 *  i.e., modifying the list while iterating over it
                 */
                iterator.remove(); // removes "object" from currentBlockUsedObjects list
                moveObjectToFirstFreeBlock(object, currentBlock, firstFreeBlock);
            }
        }
    }

    private Block getFirstFreeBlock(Node object) {
        for (Block currentBlock : blocks) {
            if (currentBlock.isFree() && blockCanFitObject(currentBlock, object))
                return currentBlock;
        }
        return null;
    }

    private boolean blockCanFitObject(Block block, Node object) {
        // assuming no object can be larger than a block size
        if (block.getUsedObjects().isEmpty()) return true;

        /*
         * even though objectsMemoryLocationMap.get(objectId) returns
         * it's first interval and not it's current interval, it doesnt matter
         * since we use it only to get the object size
         */

        Interval objInterval = objectsMemoryLocationMap.get(object.getId());
        int objectSize = objInterval.getEnd() - objInterval.getStart() + 1;
        return (blockSize - block.getOffsetFromBlockStart()) - objectSize >= 0;
    }

    private void moveObjectToFirstFreeBlock(Node object, Block originalBlock, Block freeBlock) {
        Interval oldInterval = originalBlock.getObjectsIdAddressMap().get(object.getId());

        originalBlock.moveObjectToGarbage(object);
        if (originalBlock.getUsedObjects().size() == 0) {
            originalBlock.sweep();
        }

        int objectSize = oldInterval.getEnd() - oldInterval.getStart() + 1;
        int newObjStartIndex = (freeBlock.getIndex() * blockSize) + freeBlock.getOffsetFromBlockStart();
        Interval newInterval = new Interval(newObjStartIndex, newObjStartIndex + objectSize - 1);

        freeBlock.addObjectToUsed(object, newInterval);
        freeBlock.setOffsetFromBlockStart(freeBlock.getOffsetFromBlockStart() + objectSize);
    }

    public LinkedHashMap<Integer, Interval> getSortedMap() {
        LinkedHashMap<Integer, Interval> sortedMap = new LinkedHashMap<>();
        for (Block block : blocks) {
            for (Node object : block.getUsedObjects()) {
                sortedMap.put(object.getId(), block.getObjectsIdAddressMap().get(object.getId()));
            }
        }
        return sortedMap;
    }

}
