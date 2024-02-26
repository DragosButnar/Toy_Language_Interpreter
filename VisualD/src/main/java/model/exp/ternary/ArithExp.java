package model.exp.ternary;

import controller.MyException;
import model.exp.Exp;
import model.type.IType;
import model.type.IntType;
import model.value.derived.IntValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class ArithExp implements Exp {
    char op;
    Exp e1;
    Exp e2;
    //....


    public ArithExp(char op, Exp e1, Exp e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public Value eval(MyIDictionary<String,Value> tbl, IHeap<Value> heap) throws MyException
    {
        Value v1,v2;
        v1= e1.eval(tbl, heap);
        if (v1 instanceof IntValue)
        {
            v2 = e2.eval(tbl, heap);
            if (v2 instanceof IntValue)
            {
                IntValue i1 = (IntValue)v1;
                IntValue i2 = (IntValue)v2;
                int n1,n2;
                n1 = i1.getVal();
                n2 = i2.getVal();
                switch(op)
                {
                    case '+': return new IntValue(n1+n2);
                    case '-': return new IntValue(n1-n2);
                    case '*': return new IntValue(n1*n2);
                    case '/': if(n2 == 0) throw new MyException("division by zero"); else return new IntValue(n1/n2);
                }
            }
            else
            {
                throw new MyException("second operand is not an integer");
            }
        }
        throw new MyException("first operand is not an integer");
    }

    @Override
    public Exp deepCopy() {
        return new ArithExp(op, e1.deepCopy(), e2.deepCopy());
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        IType type1, type2;
        type1 = e1.typecheck(typeEnv);
        type2 = e2.typecheck(typeEnv);
        if(type1.equals(new IntType())){
            if(type2.equals(new IntType())){
                return new IntType();
            }
            else {
                throw new MyException("Second operand is not an integer");
            }
        }
        else{
            throw new MyException("First operand is not an integer");
        }
    }

    @Override
    public String toString() {
        return "" + e1 + " " + op + " " + e2;
    }
}
