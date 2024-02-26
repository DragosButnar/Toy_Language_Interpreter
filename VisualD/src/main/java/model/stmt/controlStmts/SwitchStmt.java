package model.stmt.controlStmts;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.exp.ternary.RelExp;
import model.stmt.IStmt;
import model.type.IType;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class SwitchStmt implements IStmt {
    private final IStmt s1;
    private final IStmt s2;
    private final IStmt sd;
    private final Exp cond;
    private final Exp v1;
    private final Exp v2;

    public SwitchStmt(Exp cond, Exp v1, IStmt s1, Exp v2, IStmt s2, IStmt sd) {
        this.cond = cond;
        this.v1 = v1;
        this.s1 = s1;
        this.v2 = v2;
        this.s2 = s2;
        this.sd = sd;
    }

    @Override
    public IStmt deepCopy() {
        return new SwitchStmt(cond.deepCopy(), v1.deepCopy(), s1.deepCopy(), v2.deepCopy(), s2.deepCopy(), sd.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stack = state.getExeStack();
        IStmt stmt = new IfStmt(
                        new RelExp(
                                "==",
                                cond.deepCopy(),
                                v1.deepCopy()
                        ),
                        s1.deepCopy(),
                        new IfStmt(
                                new RelExp(
                                        "==",
                                        cond.deepCopy(),
                                        v2.deepCopy()
                                ),
                                s2.deepCopy(),
                                sd.deepCopy()
                        )
        );
        stack.push(stmt);
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType t = cond.typecheck(typeEnv);
        if(!(v1.typecheck(typeEnv).equals(t) && v2.typecheck(typeEnv).equals(t))){
            throw new MyException("Switch: Both " + v1 + " and " + v2 + " must be the same type as " + cond + ".");
        }
        sd.typecheck(s2.typecheck(s1.typecheck(typeEnv)));
        return typeEnv;
    }

    @Override
    public String toString() {
        return "[switch(" +
                cond + ")" +
                " (case " + v1 + ": " + s1 + ") " +
                " (case " + v2 + ": " + s2 + ") " +
                " (default: " + sd + ")" +
                "]";
    }
}
