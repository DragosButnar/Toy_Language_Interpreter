package model.stmt.procedures;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import utils.dict.MyIDictionary;

public class ReturnStmt implements IStmt {
    @Override
    public IStmt deepCopy() {
        return null;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        state.popSymTable();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        return typeEnv;
    }

    @Override
    public String toString() {
        return "return";
    }
}
