package io.github.bigbird0101.code.fxui.fx.controller;

import javafx.fxml.FXML;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/12 19:29
 */
public class AboutController {
    @FXML
    public void goGithub() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/bigbird-0101/code-builder"));
    }
}
