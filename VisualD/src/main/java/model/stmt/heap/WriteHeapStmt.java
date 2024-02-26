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

public class WriteHeapStmt implements IStmt {
    final private String name;
    final private Exp exp;

    public WriteHeapStmt(String variable, Exp exp) {
        this.name = variable;
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new WriteHeapStmt(name, exp.deepCopy());
    }

    @Override
    public String toString() {
        return "wH(" + name + ", " + exp + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> tbl = state.getSymTable();
        IHeap<Value> heap = state.getHeap();

        if(!(tbl.isDefined(name)))
            throw new MyException("Variable is not defined");

        Value var = tbl.lookup(name);

        if(!(var instanceof RefValue rv))
            throw new MyException("Variable is not RefType");

        int address = rv.getAddress();

        if(!(heap.isDefined(address)))
            throw new MyException("Address is not defined");

        Value val = exp.eval(tbl, heap);

        heap.update(address, val);

        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typeVar = typeEnv.lookup(name);
        IType typExp = exp.typecheck(typeEnv);
        if (typeVar.equals(new RefType(typExp)))
            return typeEnv;
        else
            throw new MyException("wH: LHS and RHS have different types");
    }
}
