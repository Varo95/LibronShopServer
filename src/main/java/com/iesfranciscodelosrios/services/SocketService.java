package com.iesfranciscodelosrios.services;

import com.iesfranciscodelosrios.model.Book;
import com.iesfranciscodelosrios.model.Client;
import com.iesfranciscodelosrios.model.Manager;
import com.iesfranciscodelosrios.model.User;
import com.iesfranciscodelosrios.model.dao.BookDAO;
import com.iesfranciscodelosrios.model.dao.ClientDAO;
import com.iesfranciscodelosrios.model.nmrelation.UserBook;
import com.iesfranciscodelosrios.utils.Operations;
import com.iesfranciscodelosrios.utils.Tools;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SocketService {

    public static void listenClientActions(Socket client) throws IOException, ClassNotFoundException {
        if (client != null && !client.isClosed()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(client.getInputStream());
                LinkedHashMap<Operations.UserOptions, Object> o = (LinkedHashMap<Operations.UserOptions, Object>) objectInputStream.readObject();
                //Si recibe la operación de registrarse por parte del cliente
                if (o.containsKey(Operations.UserOptions.Register)) {
                    User clientRegister = (User) o.get(Operations.UserOptions.Register);
                    sendDataToClient(client, ClientDAO.getInstance().registerUser(clientRegister));
                //Si recibe la operación de login por parte del cliente
                } else if (o.containsKey(Operations.UserOptions.Login)) {
                    User clientLogin = (User) o.get(Operations.UserOptions.Login);
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    if (ClientDAO.getInstance().checkUser(clientLogin, true)) {
                        String[] menu = null;
                        if (clientLogin instanceof Manager)
                            menu = new String[]{"Añadir libros", "Cambiar stock"};
                        else if (clientLogin instanceof Client)
                            menu = new String[]{"Ver libros disponibles", "Ver Historial pedidos", "Ver Saldo", "Ingresar Saldo"};
                        if (menu != null) {
                            mapToSend.put(Operations.ServerActions.SendMenu, menu);
                            mapToSend.put(Operations.ServerActions.OperationOk, clientLogin);
                            sendDataToClient(client, mapToSend);
                        }
                    }else if(clientLogin.getId()==null) {
                        mapToSend.put(Operations.ServerActions.IncorrectUserType,clientLogin);
                        sendDataToClient(client, mapToSend);
                    }else if(clientLogin.getId()==-1L){
                        mapToSend.put(Operations.ServerActions.WrongPassword,clientLogin);
                        sendDataToClient(client, mapToSend);
                    }else if(clientLogin.getId()==-2L){
                        mapToSend.put(Operations.ServerActions.EmailDoesntExistOnDB,clientLogin);
                        sendDataToClient(client, mapToSend);
                    }
                } else if (o.containsKey(Operations.UserOptions.ViewOnStockBooks)) {
                    User clientLogin = (User) o.get(Operations.UserOptions.ViewOnStockBooks);
                    if(ClientDAO.getInstance().checkUser(clientLogin, false)){
                        LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                        mapToSend.put(Operations.ServerActions.SendBooksToPurchase,BookDAO.getInstance().getAllOnStockBooks());
                        sendDataToClient(client, mapToSend);
                    }
                } else if (o.containsKey(Operations.UserOptions.ViewAccount)) {
                    //enviar además el balance del cliente para que pueda verlo desde su perfil
                    User clientLogin = (User) o.get(Operations.UserOptions.ViewAccount);
                    Double account = ClientDAO.getInstance().getClientAccount((Client)clientLogin);
                    Operations.ServerActions operation;
                    if(account!=-2)
                        operation = Operations.ServerActions.OperationOk;
                    else
                        operation = Operations.ServerActions.OperationOkButNoContent;
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    mapToSend.put(operation, account);
                    sendDataToClient(client, mapToSend);
                }else if(o.containsKey(Operations.UserOptions.ChargeAccount)){
                    User clientLogin = (User) o.get(Operations.UserOptions.ChargeAccount);
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    if(ClientDAO.getInstance().checkUser(clientLogin, false) && clientLogin instanceof Client c) {
                        Map<String, Client> mapClient = new HashMap<>();
                        mapClient.put("Ingrese una cantidad para añadirla a su cuenta", c);
                        mapToSend.put(Operations.ServerActions.SendProfile, mapClient);
                        sendDataToClient(client, mapToSend);
                    }
                }else if(o.containsKey(Operations.UserOptions.ChargeAccountSend)){
                    Map<User, Double> clientAndNewAmount = (Map<User, Double>) o.get(Operations.UserOptions.ChargeAccountSend);
                    clientAndNewAmount.forEach((user, aDouble) -> {
                        if(user instanceof Client c && ClientDAO.getInstance().checkUser(c, false)){
                            LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                            mapToSend.put(Operations.ServerActions.OperationOk,ClientDAO.getInstance().increaseAccount(c, aDouble));
                            mapToSend.put(Operations.ServerActions.NewBalance, user);
                            sendDataToClient(client,mapToSend);
                        }
                    });
                }else if(o.containsKey(Operations.UserOptions.ViewPurchaseHistory)){
                    Client clientLogin = (Client) o.get(Operations.UserOptions.ViewPurchaseHistory);
                    Map<LocalDateTime, Book> historicalPurchases = new LinkedHashMap<>();
                    for (UserBook user_book:ClientDAO.getInstance().getClientHistorical(clientLogin)){
                        user_book.getBook().setFrontPage("");
                        historicalPurchases.put(user_book.getPurchaseDate(), user_book.getBook());
                    }
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    mapToSend.put(Operations.ServerActions.SendPurchaseHistory, historicalPurchases);
                    sendDataToClient(client, mapToSend);
                }else if(o.containsKey(Operations.UserOptions.BuyItem)){
                    Map<User, Book> userToBuyItem = (Map<User, Book>) o.get(Operations.UserOptions.BuyItem);
                    userToBuyItem.forEach((user, book) -> {
                        if(user instanceof Client c){
                            double prePurchaseBalance = c.getBalance() - book.getPrice();
                            LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                            if(prePurchaseBalance>=0){
                                ClientDAO.getInstance().purchaseBook(c, book);
                                LinkedHashMap<User, Book> linkedUserBook = new LinkedHashMap<>();
                                linkedUserBook.put(c, book);
                                mapToSend.put(Operations.ServerActions.OperationOk, linkedUserBook);
                            }else{
                                mapToSend.put(Operations.ServerActions.NotEnoughBalance, null);
                            }
                            sendDataToClient(client, mapToSend);
                        }
                    });
                }else if(o.containsKey(Operations.UserOptions.AddBook)){
                    User manager = (User) o.get(Operations.UserOptions.AddBook);
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    if(manager instanceof Manager && ClientDAO.getInstance().checkUser(manager, false)){
                        String[] items = new String[]{Tools.getDefaultCoverEncoded(),"Portada","Título", "Autor", "Fecha Salida", "Precio", "Stock" };
                        mapToSend.put(Operations.ServerActions.SendAddBook, items);
                        sendDataToClient(client,mapToSend);
                    }
                }else if(o.containsKey(Operations.UserOptions.ChangeStock)){
                    User manager = (User) o.get(Operations.UserOptions.ChangeStock);
                    LinkedHashMap<Operations.ServerActions, Object> mapToSend = new LinkedHashMap<>();
                    if(manager instanceof Manager m && ClientDAO.getInstance().checkUser(m, false)){
                        mapToSend.put(Operations.ServerActions.SendMenuBooks, BookDAO.getInstance().getAllBooks());
                        sendDataToClient(client, mapToSend);
                    }
                }else if(o.containsKey(Operations.UserOptions.ChangeStockAction)){
                    Book b = (Book) o.get(Operations.UserOptions.ChangeStockAction);
                    BookDAO.getInstance().changeStock(b);
                }else if(o.containsKey(Operations.UserOptions.AddBookAction)){
                    Book b = (Book) o.get(Operations.UserOptions.AddBookAction);
                    sendDataToClient(client,BookDAO.getInstance().registerBook(b));
                }
            } catch (EOFException e) {
                if (objectInputStream != null)
                    objectInputStream.close();
                System.out.println(Tools.ANSI_CYAN + "Estaba esperando un objeto y se cerró la conexión con el cliente, voy a cerrar la conexión con el cliente" + client.getInetAddress().toString() + Tools.ANSI_RESET);
                throw new SocketException("El cliente se cerró mientras estaba esperando un objeto");
            }
        }
    }

    private static void sendDataToClient(Socket client, LinkedHashMap<Operations.ServerActions, Object> responseBody) {
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
