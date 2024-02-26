package model.cmd;

import controller.Controller;
import controller.MyException;

public class RunExample extends Command {
    private Controller ctr;
    private static int id=0;

    public RunExample(String desc, Controller ctr) {
        super("" + (++id), desc);
        this.ctr = ctr;
    }

    @Override
    public void execute() {
        ctr.allStep();
    }
}
