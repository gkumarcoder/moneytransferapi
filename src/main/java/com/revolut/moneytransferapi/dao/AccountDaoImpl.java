package com.revolut.moneytransferapi.dao;

import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.controller.RouterImpl;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountDaoImpl implements AccountDao {

	private static final Logger log = LoggerFactory.getLogger(RouterImpl.class);
	
    private final Provider<EntityManager> em;

    @Inject
    public AccountDaoImpl(Provider<EntityManager> em) {
        this.em = em;
    }

    @Override
    public List<Account> getAccounts(UUID clientId) {
        List<Account> accounts = em.get().createQuery("select a from account a where a._client.id = :clientId", Account.class)
                .setParameter("clientId", clientId)
                .getResultList();

        return accounts;
    }

    @Override
    public Optional<Account> getAccount(UUID clientId, Long accountId) {
        TypedQuery<Account> query = em.get().createQuery(
                "select a from account a where a._client.id = :clientId and a.id = :accountId", Account.class)
                .setParameter("clientId", clientId)
                .setParameter("accountId", accountId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            log.warn("No account with id: {}", accountId);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccount(Long accountId) {
        TypedQuery<Account> query = em.get().createQuery(
                "select a from account a where a.id = :accountId", Account.class)
                .setParameter("accountId", accountId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            log.warn("No account with id: {}", accountId);
        }

        return Optional.empty();
    }

    @Transactional
    @Override
    public void update(Account... accounts) {
        Arrays.asList(accounts).forEach(em.get()::merge);
    }


}
