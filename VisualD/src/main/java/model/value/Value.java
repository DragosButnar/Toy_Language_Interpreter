package model.value;


import model.type.IType;

import java.util.Objects;

public interface Value {
    public IType getType();
    public Value deepCopy();
    public boolean equals(Object another);
}
