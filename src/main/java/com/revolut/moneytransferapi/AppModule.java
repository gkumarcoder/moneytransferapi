package com.revolut.moneytransferapi;

import com.revolut.moneytransferapi.dao.AccountDao;
import com.revolut.moneytransferapi.dao.AccountDaoImpl;
import com.revolut.moneytransferapi.dao.ClientDao;
import com.revolut.moneytransferapi.dao.ClientDaoImpl;
import com.revolut.moneytransferapi.controller.Router;
import com.revolut.moneytransferapi.controller.RouterImpl;
import com.revolut.moneytransferapi.service.ClientAccountService;
import com.revolut.moneytransferapi.service.ClientAccountServiceImpl;
import com.revolut.moneytransferapi.service.InternalTransferService;
import com.revolut.moneytransferapi.service.TransferService;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;


public class AppModule extends AbstractModule {

    private static final String UNDERSCORE = "_";

    @Override
    protected void configure() {
        bind(Gson.class).toInstance(new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().startsWith(UNDERSCORE);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create());

        bind(AccountDao.class).to(AccountDaoImpl.class);
        bind(ClientDao.class).to(ClientDaoImpl.class);
        bind(ClientAccountService.class).to(ClientAccountServiceImpl.class);
        bind(TransferService.class).to(InternalTransferService.class);
        bind(Router.class).to(RouterImpl.class);
    }
}
