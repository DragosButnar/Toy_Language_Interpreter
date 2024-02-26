package repository;

import controller.MyException;
import model.PrgState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository{
    private List<PrgState> programs;
    final private String logFilePath;

    public Repository(PrgState program, String logFilePath) {
        this.programs = new ArrayList<PrgState>();
        this.logFilePath = logFilePath;
        this.add(program);
    }

    @Override
    public List<PrgState> getPrgList() {
        return programs;
    }

    @Override
    public void setPrgList(List<PrgState> states) {
        programs = states;
    }

    @Override
    public void add(PrgState program) {
        programs.add(program);
    }

    @Override
    public void logPrgStateExec(PrgState state) throws MyException {
        try {
            PrintWriter logFile = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)));

            logFile.print(state);

            logFile.close();
        }
        catch (IOException e)
        {
            throw new MyException("Invalid file path");
        }
    }

    @Override
    public String toString() {
        return "Repository{" +
                "programs=" + programs +
                '}';
    }
}
