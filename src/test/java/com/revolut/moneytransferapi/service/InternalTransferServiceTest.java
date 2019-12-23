package com.revolut.moneytransferapi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revolut.moneytransferapi.dao.AccountDao;
import com.revolut.moneytransferapi.dto.TransferRequest;
import com.revolut.moneytransferapi.exception.AccountNotFoundException;
import com.revolut.moneytransferapi.exception.InsufficientFundsException;
import com.revolut.moneytransferapi.exception.InvalidAmountValueException;
import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.service.InternalTransferService;

import java.math.BigDecimal;
import java.util.Optional;

import static com.revolut.moneytransferapi.DataGenerator.AccountBuilder.anAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InternalTransferServiceTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private InternalTransferService underTest;

    @Test
    public void transfersMoneyBetweenTwoAccounts() {
        // given
        Account fromAccount = anAccount()
                .withId(0L)
                .withBalance(new BigDecimal("900.00"))
                .build();
        Account toAccount = anAccount()
                .withId(1L)
                .withBalance(new BigDecimal("600.00"))
                .build();

        TransferRequest request = new TransferRequest();
        request.setFromAccount(0L);
        request.setToAccount(1L);
        request.setAmount(200);
        when(accountDao.getAccount(0L)).thenReturn(Optional.of(fromAccount));
        when(accountDao.getAccount(1L)).thenReturn(Optional.of(toAccount));

        // when
        underTest.transfer(request);

        // then
        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("700.00"));
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("800.00"));
    }

    @Test(expected = AccountNotFoundException.class)
    public void throwsExceptionWhenFromAccountNotFound() {
        // given
        long nonExistingAccountId = 0L;
        TransferRequest request = new TransferRequest();
        request.setFromAccount(nonExistingAccountId);
        request.setAmount(1F);

        // when
        underTest.transfer(request);
    }


    @Test(expected = AccountNotFoundException.class)
    public void throwsExceptionWhenToAccountNotFound() {
        // given
        long nonExistingAccountId = 0L;
        TransferRequest request = new TransferRequest();
        request.setToAccount(nonExistingAccountId);
        request.setAmount(1F);

        // when
        underTest.transfer(request);
    }

    @Test(expected = InvalidAmountValueException.class)
    public void throwsExceptionWhenAmountNotHigherThanZero() {
        // given
        TransferRequest request = new TransferRequest();
        request.setAmount(0);

        // when
        underTest.transfer(request);
    }

    @Test(expected = InsufficientFundsException.class)
    public void throwsExceptionWhenInsufficientFunds() {
        // given
        TransferRequest request = new TransferRequest();
        request.setToAccount(0L);
        request.setFromAccount(1L);
        request.setAmount(1000);
        Account account = anAccount()
                .withBalance(new BigDecimal("900.00"))
                .build();
        when(accountDao.getAccount(anyLong())).thenReturn(Optional.of(account));

        // when
        underTest.transfer(request);
    }

}