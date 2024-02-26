package model.stmt.nothingStmt;

import controller.MyException;
import model.PrgState;
import model.exp.unary.ValueExp;
import model.stmt.multipleStmts.CompStmt;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.derived.IntValue;
import model.value.Value;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class WaitStmt implements IStmt {
    private final Value number;

    public WaitStmt(Value number) {
        this.number = number;
    }

    @Override
    public IStmt deepCopy() {
        return new WaitStmt(number.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        IntValue integer = (IntValue) number;
        int val = integer.getVal();
        if(val > 0){
            MyIStack<IStmt> stack = state.getExeStack();
            stack.push(new CompStmt(new PrintStmt(new ValueExp(number)), new WaitStmt(new IntValue(val - 1))));
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(number.getType().equals(new IntType())){
            return typeEnv;
        }
        throw new MyException("Wait: Parameter must be int type");
    }

    @Override
    public String toString() {
        return "wait(" + number + ')';
    }
}
