package com.revolut.moneytransferapi.dao;

import org.assertj.core.util.Lists;
import org.junit.*;

import com.revolut.moneytransferapi.dao.ClientDao;
import com.revolut.moneytransferapi.dao.ClientDaoImpl;
import com.revolut.moneytransferapi.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.revolut.moneytransferapi.DataGenerator.ClientBuilder.aClient;
import static org.assertj.core.api.Assertions.assertThat;

public class ClientDaoImplIntegrationTest extends AbstractDaoIntegrationTest {

    private ClientDao underTest;

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
        underTest = new ClientDaoImpl(AbstractDaoIntegrationTest::getEm);
    }

    @After
    public void clearDB() {
        cleanDB();
    }

    @Test
    public void returnsEmptyListWhenNoClients() {
        // when
        List<Client> actual = underTest.getClients();

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void returnsAllClients() {
        // given
        Client c1 = aClient()
                .withFirstName("Abc")
                .withLastName("Qwe")
                .build();
        Client c2 = aClient()
                .withFirstName("Yui")
                .withLastName("Bnm")
                .build();
        storeClients(Lists.newArrayList(c1, c2));

        // when
        List<Client> actual = underTest.getClients();

        // then
        assertThat(actual).containsExactlyInAnyOrder(c1, c2);
    }

    @Test
    public void returnsOptionalWithSingleClient() {
        // given
        Client c1 = aClient()
                .withFirstName("Pankaj")
                .withLastName("Jha")
                .build();
        storeClients(Lists.newArrayList(c1));

        // when
        Optional<Client> actual = underTest.getClient(c1.getId());

        // then
        assertThat(actual.get()).isEqualTo(c1);
    }

    @Test
    public void returnsEmptyOptionalWhenClientNotFound() {
        // given
        UUID nonExistingClientId = UUID.randomUUID();

        // when
        Optional<Client> actual = underTest.getClient(nonExistingClientId);

        // then
        assertThat(actual.isPresent()).isFalse();
    }

}