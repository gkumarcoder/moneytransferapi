package com.revolut.moneytransferapi.controller;

import com.revolut.moneytransferapi.dto.TransferRequest;
import com.revolut.moneytransferapi.exception.AccountNotFoundException;
import com.revolut.moneytransferapi.exception.ClientNotFoundException;
import com.revolut.moneytransferapi.exception.InsufficientFundsException;
import com.revolut.moneytransferapi.exception.InvalidAmountValueException;
import com.revolut.moneytransferapi.service.ClientAccountService;
import com.revolut.moneytransferapi.service.TransferService;
import com.google.gson.Gson;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;
import static spark.Spark.*;


public class RouterImpl implements Router {

	private static final Logger log = LoggerFactory.getLogger(RouterImpl.class);
	
    private static final String EMPTY = "";
    private static final String SUCCESSFUL_TRANSFER_BODY = "{\"transferStatus\": \"OK\"}";
    private static final String FAILED_TRANSFER_BODY = "{\"transferStatus\": \"FAILED\"}";
    private static final String EXCEPTION_BODY = "{\"statusCode\": %d, \"message\": \"%s\"}";

    private final Gson gson;
    private final ClientAccountService clientAccountService;
    private final TransferService transferService;

    @Inject
    public RouterImpl(Gson gson, ClientAccountService clientAccountService, TransferService transferService) {
        this.gson = gson;
        this.clientAccountService = clientAccountService;
        this.transferService = transferService;
    }

    @Override
    public void start() {
        path("/api", () -> {
            before("/*", (req, res) -> {
                log.info("Received {} request to endpoint: {}", req.requestMethod(), req.uri());
                res.type("application/json");
            });

            path("/clients", () -> {

                get(EMPTY, (req, res) -> gson.toJsonTree(clientAccountService.getClients()));

                path("/:clientId", () -> {

                    get(EMPTY, (req, res) -> {
                        UUID clientId = UUID.fromString(req.params("clientId"));
                        return gson.toJsonTree(clientAccountService.getClient(clientId));
                    });

                    path("/accounts", () -> {

                        get(EMPTY, (req, res) -> {
                            UUID clientId = UUID.fromString(req.params("clientId"));
                            return gson.toJsonTree(clientAccountService.getAccounts(clientId));
                        });

                        get("/:accountId", (req, res) -> {
                            UUID clientId = UUID.fromString(req.params("clientId"));
                            Long accountId = Long.valueOf(req.params("accountId"));
                            return gson.toJsonTree(clientAccountService.getAccount(clientId, accountId));
                        });
                    });
                });
            });

            post("/transfer", (req, res) -> {
                TransferRequest transferRequest = gson.fromJson(req.body(), TransferRequest.class);
                return transferService.transfer(transferRequest) ? SUCCESSFUL_TRANSFER_BODY : FAILED_TRANSFER_BODY;
            });

            exception(ClientNotFoundException.class, (ex, req, res) -> {
                res.status(404);
                res.body(String.format(EXCEPTION_BODY, 404, ex.getMessage()));
            });

            exception(AccountNotFoundException.class, (ex, req, res) -> {
                int statusCode = "post".equalsIgnoreCase(req.requestMethod()) ? 400 : 404;
                res.status(statusCode);
                res.body(String.format(EXCEPTION_BODY, statusCode, ex.getMessage()));
            });

            exception(InsufficientFundsException.class, (ex, req, res) -> {
                res.status(400);
                res.body(String.format(EXCEPTION_BODY, 400, ex.getMessage()));
            });

            exception(InvalidAmountValueException.class, (ex, req, res) -> {
                res.status(400);
                res.body(String.format(EXCEPTION_BODY, 400, ex.getMessage()));
            });
        });
    }

}
