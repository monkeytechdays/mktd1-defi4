package com.monkeypatch.mktd.feignvsretrofit.exo4;

import com.monkeypatch.mktd.feignvsretrofit.exo1.MonkeyRaceApi;
import com.monkeypatch.mktd.feignvsretrofit.exo1.model.MonkeyRace;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class _01_CachingTest implements Configuration {

    @Rule
    public MockWebServer server = new MockWebServer();

    private MonkeyRaceApi api;

    @Before
    public void buildApi() throws IOException {
        String url = server.url("/").toString();
        api = ApiFactory.buildRaceWithCachingApi(url);
    }

    @Test
    public void testGetRacesWithCache() throws Exception {
        List<MonkeyRace> races;

        // First Call
        Buffer buffer = readBuffer("/races.mock.json");
        server.enqueue(new MockResponse()
                .setHeader("Cache-Control", "max-age=60")
                .setBody(buffer));
        races = api.getMonkeyRaces();
        assertNotNull(races);
        assertEquals(5, races.size());

        // Second Call
        server.enqueue(new MockResponse().setResponseCode(500));
        races = api.getMonkeyRaces();
        assertNotNull(races);
        assertEquals(5, races.size());
    }

    private Buffer readBuffer(String classpathResource) throws IOException {
        Buffer buffer = new Buffer();
        try (InputStream input = getClass().getResourceAsStream(classpathResource)) {
            buffer.readFrom(input);
            return buffer;
        }
    }
}
