package com.iesfranciscodelosrios.model.dao;

import com.iesfranciscodelosrios.model.Book;
import com.iesfranciscodelosrios.utils.PersistenceUnit;
import com.iesfranciscodelosrios.utils.Tools;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public class BookDAO {

    public static synchronized List<Book> getAllOnStockBooks(){
        List<Book> result = new ArrayList<>();
        EntityManager em = PersistenceUnit.createEM();
        em.getTransaction().begin();
        try{
            TypedQuery<Book> q = em.createNamedQuery("getStockedBooks", Book.class);
            result = new ArrayList<>(q.getResultList());
            em.getTransaction().commit();
        }catch (NoResultException e){
            System.out.println(Tools.ANSI_RED+"No hay libros disponibles para su venta en la base de datos, están todos agotados"+Tools.ANSI_RESET);
        } finally {
            PersistenceUnit.closeEM();
        }
        return result;
    }
}
