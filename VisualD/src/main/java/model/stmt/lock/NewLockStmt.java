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


public class NewLockStmt implements IStmt {
    private final String v;

    public NewLockStmt(String v) {
        this.v = v;
    }

    @Override
    public IStmt deepCopy() {
        return new NewLockStmt(v);
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIDictionary<String, Value> symTbl = state.getSymTable();
        ILockTable<Integer> lockTable = state.getLockTable();
        lockTable.put(-1);

        if(symTbl.isDefined(v)){
            symTbl.update(v, new IntValue(lockTable.getAddress()));
        }
        else{
            symTbl.put(v, new IntValue(lockTable.getAddress()));
        }

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType()))
            return typeEnv;
        throw new MyException("newLock: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "newLock(" + v + ')';
    }
}
