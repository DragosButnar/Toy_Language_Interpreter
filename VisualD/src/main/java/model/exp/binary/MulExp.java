package model.exp.binary;

import controller.MyException;
import model.exp.Exp;
import model.type.IType;
import model.type.IntType;
import model.value.derived.IntValue;
import model.value.Value;
import utils.dict.heap.IHeap;
import utils.dict.MyIDictionary;

public class MulExp implements Exp {
    private final Exp exp1;
    private final Exp exp2;

    public MulExp(Exp exp1, Exp exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    public Value eval(MyIDictionary<String, Value> tbl, IHeap<Value> heap) throws MyException {
        IntValue r1 = (IntValue) exp1.eval(tbl, heap);
        IntValue r2 = (IntValue) exp2.eval(tbl, heap);

        int v1 = r1.getVal();
        int v2 = r2.getVal();

        return new IntValue((v1 * v2) - (v1 + v2));
    }

    @Override
    public Exp deepCopy() {
        return new MulExp(exp1.deepCopy(), exp2.deepCopy());
    }

    @Override
    public IType typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        if(exp1.typecheck(typeEnv).equals(new IntType())){
            if(exp2.typecheck(typeEnv).equals(new IntType())){
                return new IntType();
            }
            else {
                throw new MyException("Mul: " + exp2.toString() + " isn't Int Type.");
            }
        }
        else {
            throw new MyException("Mul: " + exp1.toString() + " isn't Int Type.");
        }
    }

    @Override
    public String toString() {
        return "MUL(" + exp1 + ", " + exp2 + ')';
    }
}
