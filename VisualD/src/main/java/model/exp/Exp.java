package model.exp;

import controller.MyException;
import model.type.IType;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public interface Exp {
    Value eval(MyIDictionary<String,Value> tbl, IHeap<Value> heap) throws MyException;
    public Exp deepCopy();
    public IType typecheck(MyIDictionary<String,IType> typeEnv) throws MyException;
}