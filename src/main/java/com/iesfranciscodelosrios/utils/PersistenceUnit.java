package com.iesfranciscodelosrios.utils;

import javax.persistence.*;

public class PersistenceUnit {

    private static EntityManagerFactory emf = null;
    private static EntityManager manager = null;

    /**
     * Solo se llama una vez para conectar con la base de datos
     */
    public static void init() {
        try {
            emf = Persistence.createEntityManagerFactory("ApplicationH2");
        } catch (PersistenceException e) {
            System.out.println("Sin nombre de proveedor de persistencia para EntityManager con H2");
        }
    }

    /**
     * Este método es para crear unidades de persistencia
     *
     * @return entitymanager para la unidad de persistencia
     */
    public synchronized static EntityManager createEM() {
        if (emf == null) {
            try {
                emf = Persistence.createEntityManagerFactory("ApplicationH2");
            } catch (PersistenceException e) {
                System.out.println("Sin nombre de proveedor de persistencia para EntityManager con H2");
            }
        }
        if (manager == null)
            manager = emf.createEntityManager();
        return manager;
    }

    /**
     * Cerramos la conexión con la base de datos
     */
    private synchronized static void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
            emf = null;
        }
    }

    /**
     * Cerramos el EntityManager
     */
    public synchronized static void closeEM() {
        if (manager != null) {
            manager.close();
            manager = null;
        }
    }

    public static void closeAllConnections() {
        closeEM();
        closeEntityManagerFactory();
    }

}
