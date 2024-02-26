package model.stmt.countSem;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.value.Value;
import model.value.derived.IntValue;
import utils.Pair;
import utils.dict.MyIDictionary;
import utils.dict.countSemaphore.ICSemTable;
import utils.dict.heap.IHeap;

import java.util.ArrayList;
import java.util.List;

public class NewCSemStmt implements IStmt {
    private final String v;
    private final Exp exp;

    public NewCSemStmt(String v, Exp exp) {
        this.v = v;
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new NewCSemStmt(v, exp.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        PrgState.lock.lock();

        MyIDictionary<String, Value> symTbl = state.getSymTable();
        IHeap<Value> heap = state.getHeap();
        ICSemTable<Pair<Integer, List<Integer>>> semTable = state.getCsemTable();

        Value val = exp.eval(symTbl, heap);
        int number = ((IntValue) val).getVal();

        semTable.put(new Pair<>(number, new ArrayList<>()));

        if(!symTbl.isDefined(v)){
            PrgState.lock.unlock();
            throw new MyException("newCSemaphore: " + v + " is undefined.");
        }

        symTbl.update(v, new IntValue(semTable.getAddress()));

        PrgState.lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(typeEnv.lookup(v).equals(new IntType())) {
            if(exp.typecheck(typeEnv).equals(new IntType()))
                return typeEnv;
            else
                throw new MyException("newCSemaphore: " + exp + " must be Int Type.");
        }
        throw new MyException("newCSemaphore: " + v + " must be Int Type.");
    }

    @Override
    public String toString() {
        return "newCSemaphore(" + v + ", " + exp + ")";
    }
}
