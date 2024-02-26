package model.stmt.controlStmts;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.BoolType;
import model.type.IType;
import model.value.derived.BoolValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class WhileStmt implements IStmt {
    final private Exp condition;
    final private IStmt instruction;

    public WhileStmt(Exp condition, IStmt instruction) {
        this.condition = condition;
        this.instruction = instruction;
    }

    @Override
    public IStmt deepCopy() {
        return new WhileStmt(condition.deepCopy(), instruction.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stack = state.getExeStack();
        IHeap<Value> heap = state.getHeap();
        MyIDictionary<String, Value> symTbl = state.getSymTable();

        Value val = condition.eval(symTbl, heap);
        if(!(val instanceof BoolValue bv))
            throw new MyException("Condition must return BoolValue");

        if(bv.getVal()){
            stack.push(this.deepCopy());
            stack.push(instruction.deepCopy());
        }

        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typeCond = condition.typecheck(typeEnv);
        if(typeCond.equals(new BoolType())){
            instruction.typecheck(typeEnv.clone());
            return typeEnv;
        }
        else
            throw new MyException("The WHILE condition must be BoolType");
    }

    @Override
    public String toString() {
        return "[while(" +
                condition + ")" +
                "do(" + instruction +
                ")]";
    }
}
