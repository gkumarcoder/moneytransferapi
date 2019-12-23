package com.revolut.moneytransferapi.dao;

import org.assertj.core.util.Lists;
import org.junit.*;

import com.revolut.moneytransferapi.dao.AccountDao;
import com.revolut.moneytransferapi.dao.AccountDaoImpl;
import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.model.Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.revolut.moneytransferapi.DataGenerator.AccountBuilder.anAccount;
import static com.revolut.moneytransferapi.DataGenerator.ClientBuilder.aClient;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountDaoImplIntegrationTest extends AbstractDaoIntegrationTest {

    private AccountDao underTest;

    @BeforeClass
    public static void init() {
        AbstractDaoIntegrationTest.init();
    }

    @AfterClass
    public static void tearDown() {
        AbstractDaoIntegrationTest.tearDown();
    }

    @Before
    public void setUp() {
        underTest = new AccountDaoImpl(AbstractDaoIntegrationTest::getEm);
    }

    @After
    public void clearDB() {
        cleanDB();
    }

    @Test
    public void returnsAllAccountsForGivenClient() {
        // given
        Account a1 = anAccount().withBalance(BigDecimal.ZERO).build();
        Account a2 = anAccount().withBalance(BigDecimal.TEN).build();
        Client c = aClient()
                .withFirstName("Abc")
                .withLastName("Zxc")
                .withAccounts(a1, a2)
                .build();
        storeClients(Lists.newArrayList(c));

        // when
        List<Account> actual = underTest.getAccounts(c.getId());

        // then
        assertThat(actual).containsExactlyInAnyOrder(a1, a2);
    }

    @Test
    public void returnsEmptyListWhenNoAccountsForGivenClient() {
        // when
        List<Account> accounts = underTest.getAccounts(UUID.randomUUID());

        // then
        assertThat(accounts).isEmpty();
    }

    @Test
    public void returnsOptionalWithSingleAccountForGivenClient() {
        // given
        Account a = anAccount()
                .withBalance(BigDecimal.ZERO)
                .build();
        Client c = aClient()
                .withFirstName("Abc")
                .withLastName("Zxc")
                .withAccounts(a)
                .build();
        storeClients(Lists.newArrayList(c));

        // when
        Optional<Account> account = underTest.getAccount(c.getId(), a.getId());

        assertThat(account.get()).isEqualTo(a);
    }

    @Test
    public void returnsEmptyOptionalWhenAccountNotFound() {
        // given
        long nonExistingAccountId = 0L;
        UUID clientId = UUID.randomUUID();

        // when
        Optional<Account> actual = underTest.getAccount(clientId, nonExistingAccountId);

        // then
        assertThat(actual.isPresent()).isFalse();
    }
}