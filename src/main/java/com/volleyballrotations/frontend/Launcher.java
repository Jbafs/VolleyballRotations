package com.volleyballrotations.frontend;

import javafx.application.Application;

/**
 * Entry point that delegates to {@link VolleyballApplication}, working around JavaFX module launch restrictions.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(VolleyballApplication.class, args);
    }
}
