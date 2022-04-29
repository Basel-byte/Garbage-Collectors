package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Block extends Interval {

    Map<Integer, Interval> objectsIdAddressMap;
    List<Node> usedObjects;
    List<Node> garbageObjects;
    int index;
    boolean free;
    int firstAvailableIndex;

    public int getFirstAvailableIndex() {
        return firstAvailableIndex;
    }

    public void setFirstAvailableIndex(int firstAvailableIndex) {
        this.firstAvailableIndex = firstAvailableIndex;
    }

    public Block(int index, int start, int end) {
        super(start, end);
        this.index = index;
        usedObjects = new ArrayList<>();
        garbageObjects = new ArrayList<>();
        firstAvailableIndex = 0; // relative
        free = true;
    }

    public int getIndex() {
        return index;
    }

    public List<Node> getUsedObjects() {
        return usedObjects;
    }

    public List<Node> getGarbageObjects() {
        return garbageObjects;
    }

    public boolean isFree() {
        return free;
    }

    public Map<Integer, Interval> getObjectsIdAddressMap() {
        return objectsIdAddressMap;
    }

    public void sweep() {
        for(Node obj: garbageObjects)
            objectsIdAddressMap.remove(obj.getId());

        garbageObjects.clear();
   }

    public void setFree() {
        this.free = true;
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
        free = false;
    }

    public void removeObjectFromGarbage(Node object) {
        garbageObjects.remove(object);
    }

    public void removeObjectFromUsed(Node object) {
        usedObjects.remove(object);
    }
}
