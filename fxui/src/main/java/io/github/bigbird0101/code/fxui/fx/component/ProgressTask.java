package io.github.bigbird0101.code.fxui.fx.component;

import javafx.concurrent.Task;

/**
 * @author Administrator
 */
public abstract class ProgressTask extends Task<Void> {

    @Override
    protected Void call() throws Exception {
        execute();
        return null;
    }

    @Override
    public void updateMessage(String message) {
        super.updateMessage(message);
    }

    protected abstract void execute() throws Exception;
}
