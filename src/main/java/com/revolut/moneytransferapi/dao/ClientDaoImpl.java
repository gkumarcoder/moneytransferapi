package com.revolut.moneytransferapi.dao;

import com.revolut.moneytransferapi.model.Client;

import com.google.inject.Provider;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientDaoImpl implements ClientDao {

	private static final Logger log = LoggerFactory.getLogger(ClientDaoImpl.class);
	
    private final Provider<EntityManager> em;

    @Inject
    public ClientDaoImpl(Provider<EntityManager> em) {
        this.em = em;
    }

    @Override
    public List<Client> getClients() {
        TypedQuery<Client> query = em.get().createQuery("select c from client c", Client.class);

        return query.getResultList();
    }

    @Override
    public Optional<Client> getClient(UUID id) {
        TypedQuery<Client> query = em.get()
                .createQuery("select c from client c where c.id = :id", Client.class)
                .setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            log.warn("No client with id: {}", id);
        }

        return Optional.empty();
    }
}
