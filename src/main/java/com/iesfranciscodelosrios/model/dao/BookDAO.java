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
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@MappedSuperclass
public class BookDAO {

    public static synchronized List<Book> getAllOnStockBooks() {
        List<Book> result = new ArrayList<>();
        EntityManager em = PersistenceUnit.createEM();
        em.getTransaction().begin();
        try {
            TypedQuery<Book> q = em.createNamedQuery("getStockedBooks", Book.class);
            result = new ArrayList<>(q.getResultList());
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println(Tools.ANSI_RED + "No hay libros disponibles para su venta en la base de datos, están todos agotados" + Tools.ANSI_RESET);
        } finally {
            PersistenceUnit.closeEM();
        }
        return result;
    }

    public static synchronized LinkedHashMap<Operations.ServerActions, Object> registerBook(Book b) {
        LinkedHashMap<Operations.ServerActions, Object> result = new LinkedHashMap<>();
        EntityManager em = PersistenceUnit.createEM();
        em.getTransaction().begin();
        Book book = em.merge(b);
        em.persist(book);
        if (book.getId() == -1) {
            em.flush();
            book.setId(book.getId());
        }
        result.put(Operations.ServerActions.OperationOk, book);
        System.out.println("El libro " + book.getTitle() + " se ha registrado correctamente");
        em.getTransaction().commit();
        PersistenceUnit.closeEM();
        return result;
    }
}
