package com.revolut.moneytransferapi.controller;

import com.revolut.moneytransferapi.dao.AbstractDaoIntegrationTest;
import com.revolut.moneytransferapi.model.Account;
import com.revolut.moneytransferapi.model.Client;
import com.google.gson.JsonObject;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.revolut.moneytransferapi.DataGenerator.ClientBuilder.aClient;
import static com.revolut.moneytransferapi.util.TestUtils.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class StepDefinitions extends AbstractDaoIntegrationTest {

    private static final String RANDOM_CLIENT_ID = "be8bdd99-d68e-48f9-89f4-ad2b066b4ae2";
    private static final Long   RANDOM_ACCOUNT_ID = 236436253L;
    private static final String ANKIT_JHA_ID_PLACEHOLDER = "<AnkitJhaId>";
    private static final String NON_EXISTING_ID_PLACEHOLDER = "<NonExistingId>";
    private static final String ANKIT_JHA_ACCOUNT_PLACEHOLDER = "<AnkitJhaAccount>";
    private static final String ANKIT_JHA_ACCOUNT_ID_PLACEHOLDER = "<AnkitJhaAccountId>";
    private static final String AMITH_JHA_ACCOUNT_PLACEHOLDER = "<AmithJhaAccount>";
    private static final String AMITH_JHA_ACCOUNT_ID_PLACEHOLDER = "<AmithJhaAccountId>";
    private static final String NON_EXISTING_ACCOUNT_ID_PLACEHOLDER = "<NonExistingAccountId>";

    static {
        RestAssured.baseURI = "http://localhost:4567";
        RestAssured.basePath = "/api";
    }

    private Response response;
    private ValidatableResponse json;

    private List<Client> savedClients;
    private List<Client> clients;

    @After
    public void clearDB() {
        cleanDB();
    }

    @Given("^system contains following clients$")
    public void setupClients(DataTable inputData) {
        clients = inputData.asMaps(String.class, String.class).stream()
                .map(row -> aClient()
                        .withFirstName(row.get("firstName"))
                        .withLastName(row.get("lastName"))
                        .build())
                .collect(Collectors.toList());
    }

    @Given("^clients have following accounts$")
    public void setupAccounts(DataTable inputData) {
        inputData.asMaps(String.class, String.class)
                .forEach(row -> {
                    Client client = getClientByFirstAndLastName(clients, row.get("firstName"), row.get("lastName"));
                    Account account = new Account();
                    account.setBalance(toBigDecimal(row.get("accountBalance")));
                    client.addAccount(account);
                });
    }

    @When("^client requests GET (.*)$")
    public void doGetRequest(String path) {
        savedClients = storeClients(clients);
        response = get(resolvePlaceholders(path));
    }

    @When("^client requests POST /transfer with following body$")
    public void doPostRequest(DataTable inputData) {
        Map<String, String> input = inputData.asMap(String.class, String.class);
        savedClients = storeClients(clients);
        JsonObject json = new JsonObject();
        json.addProperty("fromAccount", resolvePlaceholder(input.get("fromAccount"), Long.class));
        json.addProperty("toAccount", resolvePlaceholder(input.get("toAccount"), Long.class));
        json.addProperty("amount", Float.valueOf(input.get("amount")));

        response = given().body(json).post("/transfer");
    }

    @Then("^response status is (\\d+)$")
    public void checkResponseStatus(int status) {
        json = response.then().statusCode(status);
    }

    @Then("^response contains (\\d+) clients$")
    public void checkResponseSize(int expectedSize) {
        json.body("$", hasSize(expectedSize));
    }

    @Then("^response includes the following clients$")
    public void checkResponseMultipleClients(DataTable expectedData) {
        expectedData.asMaps(String.class, String.class)
                .forEach(expected -> {
                    String firstName = expected.get("firstName");
                    String lastName = expected.get("lastName");
                    json.root(format("[%s]", expected.get("order")))
                            .body("id", equalTo(extractClientId(
                                    getClientByFirstAndLastName(savedClients, firstName, lastName)).toString()))
                            .body("firstName", equalTo(firstName))
                            .body("lastName", equalTo(lastName))
                            .body("accounts", hasSize(0));
                });
    }

    @Then("^response includes the following client$")
    public void checkResponseSingleClient(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);

        String firstName = expected.get("firstName");
        String lastName = expected.get("lastName");

        json.body("id", equalTo(extractClientId(
                getClientByFirstAndLastName(savedClients, firstName, lastName)).toString()))
                .body("firstName", equalTo(firstName))
                .body("lastName", equalTo(lastName))
                .body("accounts", hasSize(0));
    }

    @Then("^response includes the following message$")
    public void checkResponseException(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);

        json.body("statusCode", equalTo(Integer.valueOf(expected.get("statusCode"))))
                .body("message", equalTo(resolvePlaceholders(expected.get("message"))));
    }

    @Then("^response includes the following transfer status$")
    public void checkResponseTransfer(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);

        json.body("transferStatus", equalTo(expected.get("transferStatus")));
    }

    @Then("^response includes the following accounts$")
    public void checkResponseMultipleAccounts(DataTable expectedData) {
        expectedData.asMaps(String.class, String.class)
                .forEach(expected -> json.root(format("[%s]", expected.get("order")))
                        .body("balance", equalTo(Float.valueOf(expected.get("balance")))));
    }

    @Then("^response includes the following account$")
    public void checkResponseSingleAccount(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);
        long expectedId = extractAccount(getClientByFirstAndLastName(savedClients, "Ankit", "Jha")).getId();
        json.body("id", equalTo((int) expectedId))
                .body("balance", equalTo(Float.valueOf(expected.get("balance"))));
    }

    @Then("^balance of accounts are following")
    public void checkBalanceOfAccounts(DataTable expectedData) {
        expectedData.asMaps(String.class, String.class).forEach(row -> {
            Account account = resolvePlaceholder(row.get("accountId"), Account.class);
            getEm().refresh(account);
            assertThat(account.getBalance())
                    .isEqualTo(toBigDecimal(row.get("balance")));
        });
    }

    private String resolvePlaceholders(String str) {
        if (str.contains(ANKIT_JHA_ID_PLACEHOLDER)) {
            str = str.replace(ANKIT_JHA_ID_PLACEHOLDER,
                    resolvePlaceholder(ANKIT_JHA_ID_PLACEHOLDER, UUID.class).toString());
        }
        if (str.contains(NON_EXISTING_ID_PLACEHOLDER)) {
            str = str.replace(NON_EXISTING_ID_PLACEHOLDER,
                    resolvePlaceholder(NON_EXISTING_ID_PLACEHOLDER, String.class));
        }
        if (str.contains(ANKIT_JHA_ACCOUNT_ID_PLACEHOLDER)) {
            str = str.replace(ANKIT_JHA_ACCOUNT_ID_PLACEHOLDER,
                    String.valueOf(resolvePlaceholder(ANKIT_JHA_ACCOUNT_ID_PLACEHOLDER, Long.class)));
        }
        if (str.contains(NON_EXISTING_ACCOUNT_ID_PLACEHOLDER)) {
            str = str.replace(NON_EXISTING_ACCOUNT_ID_PLACEHOLDER,
                    String.valueOf(resolvePlaceholder(NON_EXISTING_ACCOUNT_ID_PLACEHOLDER, Long.class)));
        }
        return str;
    }

    private <T> T resolvePlaceholder(String placeholder, Class<T> type) {
        switch (placeholder) {
            case ANKIT_JHA_ID_PLACEHOLDER:
                return type.cast(extractClientId(getClientByFirstAndLastName(savedClients, "Ankit", "Jha")));
            case ANKIT_JHA_ACCOUNT_PLACEHOLDER:
                return type.cast(extractAccount(getClientByFirstAndLastName(savedClients, "Ankit", "Jha")));
            case ANKIT_JHA_ACCOUNT_ID_PLACEHOLDER:
                return type.cast(extractAccount(getClientByFirstAndLastName(savedClients, "Ankit", "Jha")).getId());
            case AMITH_JHA_ACCOUNT_PLACEHOLDER:
                return type.cast(extractAccount(getClientByFirstAndLastName(savedClients, "Amith", "Jha")));
            case AMITH_JHA_ACCOUNT_ID_PLACEHOLDER:
                return type.cast(extractAccount(getClientByFirstAndLastName(savedClients, "Amith", "Jha")).getId());
            case NON_EXISTING_ID_PLACEHOLDER:
                return type.cast(RANDOM_CLIENT_ID);
            case NON_EXISTING_ACCOUNT_ID_PLACEHOLDER:
                return type.cast(RANDOM_ACCOUNT_ID);
        }
        throw new IllegalStateException("Not known placeholder");
    }
}