package model.type;


import model.value.derived.IntValue;
import model.value.Value;

public class IntType implements IType {
    public IntType() {
    }


    @Override
    public String toString() {
        return "int";
    }

    @Override
    public IType deepCopy() {
        return new IntType();
    }

    @Override
    public Value defaultValue() {
        return new IntValue(0);
    }
    @Override
    public boolean equals(Object another) {
        return another instanceof IntType;
    }
}
