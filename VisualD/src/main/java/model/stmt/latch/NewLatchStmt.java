package model.stmt.latch;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.Value;
import model.value.derived.IntValue;
import utils.dict.MyIDictionary;
import utils.dict.heap.IHeap;
import utils.dict.latch.ILatchTable;

public class NewLatchStmt implements IStmt {
    private final String v;
    private final Exp exp;

    public NewLatchStmt(String v, Exp exp) {
        this.v = v;
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new NewLatchStmt(v, exp.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        IHeap<Value> heap = state.getHeap();
        ILatchTable<Integer> latches = state.getLatchTable();

        Value val = exp.eval(symTbl, heap);
        int num1 = ((IntValue) val).getVal();

        latches.put(num1);

        if(!symTbl.isDefined(v)){
            PrgState.lock.unlock();
            throw new MyException("newLatch: " + v + " is undefined.");
        }
        symTbl.update(v, new IntValue(latches.getAddress()));

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType())) {
            if(exp.typecheck(typeEnv).equals(new IntType()))
                return typeEnv;
            else
                throw new MyException("newLatch: " + exp.toString() + " must be Int Type.");
        }
        throw new MyException("newLatch: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "newLatch(" + v + ", " + exp + ")";
    }
}
