package utils.dict;

import controller.MyException;

import java.util.HashMap;
import java.util.Map;

public class MyDictionary<Key, Value> implements MyIDictionary<Key, Value> {
    protected final Map<Key, Value> map;

    public MyDictionary() {
        this.map = new HashMap<>();
    }

    @Override
    public Value lookup(Key key) {
        return map.get(key);
    }

    @Override
    public boolean isDefined(Key key) {
        return map.get(key) != null;
    }

    @Override
    public void put(Key key, Value value) {
        map.put(key, value);
    }

    @Override
    public void update(Key key, Value value) {
        map.put(key, value);
    }

    @Override
    public MyIDictionary<Key, Value> clone() {
        MyDictionary<Key, Value> dict = new MyDictionary<>();
        for(Key key : map.keySet())
        {
            dict.put(key, map.get(key));
        }
        return dict;
    }

    @Override
    public void remove(Key key) throws MyException {
        if(map.remove(key) == null){
            throw new MyException("Key undefined");
        }
    }

    @Override
    public Map<Key, Value> getContent() {
        return map;
    }


    @Override
    public String toString() {
        return "" + map;
    }
}
