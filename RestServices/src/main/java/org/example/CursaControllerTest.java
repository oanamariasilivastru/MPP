package org.example;

import model.Cursa;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.Callable;

public class CursaControllerTest {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/curse";

    public CursaControllerTest() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new CustomRestClientInterceptor());
    }

    public Cursa[] findAll() {
        return execute(() -> restTemplate.getForObject(BASE_URL, Cursa[].class));
    }

    public Cursa findOne(Long id) {
        String url = BASE_URL + "/" + id;
        return execute(() -> restTemplate.getForObject(url, Cursa.class));
    }

    public Long save(Cursa cursa) {
        return execute(() -> restTemplate.postForObject(BASE_URL, cursa, Long.class));
    }

    public void update(Long id, Cursa cursa) {
        String url = BASE_URL + "/" + id;
        execute(() -> {
            restTemplate.put(url, cursa);
            return null;
        });
    }

    public void delete(Long id) {
        String url = BASE_URL + "/" + id;
        execute(() -> {
            restTemplate.delete(url);
            return null;
        });
    }

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class CustomRestClientInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            System.out.println("Sending a " + request.getMethod() + " request to " + request.getURI() + " with body [" + new String(body) + "]");
            ClientHttpResponse response = execution.execute(request, body);
            System.out.println("Got response code " + response.getStatusText());
            return response;
        }
    }
}
