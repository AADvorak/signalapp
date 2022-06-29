package com.example.signalapp;

import com.example.signalapp.dto.request.ModuleDtoRequest;
import com.example.signalapp.dto.response.FieldErrorDtoResponse;
import com.example.signalapp.dto.response.ModuleDtoResponse;
import com.example.signalapp.repository.ModuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModuleIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ModuleRepository moduleRepository;

    @BeforeAll
    public void clearAndRegisterUsers() {
        userRepository.deleteAll();
        registerUsers();
    }

    @BeforeEach
    public void clearAllModules() {
        moduleRepository.deleteAll();
    }

    @Test
    public void testPostNullModule() throws JsonProcessingException {
        ModuleDtoRequest dto = new ModuleDtoRequest(null, "Name", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                ()-> template.postForEntity(fullUrl(MODULES_URL), dto, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Module", error.getCode()),
                () -> assertEquals("module", error.getField()));
    }

    @Test
    public void testPostEmptyModule() throws JsonProcessingException {
        ModuleDtoRequest dto = new ModuleDtoRequest("", "Name", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                ()-> template.postForEntity(fullUrl(MODULES_URL), dto, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Module", error.getCode()),
                () -> assertEquals("module", error.getField()));
    }

    @Test
    public void testPostModuleWithSpaces() throws JsonProcessingException {
        ModuleDtoRequest dto = new ModuleDtoRequest("With spaces", "Name", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                ()-> template.postForEntity(fullUrl(MODULES_URL), dto, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Module", error.getCode()),
                () -> assertEquals("module", error.getField()));
    }

    @Test
    public void testPostEmptyName() throws JsonProcessingException {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "", "left", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                ()-> template.postForEntity(fullUrl(MODULES_URL), dto, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("name", error.getField()));
    }

    @Test
    public void testPostInvalidContainer() throws JsonProcessingException {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "Name", "Invalid", true, true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                ()-> template.postForEntity(fullUrl(MODULES_URL), dto, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Container", error.getCode()),
                () -> assertEquals("container", error.getField()));
    }

    @Test
    public void testPostOk() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "Name", "left", true, true);
        ModuleDtoResponse response = template.postForObject(fullUrl(MODULES_URL), dto, ModuleDtoResponse.class);
        assert response != null;
        assertAll(() -> assertTrue(response.getId() > 0),
                () -> assertEquals(dto.getModule(), response.getModule()),
                () -> assertEquals(dto.getName(), response.getName()),
                () -> assertEquals(dto.getContainer(), response.getContainer()),
                () -> assertEquals(dto.isForMenu(), response.isForMenu()),
                () -> assertEquals(dto.isTransformer(), response.isTransformer()));
    }

    @Test
    public void testPutOk() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "Name", "left", true, true);
        ModuleDtoResponse response = template.postForObject(fullUrl(MODULES_URL), dto, ModuleDtoResponse.class);
        assert response != null;
        int id = response.getId();
        dto.setModule(dto.getModule() + "1");
        dto.setName(dto.getName() + "1");
        dto.setContainer("right");
        dto.setForMenu(false);
        dto.setTransformer(false);
        ResponseEntity<ModuleDtoResponse> response1 = template.exchange(fullUrl(MODULES_URL) + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(dto), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response1.getBody();
        assert moduleDtoResponse != null;
        assertAll(() -> assertEquals(id, moduleDtoResponse.getId()),
                () -> assertEquals(dto.getModule(), moduleDtoResponse.getModule()),
                () -> assertEquals(dto.getName(), moduleDtoResponse.getName()),
                () -> assertEquals(dto.getContainer(), moduleDtoResponse.getContainer()),
                () -> assertEquals(dto.isForMenu(), moduleDtoResponse.isForMenu()),
                () -> assertEquals(dto.isTransformer(), moduleDtoResponse.isTransformer()));
    }

    @Test
    public void testGet() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "Name", "left", true, true);
        ModuleDtoResponse moduleDtoResponse = template.postForObject(fullUrl(MODULES_URL), dto, ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse1 = Objects.requireNonNull(template.getForObject(fullUrl(MODULES_URL), ModuleDtoResponse[].class))[0];
        assert moduleDtoResponse != null;
        assert moduleDtoResponse1 != null;
        assertAll(() -> assertEquals(moduleDtoResponse.getId(), moduleDtoResponse1.getId()),
                () -> assertEquals(dto.getModule(), moduleDtoResponse1.getModule()),
                () -> assertEquals(dto.getName(), moduleDtoResponse1.getName()),
                () -> assertEquals(dto.getContainer(), moduleDtoResponse1.getContainer()),
                () -> assertEquals(dto.isForMenu(), moduleDtoResponse1.isForMenu()),
                () -> assertEquals(dto.isTransformer(), moduleDtoResponse1.isTransformer()));
    }

    @Test
    public void testDelete() {
        ModuleDtoRequest dto = new ModuleDtoRequest("Module", "Name", "left", true, true);
        ModuleDtoResponse moduleDtoResponse = template.postForObject(fullUrl(MODULES_URL), dto, ModuleDtoResponse.class);
        assert moduleDtoResponse != null;
        template.delete(fullUrl(MODULES_URL) + "/" + moduleDtoResponse.getId());
    }

}
