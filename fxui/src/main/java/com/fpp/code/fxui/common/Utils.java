//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fpp.code.fxui.common;

import javafx.scene.Node;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

import java.util.Iterator;

public class Utils {
    public Utils() {
    }

    public static Window getWindow(Object owner) throws IllegalArgumentException {
        if (owner == null) {
            Window window = null;
            Iterator var3 =Window.impl_getWindows();
            Window w;
            do {
                if (!var3.hasNext()) {
                    return window;
                }

                w = (Window)var3.next();
                window = w;
            } while(!w.isFocused() || w instanceof PopupWindow);

            return w;
        } else if (owner instanceof Window) {
            return (Window)owner;
        } else if (owner instanceof Node) {
            return ((Node)owner).getScene().getWindow();
        } else {
            throw new IllegalArgumentException("Unknown owner: " + owner.getClass());
        }
    }

    public static final String getExcelLetterFromNumber(int number) {
        String letter;
        for(letter = ""; number >= 0; number = number / 26 - 1) {
            int remainder = number % 26;
            letter = (char)(remainder + 65) + letter;
        }

        return letter;
    }

    public static double clamp(double min, double value, double max) {
        if (value < min) {
            return min;
        } else {
            return value > max ? max : value;
        }
    }

    public static double nearest(double less, double value, double more) {
        double lessDiff = value - less;
        double moreDiff = more - value;
        return lessDiff < moreDiff ? less : more;
    }
}
