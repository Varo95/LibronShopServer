package com.iesfranciscodelosrios.model.dao;

import com.iesfranciscodelosrios.model.Book;
import com.iesfranciscodelosrios.utils.Operations;
import com.iesfranciscodelosrios.utils.PersistenceUnit;
import com.iesfranciscodelosrios.utils.Tools;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@MappedSuperclass
public class BookDAO {

    private static BookDAO bookDAO = getInstance();

    public static BookDAO getInstance() {
        if (bookDAO == null)
            bookDAO = new BookDAO();
        return bookDAO;
    }

    @Transient
    public synchronized List<Book> getAllOnStockBooks() {
        List<Book> result = new ArrayList<>();
        try {
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
                notifyAll();
            }
        } catch (Exception e) {
            System.out.println("Hubo un error en getAllOnStockBooks");
        }
        return result;
    }

    @Transient
    public synchronized LinkedHashMap<Operations.ServerActions, Object> registerBook(Book b) {
        LinkedHashMap<Operations.ServerActions, Object> result = new LinkedHashMap<>();
        try {
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
            notifyAll();
        }catch (Exception e){
            System.out.println("Hubo un error al registrar un libro en la BD");
        }
        return result;
    }

    @Transient
    public synchronized boolean checkBook(Book b) {
        return BookDAO.getInstance().getAllOnStockBooks().contains(b);
    }

    @Transient
    public synchronized List<Book> getAllBooks(){
        List<Book> result = new ArrayList<>();
        EntityManager em = PersistenceUnit.createEM();
        em.getTransaction().begin();
        TypedQuery<Book> query = em.createNamedQuery("getAllBookOrder", Book.class);
        result.addAll(query.getResultList());
        em.getTransaction().commit();
        PersistenceUnit.closeEM();
        notifyAll();
        return result;
    }

    @Transient
    public synchronized boolean changeStock(Book book){
        boolean result = false;
        EntityManager em = PersistenceUnit.createEM();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("UPDATE BOOK SET STOCK=? WHERE ID=?");
        q.setParameter(1, book.isStock());
        q.setParameter(2, book.getId());
        q.executeUpdate();
        result = true;
        em.getTransaction().commit();
        PersistenceUnit.closeEM();
        notifyAll();
        return result;
    }
}
