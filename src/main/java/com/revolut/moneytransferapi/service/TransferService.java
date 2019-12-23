package com.revolut.moneytransferapi.service;

import com.revolut.moneytransferapi.dto.TransferRequest;

public interface TransferService {

    boolean transfer(TransferRequest transferRequest);
}
