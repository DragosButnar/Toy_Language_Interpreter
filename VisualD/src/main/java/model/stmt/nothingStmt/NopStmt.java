package model.stmt.nothingStmt;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import utils.dict.MyIDictionary;

public class NopStmt implements IStmt {
    @Override
    public IStmt deepCopy() {
        return new NopStmt();
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        return typeEnv;
    }
}
