package model.stmt.files;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.IType;
import model.type.StringType;
import model.value.derived.StringValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

import java.io.*;

public class OpenRFileStmt implements IStmt {
    private final Exp exp;

    public OpenRFileStmt(Exp exp) {
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new OpenRFileStmt(exp.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> dict = state.getSymTable();
        IHeap<Value> heap = state.getHeap();
        MyIDictionary<StringValue, BufferedReader> files = state.getFiles();
        Value val = exp.eval(dict, heap);
        if (!(val instanceof StringValue strVal))
            throw new MyException("File type must be a string");

        if (files.isDefined(strVal))
            throw new MyException("File is already open");

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(strVal.getVal()));
        } catch (FileNotFoundException e) {
            throw new MyException(e.toString());
        }
        files.put(strVal, reader);
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType type = exp.typecheck(typeEnv);
        if(type.equals(new StringType()))
            return typeEnv;
        else
            throw new MyException("open() statement's argument must be StringType");
    }

    @Override
    public String toString() {
        return "open(" + exp.toString() + ")";
    }
}
