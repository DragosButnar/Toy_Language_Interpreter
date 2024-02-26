package model.type;

import model.value.derived.BoolValue;
import model.value.Value;

public class BoolType implements IType {
    public BoolType() {}

    @Override
    public String toString() {
        return "bool";
    }

    @Override
    public IType deepCopy() {
        return new BoolType();
    }

    @Override
    public Value defaultValue() {
        return new BoolValue(false);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof BoolType;
    }
}
