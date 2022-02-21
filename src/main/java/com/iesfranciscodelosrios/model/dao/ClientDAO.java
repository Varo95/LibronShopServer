package com.iesfranciscodelosrios.model.dao;

import com.iesfranciscodelosrios.model.Book;
import com.iesfranciscodelosrios.model.Client;
import com.iesfranciscodelosrios.model.Manager;
import com.iesfranciscodelosrios.model.User;
import com.iesfranciscodelosrios.utils.Operations;
import com.iesfranciscodelosrios.utils.PersistenceUnit;
import com.iesfranciscodelosrios.utils.Tools;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

//Este hará el CRUD de cliente y manager
@MappedSuperclass
public class ClientDAO {

    private static ClientDAO clientDAO = getInstance();

    public static synchronized ClientDAO getInstance() {
        if (clientDAO == null)
            clientDAO = new ClientDAO();
        return clientDAO;
    }

    /**
     * Este método revisa si un usuario está registrada en la aplicación o no a través del email
     *
     * @param u usuario a revisar
     * @return true si son correctas las credenciales, false si no se encuentra en la BD ó no coinciden las crendenciales
     */
    public synchronized boolean checkUser(User u, boolean login) {
        boolean result = false;
        try {
            EntityManager em = PersistenceUnit.createEM();
            em.getTransaction().begin();
            Query q = em.createNativeQuery("SELECT ID, EMAIL, PASSWORD, BALANCE, ISMANAGER FROM _USER WHERE _USER.EMAIL=?");
            q.setParameter(1, u.getEmail());
            try {
                Object[] columns = (Object[]) q.getSingleResult();
                if (u.getPassword().equals(columns[2])) {
                    if (u.isManager() == ((Byte) columns[4] != 0)) {
                        u.setId(((BigInteger) columns[0]).longValue());
                        if (u instanceof Client c)
                            c.setBalance((Double) columns[3]);
                        result = true;
                    } else {
                        u.setId(null);
                        if (login)
                            System.out.println(Tools.ANSI_YELLOW + "Un usuario intentó iniciar sesión como " + (u.isManager() ? "Manager" : "Cliente") + ", pero su tipo en la BD es " + (((Byte) columns[4] != 0) ? "Manager" : "Cliente") + Tools.ANSI_RESET);
                        result = false;
                    }
                } else {
                    if (login)
                        System.out.println("El usuario " + u.getEmail() + " intentó iniciar sesión pero su contraseña era distinta a la guardada");
                    result = false;
                }
            } catch (NoResultException e) {
                if (login)
                    System.out.println("El usuario " + u.getEmail() + " intentó iniciar sesión pero no está registrado");
                u.setId(-2L);
                result = false;
            } finally {
                em.getTransaction().commit();
                PersistenceUnit.closeEM();
                notifyAll();
            }
        } catch (Exception e) {
            System.out.println("Hubo un problema a la hora de chekear el usuario");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Este método registra tanto usuarios clientes como managers
     *
     * @param client cliente a registrar
     * @return clave valor-> operacion ok, usuario con id registrado | usuario ya existe, cliente sin registrar
     */
    public synchronized LinkedHashMap<Operations.ServerActions, Object> registerUser(User client) {
        LinkedHashMap<Operations.ServerActions, Object> result = new LinkedHashMap<>();
        try {
            if (!ClientDAO.getInstance().checkUser(client, false)) {
                EntityManager em = PersistenceUnit.createEM();
                em.getTransaction().begin();
                User u;
                if (client instanceof Manager m) {
                    u = em.merge(m);
                } else {
                    u = em.merge((Client) client);
                }
                em.persist(u);
                if (client.getId() == -1) {
                    em.flush();
                    u.setId(u.getId());
                }
                result.put(Operations.ServerActions.OperationOk, u);
                System.out.println("El usuario " + client.getEmail() + " se ha registrado correctamente");
                em.getTransaction().commit();
                PersistenceUnit.closeEM();
                notifyAll();
            } else {
                result.put(Operations.ServerActions.UserAlreadyExist, client);
            }
        } catch (Exception e) {
            System.out.println("Hubo un problema al intentar registrar un usuario");
        }
        return result;
    }

    public synchronized void purchaseBook(Client client, Book book) {
        if (ClientDAO.getInstance().checkUser(client, false) && BookDAO.getInstance().checkBook(book)) {
            try {
                EntityManager em = PersistenceUnit.createEM();
                em.getTransaction().begin();
                Client c = em.merge(client);
                Book b = em.merge(book);
                client.setBalance(c.getBalance() - b.getPrice());
                c.setBalance(c.getBalance() - b.getPrice());
                em.persist(c);
                Query q = em.createNativeQuery("INSERT INTO USERBOOK (purchasedate, book_id, user_id) VALUES ( ?,?,? )");
                q.setParameter(1, LocalDateTime.now());
                q.setParameter(2, book.getId());
                q.setParameter(3, client.getId());
                q.executeUpdate();
                em.getTransaction().commit();
                PersistenceUnit.closeEM();
                notifyAll();
            } catch (Exception e) {
                System.out.println("Hubo un problema a la hora de intentar comprar un libro");
            }
        }
    }

    public synchronized Double getClientAccount(Client client) {
        double result = -1;
        if (ClientDAO.getInstance().checkUser(client, false)) {
            result = client.getBalance();
        }
        return result;
    }

    /**
     * Este método incrementa el saldo a un usuario
     *
     * @param amountToAdd saldo a incrementar
     * @return devuelve el nuevo saldo
     */
    public synchronized Double increaseAccount(User client,Double amountToAdd) {
        Double result = -1.0;
        EntityManager em = PersistenceUnit.createEM();
        em.getTransaction().begin();
        if(client instanceof Client c) {
            Client u = em.merge(c);
            u.setBalance(u.getBalance()+amountToAdd);
            c.setBalance(u.getBalance()+amountToAdd);
            result = c.getBalance();
            em.persist(u);
        }
        em.getTransaction().commit();
        PersistenceUnit.closeEM();
        return result;
    }
}
