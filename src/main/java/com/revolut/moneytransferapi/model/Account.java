package com.revolut.moneytransferapi.model;


import javax.persistence.*;

import com.revolut.moneytransferapi.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Entity(name = "account")
public class Account {

    public Lock get_lock() {
		return _lock;
	}

	public void set_lock(Lock _lock) {
		this._lock = _lock;
	}

	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Client get_client() {
		return _client;
	}

	public void set_client(Client _client) {
		this._client = _client;
	}

	@Transient
    public Lock _lock = new ReentrantLock();

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    @SequenceGenerator(name = "seq-gen",
            sequenceName = "user_sequence", initialValue = 296896261)
    private Long id;

    @Column
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "fk_client", updatable = false, nullable = false)
    private Client _client;

    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(String.format("Insufficient funds to perform withdraw from account: %d", id));
        }
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}