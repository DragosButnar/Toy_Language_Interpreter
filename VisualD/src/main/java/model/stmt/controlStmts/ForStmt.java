package model.stmt.controlStmts;

import controller.MyException;
import model.PrgState;
import model.exp.*;
import model.exp.ternary.RelExp;
import model.exp.unary.VarExp;
import model.stmt.variableStmt.AssignStmt;
import model.stmt.multipleStmts.CompStmt;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.Value;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class ForStmt implements IStmt {
    private final String v;
    private final Exp init;
    private final Exp cond;
    private final Exp incr;
    private final IStmt instr;

    public ForStmt(String v, Exp init, Exp cond, Exp incr, IStmt instr) {
        this.v = v;
        this.init = init;
        this.cond = cond;
        this.incr = incr;
        this.instr = instr;
    }

    @Override
    public IStmt deepCopy() {
        return new ForStmt(v, init.deepCopy(), cond.deepCopy(), incr.deepCopy(), instr.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stack = state.getExeStack();
        MyIDictionary<String, Value> symTbl = state.getSymTable();


        if(!symTbl.isDefined(v))
            throw new MyException("For: Variable is undefined.");

        IStmt forStmt = new CompStmt(
                new AssignStmt(v, init),
                new WhileStmt(
                        new RelExp("<", new VarExp(v), cond),
                        new CompStmt(
                                instr,
                                new AssignStmt(v, incr)
                        )
                )
        );
        stack.push(forStmt);

        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType vType = typeEnv.lookup(v);
        if (vType.equals(new IntType())) {
            IType initType = init.typecheck(typeEnv);
            IType condType = cond.typecheck(typeEnv);
            IType instType = incr.typecheck(typeEnv);
            if(initType.equals(new IntType()) && condType.equals(new IntType()) && instType.equals(new IntType())) {
                instr.typecheck(typeEnv.clone());
                return typeEnv;
            }
            else {
                throw new MyException("For: Initial value and condition value must be int type.");
            }
        }
        else{
            throw new MyException("For: For variable must be int type.");
        }
    }

    @Override
    public String toString() {
        return "[for(" +
            v + " = " + init.toString() + ";" +
            v + " < " + cond.toString() + ";" +
            v + " = " + incr.toString() + ")" +
                " " + instr.toString() +
            "]";
    }
}
