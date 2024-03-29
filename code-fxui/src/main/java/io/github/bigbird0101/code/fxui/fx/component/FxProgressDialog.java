package io.github.bigbird0101.code.fxui.fx.component;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.text.DecimalFormat;

import static io.github.bigbird0101.code.fxui.common.LayoutHelper.hbox;
import static io.github.bigbird0101.code.fxui.common.LayoutHelper.vbox;
import static javafx.stage.WindowEvent.WINDOW_SHOWN;

/**
 * 执行后台任务的同时打开一个置顶的进度窗口，用户可以通过点击取消按钮中止任务执行
 * @author Administrator
 */
public final class FxProgressDialog {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.00%");

    private final ProgressTask progressTask;

    private final Window owner;

    private final Label messageLabel = new Label();

    private final Label progressLabel = new Label();

    private boolean showAsPercentage = true;

    private boolean showCancel;

    private boolean decorated = true;

    public static FxProgressDialog create(Window owner, ProgressTask progressTask, String message) {
        return new FxProgressDialog(owner, progressTask, message);
    }

    public static FxProgressDialog create(Window owner, ProgressTask progressTask, String message,boolean showCancel) {
        return new FxProgressDialog(owner, progressTask, message,showCancel);
    }

    private FxProgressDialog(Window owner, ProgressTask progressTask, String message) {
        this.owner = owner;
        this.progressTask = progressTask;

        progressTask.updateMessage(message);
    }

    private FxProgressDialog(Window owner, ProgressTask progressTask, String message,boolean showCancel) {
        this.owner = owner;
        this.progressTask = progressTask;
        this.showCancel=showCancel;
        progressTask.updateMessage(message);
    }

    public void setDecorated(boolean decorated) {
        this.decorated = decorated;
    }

    public void setShowAsPercentage(boolean showAsPercentage) {
        this.showAsPercentage = showAsPercentage;
    }

    public void show() {
        show0(false);
    }

    public void showAndWait() {
        show0(true);
    }

    private void show0(boolean wait) {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return progressTask;
            }
        };

        FxDialog<Void> fxDialog = new FxDialog<Void>()
            .setOwner(owner)
            .setBody(progressBody(progressTask))
            .setCloseable(false)
            .withStage(stage -> {
                if (!decorated) {
                    stage.initStyle(StageStyle.UNDECORATED);
                }
                stage.addEventHandler(WINDOW_SHOWN, event -> service.start());
                service.setOnSucceeded(event -> stage.close());
                service.setOnCancelled(event -> stage.close());
                service.setOnFailed(event -> stage.close());
            });

        if(showCancel){
            fxDialog.setButtonTypes(ButtonType.CANCEL);
            fxDialog.setButtonHandler(ButtonType.CANCEL, (event, stage) -> service.cancel());
        }

        if (wait) {
            fxDialog.showAndWait();
        } else {
            fxDialog.show();
        }
    }

    private Parent progressBody(ProgressTask progressTask) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefHeight(25);
        progressBar.setPrefWidth(300);

        progressTask.progressProperty().addListener(
            (observable, oldValue, newValue) -> updateProgress(progressTask, progressBar, progressLabel)
        );

        messageLabel.textProperty().bind(progressTask.messageProperty());

        return vbox(0, 5, Pos.CENTER,
            hbox(5, 0, messageLabel),
            new StackPane(progressBar, progressLabel)
        );
    }

    private void updateProgress(
        ProgressTask progressTask, ProgressBar progressBar, Label progressLabel
    ) {
        FxApp.runLater(() -> {
            double progress = progressTask.getProgress();
            progressBar.setProgress(progress);
            if (showAsPercentage) {
                progressLabel.setText(FORMAT.format(progress));
            } else {
                if (progressTask.getTotalWork() <= 1) {
                    progressLabel.setText(
                        String.format("%f / %f", progressTask.getWorkDone(), progressTask.getTotalWork())
                    );
                } else {
                    progressLabel.setText(
                        String.format("%d / %d", (int) progressTask.getWorkDone(), (int) progressTask.getTotalWork())
                    );
                }
            }
        });
    }
}
