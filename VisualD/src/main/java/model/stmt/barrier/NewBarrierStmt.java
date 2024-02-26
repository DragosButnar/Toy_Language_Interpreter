package model.stmt.barrier;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.derived.IntValue;
import model.value.Value;
import utils.Pair;
import utils.dict.barrier.IBarrierTable;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

import java.util.ArrayList;
import java.util.List;

public class NewBarrierStmt implements IStmt {
    private final String v;
    private final Exp exp;

    public NewBarrierStmt(String v, Exp exp) {
        this.v = v;
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new NewBarrierStmt(v, exp.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIDictionary<String, Value> symTbl = state.getSymTable();
        IHeap<Value> heap = state.getHeap();
        IBarrierTable<Pair<Integer, List<Integer>>> barriers = state.getBarrierTable();

        Value number = exp.eval(symTbl, heap);
        int val = ((IntValue)number).getVal();
        barriers.put(new Pair<>(val, new ArrayList<>()));

        if(symTbl.isDefined(v)){
            symTbl.update(v, new IntValue(barriers.getAddress()));
        }
        else{
            symTbl.put(v, new IntValue(barriers.getAddress()));
        }

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType())) {
            if(exp.typecheck(typeEnv).equals(new IntType()))
                return typeEnv;
            else
                throw new MyException("newBarrier: " + exp.toString() + " must be Int Type.");
        }
        throw new MyException("newBarrier: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "newBarrier(" + v  + ", " + exp + ')';
    }
}
