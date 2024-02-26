package model.stmt.variableStmt;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.value.Value;
import utils.dict.MyIDictionary;

public class VarDeclStmt implements IStmt {
    private String symbolName;
    private IType type;
    public VarDeclStmt(java.lang.String symbolName, IType type) {
        this.symbolName = symbolName;
        this.type = type;
    }

    @Override
    public IStmt deepCopy() {
        return new VarDeclStmt(symbolName, type.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) {
        MyIDictionary<java.lang.String, Value> dict = state.getSymTable();
        dict.put(symbolName, type.defaultValue());
        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        typeEnv.put(symbolName, type);
        return typeEnv;
    }

    @Override
    public String toString() {
        return type.toString() + " " +
                symbolName;
    }
}
