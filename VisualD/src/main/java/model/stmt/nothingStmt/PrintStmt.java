package model.stmt.nothingStmt;

import controller.MyException;
import model.exp.Exp;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.value.Value;
import utils.dict.MyIDictionary;
import utils.list.MyIList;

public class PrintStmt implements IStmt {
    Exp exp;

    public PrintStmt(Exp exp) {
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new PrintStmt(exp.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIList<Value> out = state.getOut();
        out.add(exp.eval(state.getSymTable(), state.getHeap()));
        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        exp.typecheck(typeEnv);
        return typeEnv;
    }

    @Override
    public String toString() {
        return "print(" + exp.toString() + ")";
    }
}
