package model.stmt.multipleStmts;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.value.derived.StringValue;
import model.value.Value;
import utils.Pair;
import utils.dict.*;
import utils.dict.barrier.IBarrierTable;
import utils.dict.countSemaphore.ICSemTable;
import utils.dict.heap.IHeap;
import utils.dict.latch.ILatchTable;
import utils.dict.lock.ILockTable;
import utils.dict.proc.IProcTable;
import utils.list.MyIList;
import utils.stack.MyIStack;
import utils.stack.MyStack;

import java.io.BufferedReader;
import java.util.List;
import java.util.Stack;

public class ForkStmt implements IStmt {
    private final IStmt initialStmt;

    public ForkStmt(IStmt initialPrg) {
        this.initialStmt = initialPrg;
    }

    @Override
    public IStmt deepCopy() {
        return new ForkStmt(initialStmt.deepCopy());
    }

    @Override
    public PrgState execute(PrgState parent) throws MyException {
        MyIStack<IStmt> childStack = new MyStack<>();

        Stack<MyIDictionary<String, Value>> parentSymTable = parent.getSymTableStack();
        Stack<MyIDictionary<String, Value>> childSymTable = new Stack<>();

        parentSymTable.forEach(symTable -> {childSymTable.push(symTable.clone());});

        IHeap<Value> childHeap = parent.getHeap();

        MyIDictionary<StringValue, BufferedReader> childFileTable = parent.getFiles();

        MyIList<Value> childOutput = parent.getOut();

        ILockTable<Integer> childLock = parent.getLockTable();

        IBarrierTable<Pair<Integer, List<Integer>>> childBarriers = parent.getBarrierTable();

        IProcTable<String, Pair<List<String>, IStmt>> childProcTable = parent.getProcTable();

        ILatchTable<Integer> childLatchTable = parent.getLatchTable();

        ICSemTable<Pair<Integer, List<Integer>>> childCSem = parent.getCsemTable();

        return new PrgState(childStack, childSymTable, childOutput, childFileTable, childHeap, childProcTable,
                childLock, childBarriers, childLatchTable, childCSem, initialStmt);
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        initialStmt.typecheck(typeEnv.clone());
        return typeEnv;
    }

    @Override
    public String toString() {
        return "fork(" + initialStmt + ')';
    }
}
