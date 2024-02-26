package model.stmt.controlStmts;

import controller.MyException;
import model.exp.Exp;
import model.PrgState;
import model.stmt.IStmt;
import model.type.BoolType;
import model.type.IType;
import model.type.IntType;
import model.value.derived.BoolValue;
import model.value.derived.IntValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class IfStmt implements IStmt {
    Exp exp;
    IStmt thenS;
    IStmt elseS;

    public IfStmt(Exp e, IStmt t, IStmt el) {
        exp=e;
        thenS=t;
        elseS=el;
    }
    @Override
    public String toString(){
        return "[if(" + exp.toString()+") " +
                "then (" + thenS.toString() +
                ") else (" + elseS.toString()+ ")" +
        "]";
    }

    @Override
    public IStmt deepCopy() {
        return new IfStmt(exp.deepCopy(), thenS.deepCopy(), elseS.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException{
        MyIStack<IStmt> stk = state.getExeStack();
        IHeap<Value> heap = state.getHeap();
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        if(exp.eval(symTbl, heap).equals(new BoolValue(true)) ||
                (exp.eval(symTbl, heap).getType().equals(new IntType()) && ((IntValue) exp.eval(symTbl, heap)).getVal() > 0)){
            stk.push(thenS);
        }
        else{
            stk.push(elseS);
        }

        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typExp=exp.typecheck(typeEnv);
        if (typExp.equals(new BoolType()) || typExp.equals(new IntType())) {
            thenS.typecheck(typeEnv.clone());
            elseS.typecheck(typeEnv.clone());
            return typeEnv;
        }
        else
            throw new MyException("The IF condition must be BoolType or IntType");
    }
}

