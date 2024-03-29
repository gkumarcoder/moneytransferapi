package com.revolut.moneytransferapi.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.revolut.moneytransferapi.model.Client;

import java.util.ArrayList;
import java.util.List;

public class AbstractDaoIntegrationTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static List<Client> clients = new ArrayList<>();

    public static void init() {
        emf = Persistence.createEntityManagerFactory("mtransfer");
        em = emf.createEntityManager();
    }

    public static void tearDown() {
        em.clear();
        em.close();
        emf.close();
    }

    public static EntityManager getEm() {
        return em;
    }

    protected List<Client> storeClients(List<Client> toSave) {
        EntityTransaction tx = getEm().getTransaction();
        tx.begin();
        toSave.forEach(getEm()::persist);
        tx.commit();

        clients.addAll(toSave);

        return toSave;
    }

    protected void cleanDB() {
        EntityTransaction tx = getEm().getTransaction();
        tx.begin();
        clients.stream().map(Client::getId).forEach(
                id -> getEm().remove(getEm().find(Client.class, id))
        );
        clients.clear();
        tx.commit();
    }

}
