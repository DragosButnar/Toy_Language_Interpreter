package model.stmt.variableStmt;

import controller.MyException;
import model.exp.Exp;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class AssignStmt implements IStmt {
    private final String id;
    private final Exp exp;

    public AssignStmt(java.lang.String id, Exp exp) {
        this.id = id;
        this.exp = exp;
    }

    @Override
    public java.lang.String toString() {
        return id + " = " + exp.toString();
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        IHeap<Value> heap = state.getHeap();

        if (symTbl.isDefined(id)) {
            Value val = exp.eval(symTbl, heap);
            IType typId = (symTbl.lookup(id)).getType();
            if (val.getType().equals(typId)) {
                symTbl.update(id, val);
            } else
                throw new MyException("declared type of variable " + id + " and type of the assigned expression do not match");
        } else throw new MyException("the used variable " + id + " was not declared before");

        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typeVar = typeEnv.lookup(id);
        IType typExp = exp.typecheck(typeEnv);
        if (typeVar.equals(typExp))
            return typeEnv;
        else
            throw new MyException("Assignment: LHS and RHS have different types");
    }

    @Override
    public IStmt deepCopy()
    {
        return new AssignStmt(id, exp.deepCopy());
    }
}

