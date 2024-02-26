package model.exp.unary;

import controller.MyException;
import model.exp.Exp;
import model.type.IType;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class VarExp implements Exp {
    String id;

    public VarExp(String id) {
        this.id = id;
    }

    @Override
    public Value eval(MyIDictionary<String,Value> tbl, IHeap<Value> heap) throws MyException
    {
        return tbl.lookup(id);
    }

    @Override
    public Exp deepCopy() {
        return new VarExp(id);
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        return typeEnv.lookup(id).deepCopy();
    }

    @Override
    public String toString() {
        return id;
    }
}
