package model.stmt.procedures;

import controller.MyException;
import model.PrgState;
import model.exp.Exp;
import model.stmt.IStmt;
import model.type.IType;
import model.value.Value;
import utils.Pair;
import utils.dict.heap.IHeap;
import utils.dict.proc.IProcTable;
import utils.dict.MyDictionary;
import utils.dict.MyIDictionary;
import utils.stack.MyIStack;

import java.util.List;
import java.util.stream.Collectors;

public class CallStmt implements IStmt {
    private final String procedure;
    private final List<Exp> parameters;

    public CallStmt(String procedure, List<Exp> parameters) {
        this.procedure = procedure;
        this.parameters = parameters;
    }

    @Override
    public IStmt deepCopy() {
        return new CallStmt(procedure, parameters.stream().map(Exp::deepCopy).collect(Collectors.toList()));
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        IProcTable<String, Pair<List<String>, IStmt>> procTable = state.getProcTable();
        MyIStack<IStmt> stk = state.getExeStack();
        IHeap<Value> heap = state.getHeap();
        MyIDictionary<String, Value> symTbl = state.getSymTable();

        if(!procTable.isDefined(procedure))
            throw new MyException("call: Procedure " + procedure + " is not defined.");

        Pair<List<String>, IStmt> proc = procTable.lookup(procedure);
        List<String> params = proc.getA();
        IStmt body = proc.getB();

        if(params.size() != parameters.size())
            throw new MyException("call: Procedure " + procedure + " must have " + params.size() + " parameters.");

        List<Value> values = parameters.stream().map(exp -> {
            try {
                return exp.eval(symTbl, heap);
            } catch (MyException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        MyIDictionary<String, Value> procSymTable = new MyDictionary<String, Value>();

        for(int i = 0; i < params.size(); i++){
            procSymTable.put(params.get(i), values.get(i));
        }

        state.pushSymTable(procSymTable);

        stk.push(new ReturnStmt());
        stk.push(body);
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        return typeEnv;
    }

    @Override
    public String toString() {
        return "call [" + procedure + parameters + "]";
    }
}
