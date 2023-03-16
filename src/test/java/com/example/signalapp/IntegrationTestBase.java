package com.example.signalapp;

import com.example.signalapp.dto.request.LoginDtoRequest;
import com.example.signalapp.dto.request.UserDtoRequest;
import com.example.signalapp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.Objects;

public class IntegrationTestBase {

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected UserRepository userRepository;

    protected static final String BASE_URL = "http://localhost:";
    protected static final String SESSIONS_URL = "/api/sessions";
    protected static final String USERS_URL = "/api/users";
    protected static final String MODULES_URL = "/api/modules";

    protected final String password = "password0000";
    protected final String email1 = "test1@test.com";
    protected final String email2 = "test2@test.com";

    protected final RestTemplate template = new RestTemplate();

    protected final ObjectMapper mapper = new ObjectMapper();

    protected String fullUrl(String contextUrl) {
        return BASE_URL + "8080" + contextUrl;
    }

    protected void registerUsers() {
        template.postForEntity(fullUrl(USERS_URL), createUser(email1), String.class);
        template.postForEntity(fullUrl(USERS_URL), createUser(email2), String.class);
    }

    protected HttpHeaders login(String email) {
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL),
                new LoginDtoRequest().setEmail(email).setPassword(password), String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0));
        return headers;
    }

    protected UserDtoRequest createUser(String email) {
        return new UserDtoRequest()
                .setEmail(email)
                .setFirstName("First")
                .setLastName("Last")
                .setPatronymic("Patronymic")
                .setPassword(password);
    }

}
