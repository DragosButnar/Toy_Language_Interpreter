package model.stmt.procedures;

import controller.MyException;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import utils.Pair;
import utils.dict.proc.IProcTable;
import utils.dict.MyIDictionary;

import java.util.List;

public class NewProcStmt implements IStmt {
    private final String name;
    private final List<String> parameters;
    private final IStmt instruction;

    public NewProcStmt(String name, List<String> parameters, IStmt instruction) {
        this.name = name;
        this.parameters = parameters;
        this.instruction = instruction;
    }

    @Override
    public IStmt deepCopy() {
        return new NewProcStmt(name, parameters, instruction.deepCopy());
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        IProcTable<String, Pair<List<String>, IStmt>> procs = state.getProcTable();
        if(procs.isDefined(name))
            throw new MyException("procedure: Procedure " + name + " must have only one implementation.");
        procs.put(name, new Pair<>(parameters, instruction));
        return null;
    }

    @Override
    public MyIDictionary<String, IType> typecheck(MyIDictionary<String, IType> typeEnv) throws MyException {
        return typeEnv;
    }

    @Override
    public String toString() {
        return "procedure[" + name + parameters + "->" + instruction + "]";
    }
}
