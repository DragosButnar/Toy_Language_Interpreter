package model.type;

import model.value.derived.StringValue;
import model.value.Value;

public class StringType implements IType {
    public StringType() {
    }

    @Override
    public String toString() {
        return "string";
    }

    @Override
    public IType deepCopy() {
        return new StringType();
    }

    @Override
    public Value defaultValue() {
        return new StringValue("");
    }
    @Override
    public boolean equals(Object another) {
        return another instanceof StringType;
    }
}
