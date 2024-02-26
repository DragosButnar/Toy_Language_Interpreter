package model.value.derived;

import model.type.IType;
import model.type.RefType;
import model.value.Value;

public class RefValue implements Value {

    private int address;
    IType locationType;

    public RefValue(int address, IType locationType) {
        this.address = address;
        this.locationType = locationType.deepCopy();
    }

    public int getAddress() {
        return address;
    }

    @Override
    public IType getType() {
        return new RefType(locationType);
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setLocationType(IType locationType) {
        this.locationType = locationType;
    }

    @Override
    public Value deepCopy() {
        return new RefValue(address, locationType.deepCopy());
    }

    @Override
    public boolean equals(Object another) {
        if(another instanceof RefValue)
            return this.address == ((RefValue) another).address &&
                    this.locationType.equals(((RefValue) another).locationType);
        return false;
    }

    @Override
    public String toString() {
        return "RefValue(" +
                address +
                ", " +
                locationType +
                ')';
    }
}
