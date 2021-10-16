package com.fpp.code.common;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertUtil {
    public static void showAlert(Alert.AlertType alertType, String title, String header, String content){
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public static ButtonType showAlertReturn(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        return alert.showAndWait().get();
    }

    public static void showInfo(String content){
        showAlert(Alert.AlertType.INFORMATION, "成功", "", content);
    }

    public static void showError(String content){
        showAlert(Alert.AlertType.ERROR, "错误", "", content);
    }

    public static void showWarning(String content){
        showAlert(Alert.AlertType.WARNING, "警告", "", content);
    }

    public static ButtonType showConfirm(String content){
        return showAlertReturn(Alert.AlertType.CONFIRMATION,"警告","",content);
    }
}
