package io.github.bigbird0101.code.fxui.common;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.tools.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Administrator
 */
public class TooltipUtil {
    public static void showToast(String message) {
        showToast((Node) null, message);
    }

    public static void showToast(Node node, String message) {
        Window window = Utils.getWindow(node);
        if (null == window) {
            return;
        }
        double x = 0;
        double y = 0;
        if (node != null) {
            x = GetScreenUtil.getScreenX(node) + GetScreenUtil.getWidth(node) / 2;
            y = GetScreenUtil.getScreenY(node) + GetScreenUtil.getHeight(node);
        } else {
            x = window.getX() + window.getWidth() / 2;
            y = window.getY() + window.getHeight();
//			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//            x = screenBounds.getWidth() /2;
//            y = screenBounds.getHeight();
        }
        showToast(window, message, 3000, x, y);
    }

    public static void showToast(Window window, String message, long time, double x, double y) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setAutoHide(true);
        tooltip.setOpacity(0.9d);
        tooltip.setWrapText(true);
        tooltip.show(window, x, y);
        tooltip.setAnchorX(tooltip.getAnchorX() - tooltip.getWidth() / 2);
        tooltip.setAnchorY(tooltip.getAnchorY() - tooltip.getHeight());
        if (time > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(tooltip::hide);
                }
            }, time);
        }
    }

    public static void showToast(String message, Pos pos) {
        ToastParam toastParam=new ToastParam();
        toastParam.setTitle(null);
        toastParam.setMessage(message);
        toastParam.setPos(pos);
        toastParam.setHideTime(3);
        toastParam.setHideCloseButton(true);
        toastParam.setDarkStyle(true);
        showToast(toastParam);
    }

    public static void showToast(String title, String message) {
        ToastParam toastParam=new ToastParam();
        toastParam.setTitle(title);
        toastParam.setMessage(message);
        toastParam.setPos(Pos.BOTTOM_CENTER);
        toastParam.setHideTime(3);
        toastParam.setHideCloseButton(true);
        toastParam.setDarkStyle(true);
        showToast(toastParam);
    }

    public static void showToast(String title, String message, Pos pos) {
        ToastParam toastParam=new ToastParam();
        toastParam.setTitle(title);
        toastParam.setMessage(message);
        toastParam.setPos(pos);
        toastParam.setHideTime(3);
        toastParam.setHideCloseButton(true);
        toastParam.setDarkStyle(true);
        showToast(toastParam);
    }

    public static void showToast(ToastParam toastParam) {
        Notifications notificationBuilder = Notifications.create()
                .title(toastParam.getTitle())
                .text(toastParam.getMessage())
                .graphic(toastParam.getGraphic())
                .hideAfter(Duration.seconds(toastParam.getHideTime()))
                .position(toastParam.getPos()).onAction(toastParam.getOnAction());
        if (toastParam.getOwner() != null) {
            notificationBuilder.owner(toastParam.getOwner());
        }
        if (toastParam.isHideCloseButton) {
            notificationBuilder.hideCloseButton();
        }
        if (toastParam.isDarkStyle) {
            notificationBuilder.darkStyle();
        }
        Platform.runLater(notificationBuilder::show);
    }

    public static class ToastParam{
        private String title;
        private String message;
        private Node graphic;
        private double hideTime;
        private Pos pos;
        private EventHandler<ActionEvent> onAction;
        private Object owner;
        private boolean isHideCloseButton;
        private boolean isDarkStyle;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Node getGraphic() {
            return graphic;
        }

        public void setGraphic(Node graphic) {
            this.graphic = graphic;
        }

        public double getHideTime() {
            return hideTime;
        }

        public void setHideTime(double hideTime) {
            this.hideTime = hideTime;
        }

        public Pos getPos() {
            return pos;
        }

        public void setPos(Pos pos) {
            this.pos = pos;
        }

        public EventHandler<ActionEvent> getOnAction() {
            return onAction;
        }

        public void setOnAction(EventHandler<ActionEvent> onAction) {
            this.onAction = onAction;
        }

        public Object getOwner() {
            return owner;
        }

        public void setOwner(Object owner) {
            this.owner = owner;
        }

        public boolean isHideCloseButton() {
            return isHideCloseButton;
        }

        public void setHideCloseButton(boolean hideCloseButton) {
            isHideCloseButton = hideCloseButton;
        }

        public boolean isDarkStyle() {
            return isDarkStyle;
        }

        public void setDarkStyle(boolean darkStyle) {
            isDarkStyle = darkStyle;
        }
    }
}
