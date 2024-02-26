package utils.dict.lock;

import controller.MyException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LockTable<V> implements ILockTable<V> {
    private static int newFreeLocation=0;
    private Map<Integer, V> map;

    public LockTable() {
        this.map = Collections.synchronizedMap(new HashMap<Integer, V>());
    }

    private int nextFreeAddress(){
        return ++newFreeLocation;
    }

    @Override
    public int getAddress() {
        return newFreeLocation;
    }

    @Override
    public int put(V v) throws MyException {
        map.put(nextFreeAddress(), v);
        return newFreeLocation;
    }

    @Override
    public V getValue(int address) throws MyException{
        if(!(map.containsKey(address)))
            throw new MyException("Lock is invalid");
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
