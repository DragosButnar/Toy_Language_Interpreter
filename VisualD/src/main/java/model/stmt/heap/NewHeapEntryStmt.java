package model.stmt.heap;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.IType;
import model.type.RefType;
import model.value.derived.RefValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class NewHeapEntryStmt implements IStmt {
    final private String name;
    final private Exp exp;

    public NewHeapEntryStmt(String name, Exp exp) {
        this.name = name;
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new NewHeapEntryStmt(name, exp.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> dict = state.getSymTable();
        IHeap<Value> heap = state.getHeap();
        if(!dict.isDefined(name))
            throw new MyException("Variable not defined");

        Value variable = dict.lookup(name);
        if(!(variable instanceof RefValue))
            throw new MyException("Address does not correspond to RefValue");

        Value val = exp.eval(dict, heap);

        heap.put(val);
        ((RefValue)dict.lookup(name)).setLocationType(val.getType());
        ((RefValue)dict.lookup(name)).setAddress(heap.getAddress());

        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typeVar = typeEnv.lookup(name);
        IType typExp = exp.typecheck(typeEnv);
        if (typeVar.equals(new RefType(typExp)))
            return typeEnv;
        else
            throw new MyException(name + " and " + exp + " of new() must have the same type");
    }

    @Override
    public String toString() {
        return "new(" +
                name +
                "," +
                exp +
                ')';
    }
}
