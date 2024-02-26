package utils.dict;

import controller.MyException;

import java.util.Map;

public interface MyIDictionary<Key, Value> {
    Value lookup(Key key);
    boolean isDefined(Key key);
    void put(Key key, Value value);
    void update(Key key, Value value);

    MyIDictionary<Key, Value> clone();
    void remove(Key key) throws MyException;
    Map<Key, Value> getContent();
}
