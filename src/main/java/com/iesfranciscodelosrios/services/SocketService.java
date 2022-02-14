package com.iesfranciscodelosrios.services;

import com.iesfranciscodelosrios.model.Client;
import com.iesfranciscodelosrios.model.Manager;
import com.iesfranciscodelosrios.utils.Tools;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class SocketService {

    public static void listenClientActions(Socket client) throws IOException, ClassNotFoundException {
        if (client != null && !client.isClosed()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(client.getInputStream());
                Object o = objectInputStream.readObject();
                System.out.println("aa");
                //enviar mensajes desde el cliente al servidor con Map<k,v>???
                //loguearse y verificar que exista en la base de datos. En caso de éxito, devolver el menú correspondiente
                //registrarse y enviar mensaje de OK cuando se haya registrado en la base de datos
                if (o instanceof Client) {
                    //enviar menú de Cliente
                } else if (o instanceof Manager) {
                    //enviar menú de Manager
                }
            }catch (EOFException e){
                if(objectInputStream!=null)
                    objectInputStream.close();
                System.out.println(Tools.ANSI_CYAN+"Estaba esperando un objeto y se cerró la conexión con el cliente, voy a cerrar la conexión con el cliente"+client.getInetAddress().toString()+Tools.ANSI_RESET);
                throw new SocketException("El cliente se cerró mientras estaba esperando un objeto");
            }
        }
    }

}
