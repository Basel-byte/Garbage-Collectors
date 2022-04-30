package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block extends Interval {

    int index;
    int offsetFromBlockStart;
    boolean free;

    List<Node> usedObjects;
    List<Node> garbageObjects;
    Map<Integer, Interval> objectsIdAddressMap;

    public Block(int index, int start, int end) {
        super(start, end);
        this.index = index;
        objectsIdAddressMap = new HashMap<>();
        usedObjects = new ArrayList<>();
        garbageObjects = new ArrayList<>();
        offsetFromBlockStart = 0;
        free = true;
    }

    public int getIndex() {
        return index;
    }

    public boolean isFree() { return free; }

    public void setFree() {
        this.free = true;
    }

    public List<Node> getUsedObjects() {
        return usedObjects;
    }

    public Map<Integer, Interval> getObjectsIdAddressMap() {
        return objectsIdAddressMap;
    }

    public void sweep() {
        for (Node obj : garbageObjects)
            objectsIdAddressMap.remove(obj.getId());

        garbageObjects.clear();
    }

    public void addObjectToGarbage(Node o, Interval interval) {
        garbageObjects.add(o);
        objectsIdAddressMap.put(o.getId(), interval);
        free = false;
    }

    public void addObjectToUsed(Node o, Interval interval) {
        usedObjects.add(o);
        objectsIdAddressMap.put(o.getId(), interval);
    }

    public void moveObjectToUsed(Node o) {
        usedObjects.add(o);
    }

    public void moveObjectToGarbage(Node o) {
        garbageObjects.add(o);
    }

    public void removeObjectFromGarbage(Node object) {
        garbageObjects.remove(object);
    }

    public int getOffsetFromBlockStart() {
        return offsetFromBlockStart;
    }

    public void setOffsetFromBlockStart(int offsetFromBlockStart) {
        this.offsetFromBlockStart = offsetFromBlockStart;
    }
}
