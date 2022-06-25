package com.example.signalapp;

import com.example.signalapp.dto.request.ModuleDtoRequest;
import com.example.signalapp.dto.response.ModuleDtoResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModuleIntegrationTest {

    private final RestTemplate template = new RestTemplate();

    private static int moduleId = 0;

    @Test
    @Order(1)
    public void testPostNullModule() {
        ModuleDtoRequest dto = new ModuleDtoRequest(null, "Name", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity("http://localhost:8080/modules", dto, String.class);
        });
        assertEquals(exc.getRawStatusCode(), 400);
        assertTrue(exc.getResponseBodyAsString().contains("module"));
    }

    @Test
    @Order(1)
    public void testPostEmptyModule() {
        ModuleDtoRequest dto = new ModuleDtoRequest("", "Name", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity("http://localhost:8080/modules", dto, String.class);
        });
        assertEquals(exc.getRawStatusCode(), 400);
        assertTrue(exc.getResponseBodyAsString().contains("module"));
    }

    @Test
    @Order(1)
    public void testPostModuleWithSpaces() {
        ModuleDtoRequest dto = new ModuleDtoRequest("With spaces", "Name", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity("http://localhost:8080/modules", dto, String.class);
        });
        assertEquals(exc.getRawStatusCode(), 400);
        assertTrue(exc.getResponseBodyAsString().contains("module"));
    }

    @Test
    @Order(1)
    public void testPostEmptyName() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity("http://localhost:8080/modules", dto, String.class);
        });
        assertEquals(exc.getRawStatusCode(), 400);
        assertTrue(exc.getResponseBodyAsString().contains("name"));
    }

    @Test
    @Order(1)
    public void testPostInvalidContainer() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "Name", "Invalid", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, ()-> {
            template.postForEntity("http://localhost:8080/modules", dto, String.class);
        });
        assertEquals(exc.getRawStatusCode(), 400);
        assertTrue(exc.getResponseBodyAsString().contains("container"));
    }

    @Test
    @Order(2)
    public void testPostOk() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "Name", "left", true, true);
        ModuleDtoResponse response = template.postForObject("http://localhost:8080/modules", dto, ModuleDtoResponse.class);
        assert response != null;
        assertTrue(response.getId() > 0);
        moduleId = response.getId();
        assertEquals(response.getModule(), dto.getModule());
        assertEquals(response.getName(), dto.getName());
        assertEquals(response.getContainer(), dto.getContainer());
        assertEquals(response.isForMenu(), dto.isForMenu());
        assertEquals(response.isTransformer(), dto.isTransformer());
    }

    // todo test put error

    @Test
    @Order(3)
    public void testPutOk() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module1", "Name", "left", true, true);
        ResponseEntity<ModuleDtoResponse> response = template.exchange("http://localhost:8080/modules/" + moduleId, HttpMethod.PUT, new HttpEntity<>(dto), ModuleDtoResponse.class);
        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(response.getBody()).getModule(), dto.getModule());
    }

    @Test
    @Order(4)
    public void testGet() {
        ModuleDtoResponse[] response = template.getForObject("http://localhost:8080/modules", ModuleDtoResponse[].class);
        assert response != null;
        assertEquals(response[response.length - 1].getId(), moduleId);
    }

    @Test
    @Order(5)
    public void testDelete() {
        template.delete("http://localhost:8080/modules/" + moduleId);
    }

}
