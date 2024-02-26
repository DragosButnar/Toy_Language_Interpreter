package model.exp.heap;

import controller.MyException;
import model.exp.Exp;
import model.type.IType;
import model.type.RefType;
import model.value.derived.RefValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class HeapReadExp implements Exp {
    private final Exp exp;

    public HeapReadExp(Exp exp) {
        this.exp = exp;
    }

    @Override
    public Value eval(MyIDictionary<String,Value> tbl, IHeap<Value> heap) throws MyException {

        Value val = exp.eval(tbl, heap);
        if(!(val instanceof RefValue rv))
            throw new MyException("Value must be RefType");

        int address = rv.getAddress();

        return heap.getValue(address);

    }

    @Override
    public Exp deepCopy() {
        return new HeapReadExp(exp.deepCopy());
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType type = exp.typecheck(typeEnv);
        if(type instanceof RefType refType){
            return refType.getInner();
        }
        else {
            throw new MyException("The argument of rH is not a RefType");
        }
    }

    @Override
    public String toString() {
        return  "rH("+ exp.toString() + ")";
    }
}
