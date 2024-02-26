package model.value.derived;

import model.type.BoolType;
import model.type.IType;
import model.value.Value;

public class BoolValue implements Value {
    boolean val;

    public BoolValue(boolean val) {
        this.val = val;
    }

    public boolean getVal() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }


    @Override
    public IType getType() {
        return new BoolType();
    }

    @Override
    public Value deepCopy() {
        return new BoolValue(val);
    }

    @Override
    public boolean equals(Object another) {
        if(another instanceof BoolValue)
            return this.val == ((BoolValue) another).val;
        return false;
    }
}
