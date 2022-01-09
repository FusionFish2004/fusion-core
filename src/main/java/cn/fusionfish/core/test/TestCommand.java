package cn.fusionfish.core.test;

import cn.fusionfish.core.annotations.Command;
import cn.fusionfish.core.command.SimpleCommand;

public class TestCommand extends SimpleCommand {
    public TestCommand() {
        super("test");
    }

    @Override
    public void onCommand() {

    }
}
