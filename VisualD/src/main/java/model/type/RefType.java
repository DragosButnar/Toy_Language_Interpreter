package model.type;

import model.value.derived.RefValue;
import model.value.Value;

public class RefType implements IType {
    private final IType inner;

    public RefType(IType inner) {
        this.inner = inner;
    }

    @Override
    public IType deepCopy() {
        return new RefType(inner);
    }

    public IType getInner() {
        return inner;
    }

    @Override
    public Value defaultValue() {
        return new RefValue(0, inner);
    }

    @Override
    public boolean equals(Object another){
        if (another instanceof RefType)
            return inner.equals((((RefType) another).getInner()));
        else
            return false;
    }

    @Override
    public String toString() {
        return "Ref(" +inner.toString()+")";
    }
}
