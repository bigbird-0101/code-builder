package com.fpp.code.fx.controller;

import javafx.fxml.FXML;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/12 19:29
 */
public class AboutController {
    @FXML
    public void goGithub() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/bigbird-0101/spring-code"));
    }
}
