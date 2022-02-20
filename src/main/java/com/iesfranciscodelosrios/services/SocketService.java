package com.iesfranciscodelosrios.services;

import com.iesfranciscodelosrios.model.Book;
import com.iesfranciscodelosrios.model.Client;
import com.iesfranciscodelosrios.model.Manager;
import com.iesfranciscodelosrios.model.User;
import com.iesfranciscodelosrios.model.dao.BookDAO;
import com.iesfranciscodelosrios.model.dao.ClientDAO;
import com.iesfranciscodelosrios.utils.Operations;
import com.iesfranciscodelosrios.utils.Tools;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedHashMap;
import java.util.List;

public class SocketService {
    /**
     * @param client
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void listenClientActions(Socket client) throws IOException, ClassNotFoundException {
        if (client != null && !client.isClosed()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(client.getInputStream());
                LinkedHashMap<Operations.UserOptions, Object> o = (LinkedHashMap<Operations.UserOptions, Object>) objectInputStream.readObject();
                //Si recibe la operación de registrarse por parte del cliente
                if (o.containsKey(Operations.UserOptions.Register)) {
                    User clientRegister = (User) o.get(Operations.UserOptions.Register);
                    sendDataToClient(client, ClientDAO.registerUser(clientRegister));
                    //Si recibe la operación de login por parte del cliente
                } else if (o.containsKey(Operations.UserOptions.Login)) {
                    User clientLogin = (User) o.get(Operations.UserOptions.Login);
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    if (ClientDAO.checkUser(clientLogin)) {
                        String[] menu = null;
                        if (clientLogin instanceof Manager)
                            menu = new String[]{"Añadir libros", "Eliminar libros", "Cambiar stock", "Gestionar Clientes"};
                        else if (clientLogin instanceof Client)
                            menu = new String[]{"Ver libros disponibles", "Ver Historial pedidos", "Ver Saldo", "Ingresar Saldo"};
                        if (menu != null) {
                            mapToSend.put(Operations.ServerActions.SendMenu, menu);
                            mapToSend.put(Operations.ServerActions.OperationOk, clientLogin);
                            sendDataToClient(client, mapToSend);
                        }
                    } else if (clientLogin.getId() == null) {
                        mapToSend.put(Operations.ServerActions.IncorrectUserType, clientLogin);
                        sendDataToClient(client, mapToSend);
                    } else if (clientLogin.getId() == -1L) {
                        mapToSend.put(Operations.ServerActions.WrongPassword, clientLogin);
                        sendDataToClient(client, mapToSend);
                    } else if (clientLogin.getId() == -2L) {
                        mapToSend.put(Operations.ServerActions.EmailDoesntExistOnDB, clientLogin);
                        sendDataToClient(client, mapToSend);
                    }
                } else if (o.containsKey(Operations.UserOptions.ViewOnStockBooks)) {
                    User clientLogin = (User) o.get(Operations.UserOptions.ViewOnStockBooks);
                    if (ClientDAO.checkUser(clientLogin)) {
                        List<Book> availableBooks = BookDAO.getAllOnStockBooks();

                    }
                } else if (o.containsKey(Operations.UserOptions.ViewAccount)) {
                    //enviar además el balance del cliente para que pueda verlo desde su perfil

                } else if (o.containsKey(Operations.UserOptions.ChargeAccount)) {

                } else if (o.containsKey(Operations.UserOptions.ViewPurchaseHistory)) {
                    //enviar el historial de compra del cliente
                } else if (o.containsKey(Operations.UserOptions.BuyItem)) {

                } else if (o.containsKey(Operations.UserOptions.AddBook)) {
                    User manager = (User) o.get(Operations.UserOptions.AddBook);
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    if (ClientDAO.checkUser(manager)) {
                        String[] items = new String[]{Tools.getDefaultCoverEncoded(), "Portada", "Título", "Autor" , "Fecha Salida", "Precio", "Stock"};
                        mapToSend.put(Operations.ServerActions.SendSubmenu, items);
                        sendDataToClient(client, mapToSend);
                    }
                } else if (o.containsKey(Operations.UserOptions.ChangeStock)) {
                    //enviar menu
                } else if (o.containsKey(Operations.UserOptions.DeleteBook)) {
                    //enviar menu
                } else if (o.containsKey(Operations.UserOptions.SeeClients)) {
                    //enviar menu
                } else if (o.containsKey(Operations.UserOptions.AddBookAction)) {
                    Book b = (Book) o.get(Operations.UserOptions.AddBookAction);
                    BookDAO.registerBook(b);
                }
            } catch (EOFException e) {
                if (objectInputStream != null)
                    objectInputStream.close();
                System.out.println(Tools.ANSI_CYAN + "Estaba esperando un objeto y se cerró la conexión con el cliente, voy a cerrar la conexión con el cliente" + client.getInetAddress().toString() + Tools.ANSI_RESET);
                throw new SocketException("El cliente se cerró mientras estaba esperando un objeto");
            }
        }
    }

    private static synchronized void sendDataToClient(Socket client, LinkedHashMap<Operations.ServerActions, Object> responseBody) {
        if (client != null && !client.isClosed()) {
            ObjectOutputStream objectOutputStream;
            try {
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                objectOutputStream.writeObject(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
