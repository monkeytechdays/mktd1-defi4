package com.monkeypatch.mktd.feignvsretrofit.exo4;

import com.monkeypatch.mktd.feignvsretrofit.exo1.MonkeyApi;
import com.monkeypatch.mktd.feignvsretrofit.exo1.model.Monkey;
import com.monkeypatch.mktd.feignvsretrofit.exo1.model.Page;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class _03_CircuitBreaker implements Configuration {

    @Rule
    public MockWebServer server = new MockWebServer();

    private MonkeyApi api;

    @Before
    public void buildApi() {
        String url = server.url("/").toString();
        api = ApiFactory.buildMonkeyWithCircuitBreaker(url);
    }

    @Test
    public void testGetMonkeysWithCircuitBreaker() throws Exception {
        Page<Monkey> page;

        // First Call
        Buffer buffer = readBuffer("/monkeys.mock.json");
        server.enqueue(new MockResponse().setBody(buffer));  // OK
        page = api.getMonkeys(0);
        assertNotNull(page);
        assertEquals(16, page.getContent().size());

        // Second Call (Fallback should return an empty list)
        server.enqueue(new MockResponse().setResponseCode(500)); // Server Error
        page = api.getMonkeys(0);
        assertNotNull(page);
        assertEquals(0, page.getContent().size());

        // Third Call (going back OK)
        server.enqueue(new MockResponse().setBody(buffer));  // OK
        page = api.getMonkeys(0);
        assertNotNull(page);
        assertEquals(16, page.getContent().size());
    }

    private Buffer readBuffer(String classpathResource) throws IOException {
        Buffer buffer = new Buffer();
        try (InputStream input = getClass().getResourceAsStream(classpathResource)) {
            buffer.readFrom(input);
            return buffer;
        }
    }
}
