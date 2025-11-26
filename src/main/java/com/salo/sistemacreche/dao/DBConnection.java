package com.salo.sistemacreche.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBConnection {
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();

    static {
        try {
            emf = Persistence.createEntityManagerFactory("crecheDB");
            System.out.println("EntityManagerFactory criado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao criar EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();

        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
            threadLocal.set(em);
            System.out.println("Novo EntityManager criado para a thread");
        }

        return em;
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null && em.isOpen()) {
            em.close();
            threadLocal.remove();
            System.out.println("EntityManager fechado");
        }
    }

    public static void closeConnection() {
        closeEntityManager(); // Fecha o EntityManager da thread atual
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory fechado");
        }
    }

    // Métodos de transação para facilitar
    public static void beginTransaction() {
        EntityManager em = getEntityManager();
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    public static void commitTransaction() {
        EntityManager em = getEntityManager();
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    public static void rollbackTransaction() {
        EntityManager em = getEntityManager();
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}