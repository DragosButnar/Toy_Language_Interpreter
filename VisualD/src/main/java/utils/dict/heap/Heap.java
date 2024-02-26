package utils.dict.heap;

import controller.MyException;

import java.util.HashMap;
import java.util.Map;

public class Heap<V> implements IHeap<V> {
    private static int nextAddress=0;
    private Map<Integer, V> map;

    public Heap() {
        this.map = new HashMap<Integer, V>();
    }

    private int nextFreeAddress(){
        return ++nextAddress;
    }

    @Override
    public int getAddress() {
        return nextAddress;
    }

    @Override
    public int put(V v) throws MyException {
        map.put(nextFreeAddress(), v);
        return nextAddress;
    }

    @Override
    public V getValue(int address) throws MyException{
        if(!(map.containsKey(address)))
            throw new MyException("Heap address is invalid");
        return map.get(address);
    }

    @Override
    public boolean isDefined(int address) {
        return map.containsKey(address);
    }

    @Override
    public void update(int address, V value) {
        map.put(address, value);
    }

    @Override
    public void setContent(Map<Integer, V> map){
        this.map=map;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public Map<Integer,V> getContent(){
        return map;
    }
}
