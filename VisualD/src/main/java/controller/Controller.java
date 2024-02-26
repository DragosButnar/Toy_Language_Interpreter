package controller;

import model.PrgState;
import model.value.derived.RefValue;
import model.value.Value;
import repository.IRepository;
import utils.dict.heap.IHeap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Controller {
    IRepository repo;
    ExecutorService executor;

    public Controller(IRepository repo) {
        this.repo = repo;
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

    public void allStep() {
        executor = Executors.newFixedThreadPool(2);
        //remove the completed programs
        List<PrgState> prgList=removeCompletedPrg(repo.getPrgList());
        while(!prgList.isEmpty()){
            PrgState prg = prgList.get(0);

            //GARBAGE DAY
            Map<Integer, Value> newHeap = safeGarbageCollector(getAllAddresses(prg.getSymTable().getContent().values(),
                    prg.getHeap()), prg.getHeap().getContent());
            prgList.forEach(p -> {p.getHeap().setContent(newHeap);});

            oneStepForAllPrg(prgList);
            //remove the completed programs
            prgList=removeCompletedPrg(repo.getPrgList());
        }
        executor.shutdownNow();
        //HERE the repository still contains at least one Completed Prg
        // and its List<PrgState> is not empty. Note that oneStepForAllPrg calls the method
        //setPrgList of repository in order to change the repository

        // update the repository state
        repo.setPrgList(prgList);
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


    private Map<Integer, Value> safeGarbageCollector(List<Integer> symTableAddr, Map<Integer,Value> heap){
        return heap.entrySet().stream()
                .filter(e->symTableAddr.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
