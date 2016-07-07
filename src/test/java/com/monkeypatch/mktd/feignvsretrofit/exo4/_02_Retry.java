package com.monkeypatch.mktd.feignvsretrofit.exo4;

import com.monkeypatch.mktd.feignvsretrofit.exo1.MonkeyApi;
import com.monkeypatch.mktd.feignvsretrofit.exo1.model.Monkey;
import com.monkeypatch.mktd.feignvsretrofit.exo1.model.Page;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class _02_Retry implements Configuration {

    @Test
    public void testGetMonkeysWithRetry() throws Exception {
        int port = getAvailablePort();
        Buffer buffer = readBuffer("/monkeys.mock.json");
        String url = String.format("http://localhost:%s/", port);
        MonkeyApi api = ApiFactory.buildMonkeyWithRetryApi(url);

        // Call with retry
        CompletableFuture.supplyAsync(() -> asyncStart(port, buffer));
        Page<Monkey> page = api.getMonkeys(0);

        assertNotNull(page);
        assertEquals(16, page.getContent().size());
    }

    private RecordedRequest asyncStart(int port, Buffer buffer) {
        MockWebServer server = new MockWebServer();
        try {
            server.start(port);
            server.enqueue(new MockResponse().setBody(buffer));  // OK
            return server.takeRequest();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("WTF!", e);
        } finally {
            try {
                server.shutdown();
            } catch (IOException e) {
                throw new RuntimeException("WTF!", e);
            }
        }
    }

    private int getAvailablePort() {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to acquired an available port!", e);
        }
    }

    private Buffer readBuffer(String classpathResource) throws IOException {
        Buffer buffer = new Buffer();
        try (InputStream input = getClass().getResourceAsStream(classpathResource)) {
            buffer.readFrom(input);
            return buffer;
        }
    }
}
