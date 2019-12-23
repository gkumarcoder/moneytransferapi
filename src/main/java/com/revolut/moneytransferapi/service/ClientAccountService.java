package com.revolut.moneytransferapi.service;

import java.util.List;
import java.util.UUID;

import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.model.Client;

public interface ClientAccountService {

    List<Client> getClients();

    Client getClient(UUID clientId);

    List<Account> getAccounts(UUID clientId);

    Account getAccount(UUID clientId, Long accountId);

}
