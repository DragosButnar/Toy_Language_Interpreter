package model.value.derived;


import model.type.IType;
import model.type.IntType;
import model.value.Value;

public class IntValue implements Value {
    int val;

    public IntValue(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    @Override
    public IType getType() {
        return new IntType();
    }

    @Override
    public Value deepCopy() {
        return new IntValue(val);
    }

    @Override
    public boolean equals(Object another) {
        if(another instanceof IntValue)
            return this.val == ((IntValue) another).val;
        return false;
    }

}
