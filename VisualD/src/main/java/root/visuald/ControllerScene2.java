package root.visuald;

import controller.MyException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.PrgState;

import model.stmt.IStmt;
import model.value.derived.RefValue;
import model.value.Value;
import repository.IRepository;
import utils.dict.heap.IHeap;
import utils.list.MyList;
import utils.stack.MyIStack;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ControllerScene2 {
    List<IStmt> programs;
    IRepository repo;
    List<PrgState> prgList;
    ExecutorService executor;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private Button fxButton;

    @FXML private TableView<TableModel> fxHeap = new TableView<>();
    private final ObservableList<TableModel> heapData = FXCollections.observableArrayList();
    @FXML TableColumn<TableModel, String> addressColumn = new TableColumn<>("Address");
    @FXML TableColumn<TableModel, String> valueColumn = new TableColumn<>("Value");

    @FXML private TableView<TableModel> fxSym = new TableView<>();
    private final ObservableList<TableModel> symData = FXCollections.observableArrayList();
    @FXML TableColumn<TableModel, String> symNameColumn = new TableColumn<>("Name");
    @FXML TableColumn<TableModel, String> symValueColumn = new TableColumn<>("Value");

    @FXML private ListView<String> fxOut = new ListView<>();
    @FXML private ListView<String> fxFiles = new ListView<>();
    @FXML private ListView<String> fxStates = new ListView<>();
    @FXML private ListView<String> fxStack = new ListView<>();
    @FXML private TextField fxNrPrgs;

    @FXML private TableView<TableModel> fxLatchTable = new TableView<>();
    private final ObservableList<TableModel> latchData = FXCollections.observableArrayList();
    @FXML TableColumn<TableModel, String> latchLocationColumn = new TableColumn<>("Address");
    @FXML TableColumn<TableModel, String> latchValueColumn = new TableColumn<>("Value");

    public static class TableModel {
        private final SimpleStringProperty address;
        private final SimpleStringProperty value;

        public TableModel(String address, String value) {
            this.address = new SimpleStringProperty(address);
            this.value = new SimpleStringProperty(value);
        }

        public String getAddress() {
            return address.get();
        }

        public SimpleStringProperty addressProperty() {
            return address;
        }

        public void setAddress(String address) {
            this.address.set(address);
        }

        public String getValue() {
            return value.get();
        }

        public SimpleStringProperty valueProperty() {
            return value;
        }

        public void setValue(String value) {
            this.value.set(value);
        }
    }

    public void switchToScene1(Event event) throws IOException {
        executor.shutdownNow();
        repo.setPrgList(prgList);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene1.fxml"));
        root = loader.load();

        ControllerScene1 scene1 = loader.getController();
        scene1.returnToScene1(programs);

        stage = (Stage) (((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void runButtonClick(ActionEvent event) throws IOException {

        PrgState prgs;
        if(!prgList.isEmpty()) {

            prgs = prgList.get(0);

            //GARBAGE DAY
            Map<Integer, Value> newHeap = safeGarbageCollector(getAllAddresses(prgs.getSymTable().getContent().values(),
                    prgs.getHeap()), prgs.getHeap().getContent());
            prgList.forEach(p -> {
                p.getHeap().setContent(newHeap);
            });

            oneStepForAllPrg(prgList);

            refreshViews(prgList, prgs, newHeap);

            prgList=removeCompletedPrg(repo.getPrgList());
        }
        else{
            switchToScene1(event);
        }

    }

    @FXML
    public void indexChange(MouseEvent e){
        PrgState program = repo.getPrgList().get(fxStates.getSelectionModel().getSelectedIndex());

        // Refreshing symbol table
        fxSym.getItems().clear();
        for (Map.Entry<String, Value> entry : program.getSymTable().getContent().entrySet()) {
            TableModel hm = new TableModel(entry.getKey(), entry.getValue().toString());
            symData.add(hm);
        }

        // Refreshing execution stack
        fxStack.getItems().clear();
        program.getExeStack().reverse().forEach(value -> {fxStack.getItems().add(value.toString());});

        //Refreshing latch table
        fxLatchTable.getItems().clear();
        for(Map.Entry<Integer, Integer> entry : program.getLatchTable().getContent().entrySet()){
            TableModel lm = new TableModel(entry.getKey().toString(), entry.getValue().toString());
            latchData.add(lm);
        }
    }

    void oneStepForAllPrg(List<PrgState> prgList) {
        prgList.forEach(prg -> {
            try {
                repo.logPrgStateExec(prg);
            } catch (MyException e) {
                throw new RuntimeException(e);
            }
        });
        List<Callable<PrgState>> callList = prgList.stream()
                .map((PrgState p) -> (Callable<PrgState>)(p::oneStep))
                .collect(Collectors.toList());

        List<PrgState> newPrgList = null;
        try {
            newPrgList = executor.invokeAll(callList). stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        prgList.addAll(newPrgList);
        prgList.forEach(prg -> {
            try {
                repo.logPrgStateExec(prg);
            } catch (MyException e) {
                throw new RuntimeException(e);
            }
        });
        prgList.forEach(System.out::println);
        repo.setPrgList(prgList);
    }

    private void refreshViews(List<PrgState> prgList, PrgState prgs, Map<Integer, Value> newHeap){

        // Refreshing Nr program states
        fxNrPrgs.setText("" + repo.getPrgList().size());

        // Refreshing heap
        heapData.clear();
        for (Map.Entry<Integer, Value> entry : newHeap.entrySet()) {
            TableModel hm = new TableModel(entry.getKey().toString(), entry.getValue().toString());
            heapData.add(hm);
        }

        // Refreshing out
        fxOut.getItems().clear();
        prgs.getOut().getContent().forEach(value -> {fxOut.getItems().add(value.toString());});

        // Refreshing file table
        fxFiles.getItems().clear();
        prgs.getFiles().getContent().forEach((value, buffer) -> {fxFiles.getItems().add(value.toString());});

        // Refreshing program states
        fxStates.getItems().clear();
        prgList.forEach(program -> {fxStates.getItems().add("Program " + program.getId());});
        fxStates.getSelectionModel().select(0);

        // Refreshing symbol table
        fxSym.getItems().clear();
        for (Map.Entry<String, Value> entry : prgs.getSymTable().getContent().entrySet()) {
            TableModel hm = new TableModel(entry.getKey(), entry.getValue().toString());
            symData.add(hm);
        }

        //Refreshing latch table
        fxLatchTable.getItems().clear();
        for(Map.Entry<Integer, Integer> entry : prgs.getLatchTable().getContent().entrySet()){
            TableModel lm = new TableModel(entry.getKey().toString(), entry.getValue().toString());
            latchData.add(lm);
        }

        // Refreshing execution stack
        fxStack.getItems().clear();
        prgs.getExeStack().reverse().forEach(value -> {fxStack.getItems().add(value.toString());});
    }

    public void initialize(IRepository repo, List<IStmt> prgs) {
        executor = Executors.newFixedThreadPool(8);
        this.repo = repo;
        prgList=removeCompletedPrg(repo.getPrgList());
        this.programs = prgs;
        PrgState initialPrg = repo.getPrgList().get(0);

        MyIStack<IStmt> stack = initialPrg.getExeStack().clone();
        fxStack.getItems().add(stack.pop().toString());
        fxNrPrgs.setText("" + repo.getPrgList().size());


        fxHeap.setEditable(true);
        addressColumn.setCellValueFactory(new PropertyValueFactory<TableModel, String>("address"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<TableModel, String>("value"));
        fxHeap.setItems(heapData);

        fxSym.setEditable(true);
        symNameColumn.setCellValueFactory(new PropertyValueFactory<TableModel, String>("address"));
        symValueColumn.setCellValueFactory(new PropertyValueFactory<TableModel, String>("value"));
        fxSym.setItems(symData);

        fxLatchTable.setEditable(true);
        latchLocationColumn.setCellValueFactory(new PropertyValueFactory<TableModel, String>("address"));
        latchValueColumn.setCellValueFactory(new PropertyValueFactory<TableModel, String>("value"));
        fxLatchTable.setItems(latchData);
    }

    private List<Integer> getAllAddresses(Collection<Value> symTableValues, IHeap<Value> heapTable){
        ConcurrentLinkedDeque<Integer> symTableAdr = symTableValues.stream()
                .filter(v-> v instanceof RefValue)
                .map(v-> {RefValue v1 = (RefValue)v; return v1.getAddress();})
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));

        symTableAdr
                .forEach(a-> {
                    Value v= heapTable.getContent().get(a);
                    if (v instanceof  RefValue)
                        if (!symTableAdr.contains(((RefValue)v).getAddress()))
                            symTableAdr.add(((RefValue)v).getAddress());});


        return symTableAdr.stream().toList();
    }


    private Map<Integer, Value> safeGarbageCollector(List<Integer> symTableAddr, Map<Integer,Value> heap) {
        return heap.entrySet().stream()
                .filter(e -> symTableAddr.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public IRepository getRepo() {
        return repo;
    }

    public void setRepo(IRepository repo) {
        this.repo = repo;
    }

    List<PrgState> removeCompletedPrg(List<PrgState> inPrgList){
        return inPrgList.stream()
                .filter(PrgState::isNotCompleted)
                .collect(Collectors.toList());
    }
}
