package model.exp.unary;

import controller.MyException;
import model.exp.Exp;
import model.type.BoolType;
import model.type.IType;
import model.value.derived.BoolValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class NotExp implements Exp {
    private final Exp v;

    public NotExp(Exp v) {
        this.v = v;
    }

    @Override
    public Value eval(MyIDictionary<String, Value> tbl, IHeap<Value> heap) throws MyException {
        boolean boolV = ((BoolValue) v.eval(tbl, heap)).getVal();
        return new BoolValue(!boolV);
    }

    @Override
    public Exp deepCopy() {
        return new NotExp(v.deepCopy());
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(v.typecheck(typeEnv).equals(new BoolType()))
            return new BoolType();
        throw new MyException("Not: " + v + " must be BoolType");
    }

    @Override
    public String toString() {
        return "not(" + v + ')';
    }
}
