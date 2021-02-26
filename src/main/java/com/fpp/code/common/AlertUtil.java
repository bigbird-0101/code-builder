package com.fpp.code.common;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertUtil {
    public static ButtonType showAlert(Alert.AlertType alertType, String title, String header, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().get();
    }

    public static ButtonType showInfo(String content){
        return showAlert(Alert.AlertType.INFORMATION,"成功","",content);
    }

    public static ButtonType showError(String content){
        return showAlert(Alert.AlertType.ERROR,"错误","",content);
    }

    public static ButtonType showWarning(String content){
        return showAlert(Alert.AlertType.WARNING,"警告","",content);
    }

    public static ButtonType showConfirm(String content){
        return showAlert(Alert.AlertType.CONFIRMATION,"警告","",content);
    }
}
