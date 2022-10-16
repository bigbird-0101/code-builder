package io.github.bigbird0101.code.fxui.common;

import javafx.scene.Node;

public class GetScreenUtil {
//	public static double getScreenX(Control control) {
//		return control.getScene().getWindow().getX() + control.getScene().getX() + control.localToScene(0, 0).getX();
//	}
//
//	public static double getScreenY(Control control) {
//		return control.getScene().getWindow().getY() + control.getScene().getY() + control.localToScene(0, 0).getY();
//	}
//
//	public static double getWidth(Control control) {
//		return control.getBoundsInParent().getWidth();
//	}
//
//	public static double getHeight(Control control) {
//		return control.getBoundsInParent().getHeight();
//	}
	
	public static double getScreenX(Node control) {
		return control.getScene().getWindow().getX() + control.getScene().getX() + control.localToScene(0, 0).getX();
	}

	public static double getScreenY(Node control) {
		return control.getScene().getWindow().getY() + control.getScene().getY() + control.localToScene(0, 0).getY();
	}

	public static double getWidth(Node control) {
		return control.getBoundsInParent().getWidth();
	}

	public static double getHeight(Node control) {
		return control.getBoundsInParent().getHeight();
	}
}
