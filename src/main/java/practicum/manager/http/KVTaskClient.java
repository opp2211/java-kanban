package practicum.manager.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String apiToken;
    private final HttpClient client;

    public KVTaskClient(int port) {
        client = HttpClient.newHttpClient();
        url = "http://localhost:" + port + "/";
        apiToken = register();
    }
    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
        KVTaskClient client = new KVTaskClient(KVServer.PORT);

        String testKey = "ключ1";
        client.put(testKey, "данные1");
        System.out.println(client.load(testKey));

        client.put(testKey, "данные2");
        System.out.println(client.load(testKey));
        //System.out.println(client.load("")); //исключение
    }
    private String register() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
            return response.body();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException();
        }
    }

    public String load(String key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "";
            }
            return response.body();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException();
        }

    }
    public void put(String key, String value) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException();
        }
    }
}
