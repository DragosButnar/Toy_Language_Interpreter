package model.type;

import model.value.Value;

public interface IType {
    public IType deepCopy();
    public Value defaultValue();
    public boolean equals(Object another);
}
