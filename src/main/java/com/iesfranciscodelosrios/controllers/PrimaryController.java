package com.iesfranciscodelosrios.controllers;

import com.iesfranciscodelosrios.services.SocketService;
import com.iesfranciscodelosrios.utils.Tools;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class PrimaryController {

    private static ServerSocket ss;
    private static Thread listeningToClientsThread;
    private static List<Thread> readClientsInputsThread = new ArrayList<>();

    /**
     * Este método se usa al iniciar el servidor, está pendiente de las conexiones de los clientes
     */
    public static void initializeServer() {
        //iniciar la conexión a la base de datos
        System.out.println("Iniciando el servidor");
        listeningToClients();
    }

    /**
     * Abirmos un nuevo hilo en nuestro programa para que esté constantemente checkeando las nuevas conexiones
     * de los clientes, aceptar las peticiones y procesarlas.
     */
    private static void listeningToClients() {
        try {
            ss = new ServerSocket(1995);
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor");
        }
        listeningToClientsThread = new Thread(() -> {
            System.out.println(Tools.ANSI_GREEN+"Voy a empezar a escuchar conexiones de clientes"+Tools.ANSI_RESET);
            while (listeningToClientsThread != null && !listeningToClientsThread.isInterrupted()) {
                try {
                    Socket client = ss.accept();
                    System.out.println(Tools.ANSI_GREEN + "Se ha conectado un nuevo cliente: " + client.getLocalSocketAddress().toString() + Tools.ANSI_RESET);
                    readClientInputs(client);
                } catch (IOException e) {
                    System.out.println("Error al aceptar la conexión con el cliente");
                }
            }
        });
        listeningToClientsThread.start();
    }

    /**
     * Abrimos un hilo para cada cliente que se conecte y de esta manera tendremos tantos hilos como clientes acepte nuestro servidor
     * El hilo terminará si el cliente se desconecta u ocurre un error de conexión entre ambos
     *
     * @param client cliente a escuchar acciones
     */
    private static void readClientInputs(Socket client) {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    SocketService.listenClientActions(client);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (!client.isClosed()) {
                    if (e instanceof SocketException)
                        System.out.println("El cliente se ha desconectado, voy a parar de leer sus mensajes y cerrar la conexión");
                    else if (e instanceof IOException)
                        System.out.println(Tools.ANSI_RED + "Error desconocido, acuda al desarrollador del servidor" + Tools.ANSI_RESET);
                    closeClient(client);
                }
            }
        });
        readClientsInputsThread.add(t);
        t.start();
    }

    private static void closeClient(Socket client) {
        if (client != null && !client.isClosed()) {
            try {
                client.close();
                System.out.println(Tools.ANSI_GREEN + "Se cerró correctamente la conexión con el cliente: " + client.getLocalSocketAddress().toString() + Tools.ANSI_RESET);
            } catch (IOException e) {
                System.out.println("Error al cerrar el cliente");
            }
        }
    }

}
