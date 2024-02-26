package model.stmt;

import controller.MyException;
import model.PrgState;
import model.type.IType;
import utils.dict.MyIDictionary;

public interface IStmt {
    public IStmt deepCopy();
    PrgState execute(PrgState state) throws MyException;

    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException;
}
