package repository;

import controller.MyException;
import model.PrgState;

import java.util.List;

public interface IRepository {
    List<PrgState> getPrgList();
    void setPrgList(List<PrgState> states);
    public void add(PrgState program);
    public void logPrgStateExec(PrgState state) throws MyException;
}
