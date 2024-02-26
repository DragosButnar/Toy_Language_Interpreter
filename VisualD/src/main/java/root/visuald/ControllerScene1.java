package root.visuald;

import controller.MyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.PrgState;
import model.stmt.IStmt;
import model.type.IType;
import model.value.Value;
import model.value.derived.StringValue;
import repository.IRepository;
import repository.Repository;
import utils.Pair;
import utils.dict.MyDictionary;
import utils.dict.MyIDictionary;
import utils.dict.barrier.BarrierTable;
import utils.dict.barrier.IBarrierTable;
import utils.dict.countSemaphore.CSemTable;
import utils.dict.countSemaphore.ICSemTable;
import utils.dict.heap.Heap;
import utils.dict.latch.ILatchTable;
import utils.dict.latch.LatchTable;
import utils.dict.lock.ILockTable;
import utils.dict.lock.LockTable;
import utils.dict.proc.IProcTable;
import utils.dict.proc.ProcTable;
import utils.list.MyIList;
import utils.list.MyList;
import utils.stack.MyIStack;
import utils.stack.MyStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class ControllerScene1 {
    private IStmt currentPrg;
    private List<IStmt> programs;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<String> exList;


    public void populateList(List<IStmt> prgs){
        List<String> exs = prgs.stream().map(iStmt -> {
            return iStmt.toString();
        }).toList();
        for (String prg: exs) {
            exList.getItems().add(prg);
        }
        exList.setStyle("-fx-font-size: 24px");
        programs = prgs;
    }

    public void returnToScene1(List<IStmt> prgs)
    {
        populateList(prgs);
    }

    @FXML
    public void switchToScene2(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene2.fxml"));
        root = loader.load();

        int index = exList.getSelectionModel().getSelectedIndex();
        if(index < 0 || index > programs.size() - 1)
            return;

        ControllerScene2 scene2 = loader.getController();
        scene2.initialize(setupEx(programs.get(index), "log" + index + ".out"), programs);

        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private static IRepository setupEx(IStmt ex, String file) {
        try {
            ex.typecheck(new MyDictionary<String, IType>());
        } catch (MyException e) {
            throw new RuntimeException(e);
        }

        Stack<MyIDictionary<String, Value>> symStack = new Stack<>();

        MyIDictionary<String, Value> dict = new MyDictionary<>();
        symStack.push(dict);

        MyIStack<IStmt> exeStack = new MyStack<>();
        MyIList<Value> output = new MyList<>();
        MyIDictionary<StringValue, BufferedReader> fileTable = new MyDictionary<>();
        Heap<Value> heap = new Heap<>();

        ILockTable<Integer> lockTable = new LockTable<>();
        IBarrierTable<Pair<Integer, List<Integer>>> barrierTable = new BarrierTable<>();
        IProcTable<String, Pair<List<String>, IStmt>> procTable = new ProcTable<>();
        ILatchTable<Integer> latchTable = new LatchTable<>();
        ICSemTable<Pair<Integer, List<Integer>>> csemTable = new CSemTable<>();

        PrgState prg = new PrgState(exeStack, symStack, output, fileTable, heap, procTable, lockTable,barrierTable, latchTable, csemTable, ex);

        return new Repository(prg, file);
    }

}
