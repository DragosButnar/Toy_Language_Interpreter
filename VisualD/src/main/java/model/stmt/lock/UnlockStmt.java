package model.stmt.lock;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.derived.IntValue;
import model.value.Value;
import utils.dict.lock.ILockTable;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

public class UnlockStmt implements IStmt {
    private final String v;

    public UnlockStmt(String v) {
        this.v = v;
    }

    @Override
    public IStmt deepCopy() {
        return new UnlockStmt(v);
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIStack<IStmt> stack = state.getExeStack();
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        ILockTable<Integer> lockTable = state.getLockTable();

        if(!symTbl.isDefined(v)) {
            PrgState.lock.unlock();
            throw new MyException("Unlock: " + v + " is undefined.");
        }

        Value foundIndex = symTbl.lookup(v);
        if(!foundIndex.getType().equals(new IntType())) {
            PrgState.lock.unlock();
            throw new MyException("Unlock: " + v + " must be associated to a lock");
        }
        IntValue intIndex = (IntValue) foundIndex;

        if(!lockTable.isDefined(intIndex.getVal())) {
            PrgState.lock.unlock();
            return null;
        }

        int index = intIndex.getVal();

        if(lockTable.getValue(index) != state.getId()) {
            PrgState.lock.unlock();
            return null;
        }

        lockTable.update(index, -1);

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType()))
            return typeEnv;
        throw new MyException("Unlock: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "unlock(" + v + ')';
    }
}
