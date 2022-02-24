package com.iesfranciscodelosrios.utils;

import com.iesfranciscodelosrios.Start;

import java.io.IOException;
import java.util.Base64;

public class Tools {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static String getDefaultCoverEncoded(){
        String result;
        try {
            result = Base64.getEncoder().encodeToString(Start.class.getResourceAsStream("default_cover.png").readAllBytes());
        }catch (IOException e){
            System.out.println("Hubo un error al cargar la imagen por defecto");
            result = "";
        }
        return result;
    }
}
