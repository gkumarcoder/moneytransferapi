package com.revolut.moneytransferapi;

import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.model.Client;
import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import static com.revolut.moneytransferapi.DataGenerator.AccountBuilder.anAccount;
import static com.revolut.moneytransferapi.DataGenerator.ClientBuilder.aClient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DataGenerator {

    @Inject
    public DataGenerator(EntityManager em) {
        init(em);
    }

    @Transactional
    void init(EntityManager em) {
        em.persist(aClient()
                .withFirstName("Bala")
                .withLastName("Kumar")
                .withAccounts(
                        anAccount()
                                .withBalance(BigDecimal.TEN)
                                .build()
                ).build()
        );
        em.persist(aClient()
                .withFirstName("Ankit")
                .withLastName("Jha")
                .withAccounts(
                        anAccount()
                                .withBalance(BigDecimal.valueOf(10000))
                                .build()
                ).build()
        );
        em.persist(aClient()
                .withFirstName("Amith")
                .withLastName("Jha")
                .withAccounts(
                        anAccount()
                                .withBalance(BigDecimal.ZERO)
                                .build(),
                        anAccount()
                                .withBalance(BigDecimal.valueOf(100000))
                                .build()
                ).build()
        );
    }

    public static class ClientBuilder {

        private UUID id;
        private String firstName;
        private String lastName;
        private List<Account> accounts = new ArrayList<Account>();

        private ClientBuilder() {
        }

        public static ClientBuilder aClient() {
            return new ClientBuilder();
        }

        public ClientBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public ClientBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ClientBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ClientBuilder withAccounts(Account... accounts) {
            this.accounts.addAll(Arrays.asList(accounts));
            return this;
        }

        public Client build() {
            Client client = new Client();

            client.setId(id);
            client.setFirstName(firstName);
            client.setLastName(lastName);
            accounts.forEach(client::addAccount);

            return client;
        }
    }

    public static class AccountBuilder {

        private Long id;
        private BigDecimal balance;

        private AccountBuilder() {
        }

        public static AccountBuilder anAccount() {
            return new AccountBuilder();
        }

        public AccountBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AccountBuilder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Account build() {
            Account account = new Account();

            account.setId(id);
            account.setBalance(balance);

            return account;
        }

    }
}