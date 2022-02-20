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
    /**
     * Este método revisa si un usuario está registrada en la aplicación o no a través del email
     *
     * @param u usuario a revisar
     * @return true si son correctas las credenciales, false si no se encuentra en la BD ó no coinciden las crendenciales
     */
    public static synchronized boolean checkUser(User u, boolean login) {
        EntityManager em = PersistenceUnit.createEM();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("SELECT ID, EMAIL, PASSWORD, BALANCE, ISMANAGER FROM _USER WHERE _USER.EMAIL=?");
        q.setParameter(1, u.getEmail());
        try {
            Object[] columns = (Object[]) q.getSingleResult();
            if (u.getPassword().equals(columns[2])) {
                if (u.isManager() == ((Byte) columns[4] != 0)) {
                    u.setId(((BigInteger) columns[0]).longValue());
                    if(u instanceof Client c)
                        c.setBalance((Double)columns[3]);
                    return true;
                } else {
                    u.setId(null);
                    if(login)
                        System.out.println(Tools.ANSI_YELLOW+"Un usuario intentó iniciar sesión como " + (u.isManager() ? "Manager" : "Cliente") + ", pero su tipo en la BD es " + (((Byte) columns[4] != 0) ? "Manager" : "Cliente")+Tools.ANSI_RESET);
                    return false;
                }
            } else {
                if(login)
                    System.out.println("El usuario " + u.getEmail() + " intentó iniciar sesión pero su contraseña era distinta a la guardada");
                return false;
            }
        } catch (NoResultException e) {
            if(login)
                System.out.println("El usuario " + u.getEmail() + " intentó iniciar sesión pero no está registrado");
            u.setId(-2L);
            return false;
        } finally {
            PersistenceUnit.closeEM();
        }
    }

    /**
     * Este método registra tanto usuarios clientes como managers
     *
     * @param client cliente a registrar
     * @return clave valor-> operacion ok, usuario con id registrado | usuario ya existe, cliente sin registrar
     */
    public static synchronized LinkedHashMap<Operations.ServerActions, Object> registerUser(User client) {
        LinkedHashMap<Operations.ServerActions, Object> result = new LinkedHashMap<>();
        if (!checkUser(client, false)) {
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
        } else {
            result.put(Operations.ServerActions.UserAlreadyExist, client);
        }
        return result;
    }

    public static synchronized void purchaseBook(Client client, Book book){
        if(checkUser(client, false) && BookDAO.checkBook(book)){
            EntityManager em = PersistenceUnit.createEM();
            em.getTransaction().begin();
            Client c = em.merge(client);
            Book b = em.merge(book);
            c.setBalance(c.getBalance()-b.getPrice());
            em.persist(c);
            Query q = em.createNativeQuery("INSERT INTO USERBOOK (purchasedate, book_id, user_id) VALUES ( ?,?,? )");
            q.setParameter(1, LocalDateTime.now());
            q.setParameter(2, book.getId());
            q.setParameter(3, client.getId());
            q.executeUpdate();
            em.getTransaction().commit();
            PersistenceUnit.closeEM();
        }
    }
}
