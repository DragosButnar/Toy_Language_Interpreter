package model.value.derived;

import model.type.IType;
import model.type.StringType;
import model.value.Value;

import java.util.Objects;

public class StringValue implements Value {
    String val;

    public StringValue(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    @Override
    public IType getType() {
        return new StringType();
    }

    @Override
    public Value deepCopy() {
        return new StringValue(val);
    }

    @Override
    public boolean equals(Object another) {
        if(another instanceof StringValue)
            return Objects.equals(this.val, ((StringValue) another).val);
        return false;
    }

    public String getVal() {
        return val;
    }


}
