package com.monkeypatch.mktd.feignvsretrofit.exo4;

import com.monkeypatch.mktd.feignvsretrofit.exo1.MonkeyApi;
import com.monkeypatch.mktd.feignvsretrofit.exo1.MonkeyRaceApi;

class ApiFactory {

    static MonkeyApi buildMonkeyWithRetryApi(String url) {
        // TODO you should implements this method
        throw new RuntimeException("Not yet implemented");
    }

    static MonkeyApi buildMonkeyWithCircuitBreaker(String url) {
        // TODO you should implements this method
        throw new RuntimeException("Not yet implemented");
    }

    static MonkeyRaceApi buildRaceWithCachingApi(String url) {
        // TODO you should implements this method with caching
        throw new RuntimeException("Not yet implemented");
    }

}
