package model.stmt.barrier;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.derived.IntValue;
import model.value.Value;
import utils.Pair;
import utils.dict.barrier.IBarrierTable;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

import java.util.List;

public class BarrierAwaitStmt implements IStmt {
    private final String v;

    public BarrierAwaitStmt(String v) {
        this.v = v;
    }

    @Override
    public IStmt deepCopy() {
        return new BarrierAwaitStmt(v);
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIStack<IStmt> stack = state.getExeStack();
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        IBarrierTable<Pair<Integer, List<Integer>>> barrierTable = state.getBarrierTable();

        if(!symTbl.isDefined(v)) {
            PrgState.lock.unlock();
            throw new MyException("Await: " + v + " is undefined.");
        }

        Value aux = symTbl.lookup(v);
        IntValue index = (IntValue) aux;
        int foundIndex = index.getVal();

        if(!barrierTable.isDefined(foundIndex)){
            PrgState.lock.unlock();
            throw new MyException("Await: " + v + " must be associated to a barrier");
        }

        Pair<Integer, List<Integer>> entry = barrierTable.getValue(foundIndex);
        int n = entry.getA();
        List<Integer> l = entry.getB();

        if(n > l.size()){
            if(l.stream().anyMatch(integer -> {return integer == state.getId();})){
                stack.push(this.deepCopy());
            }
            else{
                l.add(state.getId());
                stack.push(this.deepCopy());
            }
        }

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType()))
            return typeEnv;
        throw new MyException("Await: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "await(" + v + ')';
    }
}
