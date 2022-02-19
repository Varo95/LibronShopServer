package com.iesfranciscodelosrios;

import com.iesfranciscodelosrios.controllers.PrimaryController;

import java.util.logging.Level;

public class Start {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        PrimaryController.initializeServer();
    }
}
