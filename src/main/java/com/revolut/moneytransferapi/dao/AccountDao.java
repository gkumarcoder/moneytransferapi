package com.revolut.moneytransferapi.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.revolut.moneytransferapi.model.Account;

public interface AccountDao {

    List<Account> getAccounts(UUID clientId);

    Optional<Account> getAccount(UUID clientId, Long accountId);

    Optional<Account> getAccount(Long accountId);

    void update(Account... accounts);

}
