package model.stmt.nothingStmt;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.derived.IntValue;
import model.value.Value;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class SleepStmt implements IStmt {
    private final Value number;

    public SleepStmt(Value number) {
        this.number = number;
    }

    @Override
    public IStmt deepCopy() {
        return new SleepStmt(number.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        IntValue integer = (IntValue) number;
        int val = integer.getVal();
        if(val > 0){
            MyIStack<IStmt> stack = state.getExeStack();
            stack.push(new SleepStmt(new IntValue(val - 1)));
        }
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(number.getType().equals(new IntType())){
            return typeEnv;
        }
        throw new MyException("Sleep: Parameter must be int type");
    }

    @Override
    public String toString() {
        return "sleep(" + number + ')';
    }
}
