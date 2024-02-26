package root.visuald;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.cmd.ExitCommand;
import model.cmd.RunExample;
import model.exp.Exp;
import model.exp.binary.MulExp;
import model.exp.heap.HeapReadExp;
import model.exp.ternary.ArithExp;
import model.exp.ternary.RelExp;
import model.exp.unary.ValueExp;
import model.exp.unary.VarExp;
import model.stmt.barrier.BarrierAwaitStmt;
import model.stmt.barrier.NewBarrierStmt;
import model.stmt.controlStmts.ForStmt;
import model.stmt.controlStmts.IfStmt;
import model.stmt.controlStmts.RepeatStmt;
import model.stmt.controlStmts.SwitchStmt;
import model.stmt.countSem.AcquireSemStmt;
import model.stmt.countSem.NewCSemStmt;
import model.stmt.countSem.ReleaseCSemStmt;
import model.stmt.files.CloseRFileStmt;
import model.stmt.files.OpenRFileStmt;
import model.stmt.files.ReadFileStmt;
import model.stmt.heap.NewHeapEntryStmt;
import model.stmt.heap.WriteHeapStmt;
import model.stmt.latch.CountDownStmt;
import model.stmt.latch.LatchAwaitStmt;
import model.stmt.latch.NewLatchStmt;
import model.stmt.lock.LockStmt;
import model.stmt.lock.NewLockStmt;
import model.stmt.lock.UnlockStmt;
import model.stmt.multipleStmts.CompStmt;
import model.stmt.multipleStmts.ForkStmt;
import model.stmt.nothingStmt.NopStmt;
import model.stmt.nothingStmt.PrintStmt;
import model.stmt.nothingStmt.SleepStmt;
import model.stmt.nothingStmt.WaitStmt;
import model.stmt.procedures.CallStmt;
import model.stmt.procedures.NewProcStmt;
import model.stmt.variableStmt.AssignStmt;
import model.stmt.variableStmt.CondAssStmt;
import model.stmt.variableStmt.VarDeclStmt;
import model.type.*;
import model.stmt.*;
import model.value.derived.BoolValue;
import model.value.derived.IntValue;
import model.value.derived.StringValue;
import view.TextMenu;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        //region ConditionalAssignment
        /*
        Ref int a;
        Ref int b;
        int v;
        new(a,1);
        new(b,2);
        v=(rh(a)<rh(b))?100:200;
        print(v);
        v= ((rh(b)-2)>rh(a))?100:200;
        print(v);
        The final Out should be {100,200}
        * */
        IStmt condAssStmt = new CompStmt(
                new VarDeclStmt("a", new RefType(new IntType())),
                new CompStmt(
                        new VarDeclStmt("b", new RefType(new IntType())),
                        new CompStmt(
                                new VarDeclStmt("v", new IntType()),
                                new CompStmt(
                                        new NewHeapEntryStmt(
                                                "a",
                                                new ValueExp(new IntValue(1))
                                        ),
                                        new CompStmt(
                                                new NewHeapEntryStmt(
                                                        "b",
                                                        new ValueExp(new IntValue(2))
                                                ),
                                                new CompStmt(
                                                        new CondAssStmt(
                                                                "v",
                                                                new RelExp(
                                                                        "<",
                                                                        new HeapReadExp(new VarExp("a")),
                                                                        new HeapReadExp(new VarExp("b"))
                                                                ),
                                                                new ValueExp(new IntValue(100)),
                                                                new ValueExp(new IntValue(200))
                                                        ),
                                                        new CompStmt(
                                                                new PrintStmt(new VarExp("v")),
                                                                new CompStmt(
                                                                        new CondAssStmt(
                                                                                "v",
                                                                                new RelExp(
                                                                                        ">",
                                                                                        new ArithExp(
                                                                                                '-',
                                                                                                new HeapReadExp(new VarExp("b")),
                                                                                                new ValueExp(new IntValue(2))
                                                                                        ),
                                                                                        new HeapReadExp(new VarExp("a"))
                                                                                ),
                                                                                new ValueExp(new IntValue(100)),
                                                                                new ValueExp(new IntValue(200))
                                                                        ),
                                                                        new PrintStmt(new VarExp("v"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        //endregion
        //region CountDown Latch Example
        /*
        procedure forkBody(v, latch) wh(v,rh(v)*10)); print(rh(v)); countDown(latch);

        Ref int v1;
        Ref int v2;
        Ref int v3;
        int cnt;
        new(v1,2);
        new(v2,3);
        new(v3,4);
        newLatch(cnt,rH(v2));
        fork(
            wh(v1,rh(v1)*10));
            print(rh(v1));
            countDown(cnt);
            fork(
                wh(v2, rh(v2)*10));
                print(rh(v2));
                countDown(cnt);
                fork(
                    wh(v3,rh(v3)*10));
                    print(rh(v3));
                    countDown(cnt)
                )
            )
        );
        await(cnt);
        print(100);
        countDown(cnt);
        print(100)
        The final Out should be {20,id-first-child,30,id-second-child,40, id-third-child,
        100,id_parent,100}
        * */
        IStmt latchStmt = new CompStmt(
                        new VarDeclStmt("v1", new RefType(new IntType())),
                        new CompStmt(
                                new VarDeclStmt("v2", new RefType(new IntType())),
                                new CompStmt(
                                        new VarDeclStmt("v3", new RefType(new IntType())),
                                        new CompStmt(
                                                new VarDeclStmt("cnt", new IntType()),
                                                new CompStmt(
                                                        new NewHeapEntryStmt("v1", new ValueExp(new IntValue(2))),
                                                        new CompStmt(
                                                                new NewHeapEntryStmt("v2", new ValueExp(new IntValue(3))),
                                                                new CompStmt(
                                                                        new NewHeapEntryStmt("v3", new ValueExp(new IntValue(4))),
                                                                        new CompStmt(
                                                                                new NewLatchStmt(
                                                                                        "cnt",
                                                                                        new HeapReadExp(new VarExp("v2"))
                                                                                ),
                                                                                new CompStmt(
                                                                                        new ForkStmt(
                                                                                                new CompStmt(
                                                                                                        new CompStmt(
                                                                                                                new WriteHeapStmt(
                                                                                                                        "v1",
                                                                                                                        new ArithExp(
                                                                                                                                '*',
                                                                                                                                new HeapReadExp(new VarExp("v1")),
                                                                                                                                new ValueExp(new IntValue(10))
                                                                                                                        )
                                                                                                                ),
                                                                                                                new CompStmt(
                                                                                                                        new PrintStmt(
                                                                                                                                new HeapReadExp(new VarExp("v1"))
                                                                                                                        ),
                                                                                                                        new CountDownStmt("cnt")
                                                                                                                )
                                                                                                        ),
                                                                                                        new ForkStmt(
                                                                                                                new CompStmt(
                                                                                                                        new CompStmt(
                                                                                                                                new WriteHeapStmt(
                                                                                                                                        "v2",
                                                                                                                                        new ArithExp(
                                                                                                                                                '*',
                                                                                                                                                new HeapReadExp(new VarExp("v2")),
                                                                                                                                                new ValueExp(new IntValue(10))
                                                                                                                                        )
                                                                                                                                ),
                                                                                                                                new CompStmt(
                                                                                                                                        new PrintStmt(
                                                                                                                                                new HeapReadExp(new VarExp("v2"))
                                                                                                                                        ),
                                                                                                                                        new CountDownStmt("cnt")
                                                                                                                                )
                                                                                                                        ),
                                                                                                                        new ForkStmt(
                                                                                                                                new CompStmt(
                                                                                                                                        new WriteHeapStmt(
                                                                                                                                                "v3",
                                                                                                                                                new ArithExp(
                                                                                                                                                        '*',
                                                                                                                                                        new HeapReadExp(new VarExp("v3")),
                                                                                                                                                        new ValueExp(new IntValue(10))
                                                                                                                                                )
                                                                                                                                        ),
                                                                                                                                        new CompStmt(
                                                                                                                                                new PrintStmt(
                                                                                                                                                        new HeapReadExp(new VarExp("v3"))
                                                                                                                                                ),
                                                                                                                                                new CountDownStmt("cnt")
                                                                                                                                        )
                                                                                                                                )
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        ),
                                                                                        new CompStmt(
                                                                                                new LatchAwaitStmt("cnt"),
                                                                                                new CompStmt(
                                                                                                        new PrintStmt(new ValueExp(new IntValue(100))),
                                                                                                        new CompStmt(
                                                                                                                new CountDownStmt("cnt"),
                                                                                                                new PrintStmt(new ValueExp(new IntValue(100)))
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
        //endregion

        List<IStmt> programs = new ArrayList<>();
        programs.add(condAssStmt);
        programs.add(latchStmt);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("Scene1.fxml"));
        VBox rootLayout = (VBox) loader.load();

        ControllerScene1 controller = loader.getController();

        controller.populateList(programs);

        Scene scene1 = new Scene(rootLayout);
        stage.setScene(scene1);

        stage.show();
    }
}