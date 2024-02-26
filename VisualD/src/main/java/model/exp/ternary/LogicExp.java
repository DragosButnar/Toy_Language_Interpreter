package model.exp.ternary;

import controller.MyException;
import model.exp.Exp;
import model.type.BoolType;
import model.type.IType;
import model.value.derived.BoolValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class LogicExp implements Exp {
    char op;
    Exp ex1;
    Exp ex2;

    public LogicExp(char op, Exp ex1, Exp ex2) {
        this.op = op;
        this.ex1 = ex1;
        this.ex2 = ex2;
    }

    @Override
    public Value eval(MyIDictionary<String,Value> tbl, IHeap<Value> heap) throws MyException
    {
        Value v1,v2;
        v1= ex1.eval(tbl, heap);
        if (v1 instanceof BoolValue)
        {
            v2 = ex2.eval(tbl, heap);
            if (v2 instanceof BoolValue)
            {
                BoolValue b1 = (BoolValue)v1;
                BoolValue b2 = (BoolValue)v2;
                boolean e1,e2;
                e1 = b1.getVal();
                e2 = b2.getVal();
                switch(op)
                {
                    case '&': return new BoolValue(e1 && e2);
                    case '|': return new BoolValue(e1 || e2);
                }
            }
            else
            {
                throw new MyException("second operand is not an boolean");
            }
        }
        throw new MyException("first operand is not an boolean");
    }

    @Override
    public Exp deepCopy() {
        return new LogicExp(op, ex1.deepCopy(), ex2.deepCopy());
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType type1, type2;
        type1 = ex1.typecheck(typeEnv);
        type2 = ex2.typecheck(typeEnv);
        if(type1.equals(new BoolType())){
            if(type2.equals(new BoolType())){
                return new BoolType();
            }
            else{
                throw new MyException("Second expression is not a bool");
            }
        }
        else{
            throw new MyException("First expression is not a bool");
        }
    }

    @Override
    public String toString() {
        return "" + ex1 + " " + op + " " + ex2;
    }
}
