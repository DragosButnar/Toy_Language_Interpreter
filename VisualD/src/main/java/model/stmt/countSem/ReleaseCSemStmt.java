package model.stmt.countSem;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.Value;
import model.value.derived.IntValue;
import utils.Pair;
import utils.dict.MyIDictionary;
import utils.dict.countSemaphore.ICSemTable;

import java.util.List;

public class ReleaseCSemStmt implements IStmt {
    private final String v;

    public ReleaseCSemStmt(String v) {
        this.v = v;
    }


    @Override
    public IStmt deepCopy() {
        return new ReleaseCSemStmt(v);
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIDictionary<String, Value> symTbl = state.getSymTable();
        ICSemTable<Pair<Integer, List<Integer>>> semaphores = state.getCsemTable();

        if(!symTbl.isDefined(v)) {
            PrgState.lock.unlock();
            throw new MyException("release: " + v + " is undefined.");
        }

        Value val = symTbl.lookup(v);
        int foundIndex = ((IntValue) val).getVal();

        if(!semaphores.isDefined(foundIndex)){
            PrgState.lock.unlock();
            throw new MyException("release: " + v + " must be associated to a CSemaphore.");
        }

        Pair<Integer, List<Integer>> semaphore = semaphores.getValue(foundIndex);
        List<Integer> cars = semaphore.getB();

        if(cars.contains(state.getId()))
        {
            cars.remove(cars.indexOf(state.getId()));
        }

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType()))
            return typeEnv;
        throw new MyException("release: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "release(" + v + ")";
    }
}
