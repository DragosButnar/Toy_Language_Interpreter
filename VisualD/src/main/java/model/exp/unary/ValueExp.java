package model.exp.unary;

import controller.MyException;
import model.exp.Exp;
import model.type.IType;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class ValueExp implements Exp {
    Value v;

    public ValueExp(Value v) {
        this.v = v;
    }

    @Override
    public Value eval(MyIDictionary<String,Value> tbl, IHeap<Value> heap) throws MyException {
        return v;
    }

    @Override
    public Exp deepCopy() {
        return new ValueExp(v.deepCopy());
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        return v.getType();
    }

    @Override
    public String toString() {
        return v.toString();
    }
}
