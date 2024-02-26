package model.stmt.variableStmt;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.stmt.controlStmts.IfStmt;
import model.type.BoolType;
import model.type.IType;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class CondAssStmt implements IStmt {
    private final String v;
    private final Exp cond;
    private final Exp thenS;
    private final Exp elseS;

    public CondAssStmt(String v, Exp cond, Exp thenS, Exp elseS) {
        this.v = v;
        this.cond = cond;
        this.thenS = thenS;
        this.elseS = elseS;
    }

    @Override
    public IStmt deepCopy() {
        return new CondAssStmt(v, cond.deepCopy(), thenS.deepCopy(), elseS.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stack = state.getExeStack();
        IStmt stmt = new IfStmt(
                cond,
                new AssignStmt(
                        v,
                        thenS
                ),
                new AssignStmt(
                        v,
                        elseS
                )
        );
        stack.push(stmt);
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType t = typeEnv.lookup("v");
        if(cond.typecheck(typeEnv).equals(new BoolType())){
            if(thenS.typecheck(typeEnv).equals(t) && elseS.typecheck(typeEnv).equals(t))
                return typeEnv;
            else
                throw new MyException("Conditional Assignment: Both " + thenS + " and " + elseS + " must be the same type as " + v + ".");
        }
        throw new MyException("Conditional Assignment: " + cond + " must be BoolType.");
    }

    @Override
    public String toString() {
        return  v + '=' + cond + "?" + thenS + ":" + elseS;
    }
}
