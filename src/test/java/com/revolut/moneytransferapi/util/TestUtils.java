package com.revolut.moneytransferapi.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.model.Client;

public class TestUtils {

    public static Client getClientByFirstAndLastName(List<Client> clients, String firstName, String lastName) {
        return clients.stream()
                .filter(client -> firstName.equals(client.getFirstName()) && lastName.equals(client.getLastName()))
                .findFirst()
                .orElse(null);
    }

    public static UUID extractClientId(Client client) {
        return client.getId();
    }

    public static Account extractAccount(Client client) {
        return client.getAccounts().stream().findFirst().orElse(null);
    }

    public static BigDecimal toBigDecimal(String balance) {
        return new BigDecimal(balance);
    }
}
