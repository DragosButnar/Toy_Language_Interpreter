package model.stmt.files;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.IType;
import model.type.IntType;
import model.type.StringType;
import model.value.derived.IntValue;
import model.value.derived.StringValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadFileStmt implements IStmt {
    final String varName;
    final Exp exp;

    public ReadFileStmt(String varName, Exp exp) {
        this.varName = varName;
        this.exp = exp;
    }

    @Override
    public IStmt deepCopy() {
        return new ReadFileStmt(varName, exp.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> dict = state.getSymTable();
        IHeap<Value> heap = state.getHeap();
        MyIDictionary<StringValue, BufferedReader> files = state.getFiles();

        if(!dict.isDefined(varName)){
            throw new MyException("Variable is not defined");
        }

        Value val = dict.lookup(varName);
        if(!(val instanceof IntValue)){
            throw new MyException("Variable is not int type");
        }

        Value expVal = exp.eval(dict, heap);
        if(!(expVal instanceof StringValue strExpVal)){
            throw new MyException("Expression must be a string");
        }

        BufferedReader reader = files.lookup(strExpVal);
        if(reader == null)
            throw new MyException("File is not open");

        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new MyException(e.toString());
        }
        int finalVal;
        if(line == null){
            finalVal = 0;
        }
        else {
            finalVal = Integer.parseInt(line);
        }

        dict.update(varName, new IntValue(finalVal));

        return null;    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType typeVar = typeEnv.lookup(varName);
        IType typExp = exp.typecheck(typeEnv);
        if (typeVar.equals(new IntType())) {
            if(typExp.equals(new StringType())){
                return typeEnv;
            }
            else{
                throw new MyException("read() statement's second parameter must be StringType");
            }
        }
        else
            throw new MyException("read() statement's first parameter must be IntType");
    }

    @Override
    public String toString() {
        return "read(" + varName + ", " + exp + ")";
    }
}
