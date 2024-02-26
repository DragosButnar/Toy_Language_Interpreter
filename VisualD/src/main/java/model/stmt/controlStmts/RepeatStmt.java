package model.stmt.controlStmts;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.exp.unary.NotExp;
import model.stmt.multipleStmts.CompStmt;
import model.stmt.IStmt;
import model.type.BoolType;
import model.type.IType;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class RepeatStmt implements IStmt {
    final private Exp condition;
    final private IStmt instruction;

    public RepeatStmt(Exp condition, IStmt instruction) {
        this.condition = condition;
        this.instruction = instruction;
    }

    @Override
    public IStmt deepCopy() {
        return new RepeatStmt(condition.deepCopy(), instruction.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stack = state.getExeStack();
        IHeap<Value> heap = state.getHeap();
        MyIDictionary<String, Value> symTbl = state.getSymTable();

        IStmt stmt = new CompStmt(
                instruction,
                new WhileStmt(
                    new NotExp(condition),
                    instruction
                )
        );
        stack.push(stmt);
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typeCond = condition.typecheck(typeEnv);
        if(typeCond.equals(new BoolType())){
            instruction.typecheck(typeEnv.clone());
            return typeEnv;
        }
        else
            throw new MyException("The Repeat...Until condition must be BoolType");
    }

    @Override
    public String toString() {
        return "[repeat(" +
                instruction + ")" +
                "until(" + condition +
                ")]";
    }
}
