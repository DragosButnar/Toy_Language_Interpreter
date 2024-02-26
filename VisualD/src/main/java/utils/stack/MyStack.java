package utils.stack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MyStack<T> implements MyIStack<T> {
    private Stack<T> stack;

    public MyStack() {
        this.stack = new Stack<T>();
    }

    @Override
    public T pop() {
        // If item is null, except in above layer
        return stack.pop();
    }

    @Override
    public void push(T v) {
        stack.push(v);
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public List<T> reverse() {
        List<T> items = Arrays.asList((T[]) stack.toArray());
        Collections.reverse(items);
        return items;
    }

    @Override
    public MyIStack<T> clone() {
        MyStack<T> st = new MyStack<>();
        for(T item : stack) {
            // TODO: Figure out if this is okie-dokie
            st.push(item);
        }
        return st;
    }

    @Override
    public String toString() {
        List<T> list = reverse();
        return "" + list;
    }
}
