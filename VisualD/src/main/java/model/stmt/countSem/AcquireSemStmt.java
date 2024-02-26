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
import utils.list.MyIList;
import utils.stack.MyIStack;

import java.util.List;

public class AcquireSemStmt implements IStmt {
    private final String v;

    public AcquireSemStmt(String v) {
        this.v = v;
    }


    @Override
    public IStmt deepCopy() {
        return new AcquireSemStmt(v);
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIDictionary<String, Value> symTbl = state.getSymTable();
        ICSemTable<Pair<Integer, List<Integer>>> semaphores = state.getCsemTable();
        MyIStack<IStmt> stack = state.getExeStack();

        if(!symTbl.isDefined(v)) {
            PrgState.lock.unlock();
            throw new MyException("acquire: " + v + " is undefined.");
        }

        Value val = symTbl.lookup(v);
        int foundIndex = ((IntValue) val).getVal();

        if(!semaphores.isDefined(foundIndex)){
            PrgState.lock.unlock();
            throw new MyException("acquire: " + v + " must be associated to a CSemaphore.");
        }

        Pair<Integer, List<Integer>> semaphore = semaphores.getValue(foundIndex);
        int nrGrants = semaphore.getA();
        List<Integer> cars = semaphore.getB();
        int nrCars = cars.size();

        if(nrGrants <= nrCars){
            stack.push(this.deepCopy());
        }
        else {
            if (!cars.contains(state.getId())) {
                cars.add(state.getId());
            }
        }
        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType()))
            return typeEnv;
        throw new MyException("acquire: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "acquire(" + v + ")";
    }
}
