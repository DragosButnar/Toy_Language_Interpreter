package model.stmt.multipleStmts;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class CompStmt implements IStmt {
    private final IStmt head;
    private final IStmt tail;
    public CompStmt(IStmt head, IStmt tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public IStmt deepCopy() {
        return new CompStmt(head.deepCopy(), tail.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stk = state.getExeStack();
        stk.push(tail);
        stk.push(head);
        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        // MyIDictionary<String,Type> typEnv1 = first.typecheck(typeEnv);
        // MyIDictionary<String,Type> typEnv2 = snd.typecheck(typEnv1);
        // return typEnv2;
        return tail.typecheck(head.typecheck(typeEnv));
    }

    @Override
    public String toString() {
        return "{" + head.toString() + ", " + tail.toString() + "}";
    }
}
