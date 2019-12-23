package com.revolut.moneytransferapi.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.revolut.moneytransferapi.model.Client;

public interface ClientDao {

    List<Client> getClients();

    Optional<Client> getClient(UUID clientId);

}
