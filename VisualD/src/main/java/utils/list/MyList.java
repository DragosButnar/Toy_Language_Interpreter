package utils.list;

import java.util.ArrayList;
import java.util.List;

public class MyList<T> implements MyIList<T> {
    private List<T> list;

    public MyList() {
        list = new ArrayList<>();
    }

    @Override
    public void add(T e) {
        list.add(e);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public List<T> getContent() {
        return list;
    }

    @Override
    public String toString(){
        return "" + list;
    }
}
