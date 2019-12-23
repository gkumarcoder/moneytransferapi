package com.revolut.moneytransferapi.service;

import com.revolut.moneytransferapi.dao.AccountDao;
import com.revolut.moneytransferapi.dao.ClientDao;
import com.revolut.moneytransferapi.exception.AccountNotFoundException;
import com.revolut.moneytransferapi.exception.ClientNotFoundException;
import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.model.Client;
import com.revolut.moneytransferapi.service.ClientAccountServiceImpl;
import com.google.common.collect.ImmutableList;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.revolut.moneytransferapi.DataGenerator.AccountBuilder.anAccount;
import static com.revolut.moneytransferapi.DataGenerator.ClientBuilder.aClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientAccountServiceImplTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private ClientDao clientDao;

    @InjectMocks
    private ClientAccountServiceImpl underTest;

    @Test
    public void returnsAllClients() {
        // given
        Client c1 = aClient()
                .withId(UUID.randomUUID())
                .withFirstName("Abc")
                .withLastName("Qaz")
                .build();
        Client c2 = aClient()
                .withId(UUID.randomUUID())
                .withFirstName("Zxc")
                .withLastName("Qwe")
                .build();
        when(clientDao.getClients()).thenReturn(Lists.newArrayList(c1, c2));

        // when
        List<Client> actual = underTest.getClients();

        // then
        assertThat(actual).containsExactlyInAnyOrder(c1, c2);
    }

    @Test
    public void returnsEmptyListWhenNoClients() {
        // given
        when(clientDao.getClients()).thenReturn(Lists.newArrayList());

        // when
        List<Client> actual = underTest.getClients();

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void returnsSingleClient() {
        // given
        Client c1 = aClient()
                .withId(UUID.randomUUID())
                .withFirstName("Abc")
                .withLastName("Qaz")
                .build();
        when(clientDao.getClient(c1.getId())).thenReturn(Optional.of(c1));

        // when
        Client actual = underTest.getClient(c1.getId());

        // then
        assertThat(actual).isEqualTo(c1);
    }

    @Test(expected = ClientNotFoundException.class)
    public void throwsExceptionWhenClientNotFound() {
        // given
        when(clientDao.getClient(any(UUID.class))).thenReturn(Optional.empty());

        // when
        underTest.getClient(UUID.randomUUID());
    }

    @Test
    public void returnsEmptyAccountListWhenNoAccountsForGivenClient() {
        // given
        UUID clientId = UUID.randomUUID();
        when(accountDao.getAccounts(clientId)).thenReturn(Collections.emptyList());

        // when
        List<Account> actual = underTest.getAccounts(clientId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void returnsAllAccountsForGivenClient() {
        // given
        UUID clientId = UUID.randomUUID();
        Account a1 = anAccount()
                .withId(1L)
                .withBalance(BigDecimal.ZERO)
                .build();
        Account a2 = anAccount()
                .withId(2L)
                .withBalance(BigDecimal.TEN)
                .build();
        List<Account> accounts = ImmutableList.of(a1, a2);
        when(accountDao.getAccounts(clientId)).thenReturn(accounts);

        // when
        List<Account> actual = underTest.getAccounts(clientId);

        // then
        assertThat(actual).containsExactlyInAnyOrder(a1, a2);
    }

    @Test
    public void returnsSingleAccountForGivenClient() {
        // given
        UUID clientId = UUID.randomUUID();
        long accountId = 1L;
        Account a = anAccount()
                .withId(accountId)
                .withBalance(BigDecimal.TEN)
                .build();
        when(accountDao.getAccount(clientId, 1L)).thenReturn(Optional.of(a));

        // when
        Account actual = underTest.getAccount(clientId, accountId);

        // then
        assertThat(actual).isEqualTo(a);
    }

    @Test(expected = AccountNotFoundException.class)
    public void throwsExceptionWhenAccountNotFoundForGivenUser() {
        // given
        long nonExistingAccountId = 1L;
        UUID clientId = UUID.randomUUID();
        when(accountDao.getAccount(clientId, nonExistingAccountId)).thenReturn(Optional.empty());

        // when
        underTest.getAccount(clientId, nonExistingAccountId);
    }
}
