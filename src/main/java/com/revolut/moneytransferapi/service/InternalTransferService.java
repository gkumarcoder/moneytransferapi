package com.revolut.moneytransferapi.service;

import com.revolut.moneytransferapi.dao.AccountDao;
import com.revolut.moneytransferapi.dto.TransferRequest;
import com.revolut.moneytransferapi.exception.AccountNotFoundException;
import com.revolut.moneytransferapi.exception.InvalidAmountValueException;
import com.revolut.moneytransferapi.model.Account;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class InternalTransferService implements TransferService {

    private static final Random rnd = new Random();
    private static final long FIXED_DELAY = 1;
    private static final long RANDOM_DELAY = 2;
    private static final long TIMEOUT = TimeUnit.SECONDS.toNanos(2);

    private final AccountDao accountDao;

    @Inject
    public InternalTransferService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public boolean transfer(TransferRequest transferRequest) {
        validateAmount(transferRequest.getAmount());

        BigDecimal amount = BigDecimal.valueOf(transferRequest.getAmount());

        long stopTime = System.nanoTime() + TIMEOUT;

        while (true) {
            Account fromAccount, toAccount;

            synchronized (this) {
                fromAccount = accountDao.getAccount(transferRequest.getFromAccount())
                        .orElseThrow(() -> new AccountNotFoundException(
                                format("Account with id %d (fromAccount) cannot be found", transferRequest.getFromAccount())));

                toAccount = accountDao.getAccount(transferRequest.getToAccount())
                        .orElseThrow(() -> new AccountNotFoundException(
                                format("Account with id %d (toAccount) cannot be found", transferRequest.getToAccount())));

            }

            if (fromAccount._lock.tryLock()) {
                try {
                    if (toAccount._lock.tryLock()) {
                        try {
                            fromAccount.withdraw(amount);
                            toAccount.deposit(amount);

                            accountDao.update(fromAccount, toAccount);

                            return true;
                        } finally {
                            toAccount._lock.unlock();
                        }
                    }
                } finally {
                    fromAccount._lock.unlock();
                }
            }
            if (System.nanoTime() > stopTime) {
                return false;
            }
            try {
                TimeUnit.NANOSECONDS.sleep(FIXED_DELAY + rnd.nextLong() % RANDOM_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    private void validateAmount(float amount) {
        if (amount <= 0) {
            throw new InvalidAmountValueException("Amount value has to be higher than 0");
        }
    }

}
