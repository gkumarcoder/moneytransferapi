package com.revolut.moneytransferapi.service;

import javax.inject.Inject;

import com.revolut.moneytransferapi.dao.AccountDao;
import com.revolut.moneytransferapi.dao.ClientDao;
import com.revolut.moneytransferapi.exception.AccountNotFoundException;
import com.revolut.moneytransferapi.exception.ClientNotFoundException;
import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.model.Client;

import java.util.List;
import java.util.UUID;

public class ClientAccountServiceImpl implements ClientAccountService {

    private final AccountDao accountDao;
    private final ClientDao clientDao;

    @Inject
    private ClientAccountServiceImpl(AccountDao accountDao, ClientDao clientDao) {
        this.accountDao = accountDao;
        this.clientDao = clientDao;
    }

    @Override
    public List<Client> getClients() {
        return clientDao.getClients();
    }

    @Override
    public Client getClient(UUID clientId) {
        return clientDao.getClient(clientId).orElseThrow(() ->
                new ClientNotFoundException(String.format("Client with id: %s cannot be found", clientId)));

    }

    @Override
    public List<Account> getAccounts(UUID clientId) {
        return accountDao.getAccounts(clientId);
    }

    @Override
    public Account getAccount(UUID clientId, Long accountId) {
        return accountDao.getAccount(clientId, accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with id: %s cannot be found", accountId)));
    }

}
