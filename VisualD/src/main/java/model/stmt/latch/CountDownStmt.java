package model.stmt.latch;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.Value;
import model.value.derived.IntValue;
import utils.dict.MyIDictionary;
import utils.dict.latch.ILatchTable;
import utils.list.MyIList;
import utils.stack.MyIStack;

public class CountDownStmt implements IStmt {
    private final String v;

    public CountDownStmt(String v) {
        this.v = v;
    }

    @Override
    public IStmt deepCopy() {
        return new CountDownStmt(v);
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIDictionary<String, Value> symTbl = state.getSymTable();
        ILatchTable<Integer> latches = state.getLatchTable();
        MyIList<Value> out = state.getOut();

        if(!symTbl.isDefined(v)) {
            PrgState.lock.unlock();
            throw new MyException("countDown: " + v + " is undefined.");
        }

        Value val = symTbl.lookup(v);
        int foundIndex = ((IntValue) val).getVal();

        if(!latches.isDefined(foundIndex)){
            PrgState.lock.unlock();
            throw new MyException("countDown: " + v + " must be associated to a latch.");
        }

        int latch = latches.getValue(foundIndex);

        if(latch > 0){
            latches.update(foundIndex, latch - 1);
        }

        out.add(new IntValue(state.getId()));

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType()))
            return typeEnv;
        throw new MyException("countDown: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "countDown(" + v + ")";
    }
}
