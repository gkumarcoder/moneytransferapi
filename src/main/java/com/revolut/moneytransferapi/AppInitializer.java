package com.revolut.moneytransferapi;

import com.revolut.moneytransferapi.controller.Router;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class AppInitializer {
    @Inject
    public AppInitializer(PersistService persistService, Router router) {
        persistService.start();
        router.start();
    }
}
