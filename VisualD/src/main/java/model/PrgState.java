package model;

import controller.MyException;
import model.stmt.IStmt;
import model.value.derived.StringValue;
import model.value.Value;
import utils.Pair;
import utils.dict.*;
import utils.dict.barrier.IBarrierTable;
import utils.dict.countSemaphore.ICSemTable;
import utils.dict.heap.Heap;
import utils.dict.heap.IHeap;
import utils.dict.latch.ILatchTable;
import utils.dict.lock.ILockTable;
import utils.dict.proc.IProcTable;
import utils.list.MyIList;
import utils.stack.MyIStack;

import java.io.BufferedReader;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrgState {
    public static Lock lock = new ReentrantLock();
    MyIStack<IStmt> exeStack;
    //MyIDictionary<String, Value> symTable;
    Stack<MyIDictionary<String, Value>> symTableStack;
    MyIList<Value> out;
    MyIDictionary<StringValue, BufferedReader> files;
    IHeap<Value> heap;
    IProcTable<String, Pair<List<String>, IStmt>> procTable;
    ILockTable<Integer> lockTable;
    IBarrierTable<Pair<Integer, List<Integer>>> barrierTable;
    ILatchTable<Integer> latchTable ;

    ICSemTable<Pair<Integer, List<Integer>>> csemTable;
    IStmt originalProgram;
    int id;
    static AtomicInteger lastID = new AtomicInteger(0);

    public ICSemTable<Pair<Integer, List<Integer>>> getCsemTable() {
        return csemTable;
    }

    public void setCsemTable(ICSemTable<Pair<Integer, List<Integer>>> csemTable) {
        this.csemTable = csemTable;
    }

    public ILatchTable<Integer> getLatchTable() {
        return latchTable;
    }

    public void setLatchTable(ILatchTable<Integer> latchTable) {
        this.latchTable = latchTable;
    }

    public IProcTable<String, Pair<List<String>, IStmt>> getProcTable() {
        return procTable;
    }

    public void setProcTable(IProcTable<String, Pair<List<String>, IStmt>> procTable) {
        this.procTable = procTable;
    }

    public Stack<MyIDictionary<String, Value>> getSymTableStack() {
        return symTableStack;
    }

    public void setSymTableStack(Stack<MyIDictionary<String, Value>> symTableStack) {
        this.symTableStack = symTableStack;
    }

    public MyIDictionary<String, Value> getSymTable(){
        return symTableStack.peek();
    }

    public void pushSymTable(MyIDictionary<String, Value> symTable){
        symTableStack.push(symTable);
    }

    public MyIDictionary<String, Value> popSymTable(){
        return symTableStack.pop();
    }

    public int getId() {
        return id;
    }

    public IBarrierTable<Pair<Integer, List<Integer>>> getBarrierTable() {
        return barrierTable;
    }

    public void setBarrierTable(IBarrierTable<Pair<Integer, List<Integer>>> barrierTable) {
        this.barrierTable = barrierTable;
    }

    public ILockTable<Integer> getLockTable() {
        return lockTable;
    }

    public void setLockTable(ILockTable<Integer> lockTable) {
        this.lockTable = lockTable;
    }

    static int getNewPrgStateID(){
        return lastID.incrementAndGet();
    }

    public MyIStack<IStmt> getExeStack() {
        return exeStack;
    }
    public void setExeStack(MyIStack<IStmt> exeStack) {
        this.exeStack = exeStack;
    }

    public MyIList<Value> getOut() {
        return out;
    }

    public void setOut(MyIList<Value> out) {
        this.out = out;
    }

    public MyIDictionary<StringValue, BufferedReader> getFiles() {
        return files;
    }

    public void setFiles(MyIDictionary<StringValue, BufferedReader> files) {
        this.files = files;
    }

    public IHeap<Value> getHeap() {
        return heap;
    }

    public void setHeap(Heap<Value> heap) {
        this.heap = heap;
    }

    public Boolean isNotCompleted(){ return !this.exeStack.isEmpty(); }

    public PrgState oneStep() throws MyException
    {
        if(exeStack.isEmpty())
            throw new MyException("PrgState stack is empty");
        IStmt crtStmt = exeStack.pop();
        return crtStmt.execute(this);
    }

    public PrgState(MyIStack<IStmt> exeStack, Stack<MyIDictionary<String, Value>> symStack, MyIList<Value> out,
                    MyIDictionary<StringValue, BufferedReader> files, IHeap<Value> heap, IProcTable<String, Pair<List<String>, IStmt>> procTable,
                    ILockTable<Integer> lockTable, IBarrierTable<Pair<Integer, List<Integer>>> barriers, ILatchTable<Integer> latches,
                    ICSemTable<Pair<Integer, List<Integer>>> semTable, IStmt originalProgram) {
        this.id = getNewPrgStateID();
        this.exeStack = exeStack;

        if(symStack.isEmpty())
        {
            MyIDictionary<String, Value> dict = new MyDictionary<>();
            symStack.push(dict);
        }
        this.symTableStack = symStack;
        this.out = out;
        this.files = files;
        this.heap = heap;
        this.procTable = procTable;
        this.lockTable = lockTable;
        this.barrierTable = barriers;
        this.latchTable = latches;
        this.csemTable = semTable;
        exeStack.push(originalProgram);
    }

    @Override
    public String toString() {
        return "Program " + id + "\n{" +
                "\n\tStack: " + exeStack +
                "\n\tSymbol Table: " + this.getSymTable() +
                "\n\tOutput: " + out +
                "\n\tFile table: " + files +
                "\n\tHeap: " + heap +
                "\n\tProcedures: " + procTable +
                "\n\tLock table: " + lockTable +
                "\n\tBarrier table: " + barrierTable +
                "\n\tLatch table: " + latchTable +
                "\n\tCount Semaphore table: " + csemTable +
                "\n};\n";
    }
}
