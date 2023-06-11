package com.example.signalapp;

import com.example.signalapp.dto.request.EditModuleDtoRequest;
import com.example.signalapp.dto.request.ModuleDtoRequest;
import com.example.signalapp.dto.response.FieldErrorDtoResponse;
import com.example.signalapp.dto.response.ModuleDtoResponse;
import com.example.signalapp.repository.ModuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "/test.properties")
public class ModuleIntegrationTest extends IntegrationTestBase {

    private static final String HTML_URL = "/html";
    private static final String JS_URL = "/js";

    private static final String TEST_MODULE_HTML = "<form class=\"needs-validation\" novalidate>\n" +
            "  <div class=\"form-group\">\n" +
            "    <label for=\"TestModuleText\">Text</label>\n" +
            "    <input type=\"text\" class=\"form-control\" id=\"TestModuleText\" placeholder=\"Enter text\" required>\n" +
            "    <div id=\"TestModuleTextValid\" class=\"invalid-feedback\" style=\"width: 100%;\"></div>\n" +
            "  </div>\n" +
            "</form>\n" +
            "<button id=\"TestModuleTransform\" class=\"btn btn-primary\">Transform</button>";
    private static final String TEST_MODULE_JS = "TestModule = {\n" +
            "  init(param) {\n" +
            "    this.signal = param\n" +
            "    this.selectElements()\n" +
            "    this.initEvents()\n" +
            "  },\n" +
            "  selectElements() {\n" +
            "    this.ui = {}\n" +
            "    this.ui.textInp = $('#TestModuleText')\n" +
            "    this.ui.transformBtn = $('#TestModuleTransform')\n" +
            "  },\n" +
            "  initEvents() {\n" +
            "    this.ui.transformBtn.on('click', () => {\n" +
            "      this.doTransform()\n" +
            "    })\n" +
            "  },\n" +
            "  doTransform() {\n" +
            "    this.signal.description += `\\n${this.ui.coefficientInp.val()}`\n" +
            "    Workspace.closeModule(this)\n" +
            "    Workspace.startModule({\n" +
            "      module: 'Cable',\n" +
            "      param: this.signal\n" +
            "    }).then()\n" +
            "  },\n" +
            "}";

    @Autowired
    private ModuleRepository moduleRepository;

    @BeforeAll
    public void clearAndRegisterUsers() {
        userRepository.deleteAll();
        registerUsers();
    }

    @BeforeEach
    public void clearAllUserModules() {
        moduleRepository.deleteAll();
    }

    @Test
    public void testPostNullModule() throws JsonProcessingException {
        ModuleDtoRequest dto = createModuleDtoRequest().setModule(null);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL), HttpMethod.POST, new HttpEntity<>(dto, login(email1)), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Module", error.getCode()),
                () -> assertEquals("module", error.getField()));
    }

    @Test
    public void testPostEmptyModule() throws JsonProcessingException {
        ModuleDtoRequest dto = createModuleDtoRequest().setModule("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL), HttpMethod.POST, new HttpEntity<>(dto, login(email1)), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Module", error.getCode()),
                () -> assertEquals("module", error.getField()));
    }

    @Test
    public void testPostModuleWithSpaces() throws JsonProcessingException {
        ModuleDtoRequest dto = createModuleDtoRequest().setModule("With spaces");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL), HttpMethod.POST, new HttpEntity<>(dto, login(email1)), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Module", error.getCode()),
                () -> assertEquals("module", error.getField()));
    }

    @Test
    public void testPostEmptyName() throws JsonProcessingException {
        ModuleDtoRequest dto = createModuleDtoRequest().setName("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL), HttpMethod.POST, new HttpEntity<>(dto, login(email1)), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("name", error.getField()));
    }

    @Test
    public void testPostInvalidContainer() throws JsonProcessingException {
        ModuleDtoRequest dto = createModuleDtoRequest().setContainer("Invalid");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL), HttpMethod.POST, new HttpEntity<>(dto, login(email1)), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Container", error.getCode()),
                () -> assertEquals("container", error.getField()));
    }

    @Test
    public void testPostUnauthorized() {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(MODULES_URL), createModuleDtoRequest(), String.class));
        assertEquals(401, exc.getRawStatusCode());
    }

    @Test
    public void testPostExistingModule() throws JsonProcessingException {
        HttpHeaders headers = login(email1);
        ModuleDtoRequest dto = createModuleDtoRequest();
        template.exchange(fullUrl(MODULES_URL), HttpMethod.POST, new HttpEntity<>(dto, headers), ModuleDtoResponse.class);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL), HttpMethod.POST, new HttpEntity<>(dto, headers), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MODULE_ALREADY_EXISTS", error.getCode()),
                () -> assertEquals("module", error.getField()));
    }

    @Test
    public void testPostOk() {
        ModuleDtoRequest dto = createModuleDtoRequest();
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(dto, login(email1)), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        assertAll(() -> assertTrue(moduleDtoResponse.getId() > 0),
                () -> assertEquals(dto.getModule(), moduleDtoResponse.getModule()),
                () -> assertEquals(dto.getName(), moduleDtoResponse.getName()),
                () -> assertEquals(dto.getContainer(), moduleDtoResponse.getContainer()),
                () -> assertEquals(dto.isForMenu(), moduleDtoResponse.isForMenu()),
                () -> assertEquals(dto.isTransformer(), moduleDtoResponse.isTransformer()));
    }

    @Test
    public void testPutNullName() throws JsonProcessingException {
        HttpHeaders headers = login(email1);
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        int id = moduleDtoResponse.getId();
        EditModuleDtoRequest editModuleDtoRequest = new EditModuleDtoRequest()
                .setContainer("right")
                .setForMenu(false)
                .setTransformer(false);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL) + "/" + id, HttpMethod.PUT,
                        new HttpEntity<>(editModuleDtoRequest, headers), ModuleDtoResponse.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("name", error.getField()));
    }

    @Test
    public void testPutEmptyName() throws JsonProcessingException {
        HttpHeaders headers = login(email1);
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        int id = moduleDtoResponse.getId();
        EditModuleDtoRequest editModuleDtoRequest = new EditModuleDtoRequest()
                .setName("")
                .setContainer("right")
                .setForMenu(false)
                .setTransformer(false);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL) + "/" + id, HttpMethod.PUT,
                        new HttpEntity<>(editModuleDtoRequest, headers), ModuleDtoResponse.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("name", error.getField()));
    }

    @Test
    public void testPutInvalidContainer() throws JsonProcessingException {
        HttpHeaders headers = login(email1);
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        int id = moduleDtoResponse.getId();
        EditModuleDtoRequest editModuleDtoRequest = new EditModuleDtoRequest()
                .setName("Name")
                .setContainer("invalid")
                .setForMenu(false)
                .setTransformer(false);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL) + "/" + id, HttpMethod.PUT,
                        new HttpEntity<>(editModuleDtoRequest, headers), ModuleDtoResponse.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Container", error.getCode()),
                () -> assertEquals("container", error.getField()));
    }

    @Test
    public void testPutAnotherUser() {
        HttpHeaders headers = login(email1);
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        int id = moduleDtoResponse.getId();
        EditModuleDtoRequest editModuleDtoRequest = new EditModuleDtoRequest()
                .setName("Name")
                .setContainer("right")
                .setForMenu(false)
                .setTransformer(false);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL) + "/" + id, HttpMethod.PUT,
                        new HttpEntity<>(editModuleDtoRequest, login(email2)), ModuleDtoResponse.class));
        assertEquals(404, exc.getRawStatusCode());
    }

    @Test
    public void testPutUnauthorized() {
        HttpHeaders headers = login(email1);
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        int id = moduleDtoResponse.getId();
        EditModuleDtoRequest editModuleDtoRequest = new EditModuleDtoRequest()
                .setName("Name")
                .setContainer("right")
                .setForMenu(false)
                .setTransformer(false);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.put(fullUrl(MODULES_URL) + "/" + id,
                        editModuleDtoRequest, ModuleDtoResponse.class));
        assertEquals(401, exc.getRawStatusCode());
    }

    @Test
    public void testPutOk() {
        HttpHeaders headers = login(email1);
        ModuleDtoRequest dto = createModuleDtoRequest();
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(dto, headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        int id = moduleDtoResponse.getId();
        EditModuleDtoRequest editModuleDtoRequest = new EditModuleDtoRequest()
                .setName(dto.getName() + "1")
                .setContainer("right")
                .setForMenu(false)
                .setTransformer(false);
        ResponseEntity<ModuleDtoResponse> response1 = template.exchange(fullUrl(MODULES_URL) + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(editModuleDtoRequest, headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse1 = response1.getBody();
        assert moduleDtoResponse1 != null;
        assertAll(() -> assertEquals(id, moduleDtoResponse1.getId()),
                () -> assertEquals(dto.getModule(), moduleDtoResponse1.getModule()),
                () -> assertEquals(editModuleDtoRequest.getName(), moduleDtoResponse1.getName()),
                () -> assertEquals(editModuleDtoRequest.getContainer(), moduleDtoResponse1.getContainer()),
                () -> assertEquals(editModuleDtoRequest.isForMenu(), moduleDtoResponse1.isForMenu()),
                () -> assertEquals(editModuleDtoRequest.isTransformer(), moduleDtoResponse1.isTransformer()));
    }

    @Test
    public void testGet() {
        HttpHeaders headers = login(email1);
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        ModuleDtoResponse moduleDtoResponse1 = Objects.requireNonNull(template.exchange(fullUrl(MODULES_URL), HttpMethod.GET,
                new HttpEntity<>(headers), ModuleDtoResponse[].class).getBody())[0];
        ModuleDtoResponse[] moduleDtoResponseAnotherUser = Objects.requireNonNull(template.exchange(fullUrl(MODULES_URL), HttpMethod.GET,
                new HttpEntity<>(login(email2)), ModuleDtoResponse[].class).getBody());
        ModuleDtoResponse[] moduleDtoResponseUnauthorized = Objects.requireNonNull(template.getForObject(fullUrl(MODULES_URL),
                ModuleDtoResponse[].class));
        assert moduleDtoResponse != null;
        assert moduleDtoResponse1 != null;
        assertAll(() -> assertEquals(moduleDtoResponse.getId(), moduleDtoResponse1.getId()),
                () -> assertEquals(moduleDtoResponse.getModule(), moduleDtoResponse1.getModule()),
                () -> assertEquals(moduleDtoResponse.getName(), moduleDtoResponse1.getName()),
                () -> assertEquals(moduleDtoResponse.getContainer(), moduleDtoResponse1.getContainer()),
                () -> assertEquals(moduleDtoResponse.isForMenu(), moduleDtoResponse1.isForMenu()),
                () -> assertEquals(moduleDtoResponse.isTransformer(), moduleDtoResponse1.isTransformer()),
                () -> assertEquals(0, moduleDtoResponseAnotherUser.length),
                () -> assertEquals(0, moduleDtoResponseUnauthorized.length));
    }

    @Test
    public void testDelete() {
        HttpHeaders headers = login(email1);
        ModuleDtoResponse moduleDtoResponse = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class).getBody();
        assert moduleDtoResponse != null;
        ResponseEntity<String> response = template.exchange(fullUrl(MODULES_URL) + "/" + moduleDtoResponse.getId(),
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteUnauthorized() {
        HttpHeaders headers = login(email1);
        ModuleDtoResponse moduleDtoResponse = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class).getBody();
        assert moduleDtoResponse != null;
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.delete(fullUrl(MODULES_URL) + "/" + moduleDtoResponse.getId()));
        assertEquals(401, exc.getRawStatusCode());
    }

    @Test
    public void testDeleteByAnotherUser() {
        HttpHeaders headers = login(email1);
        ModuleDtoResponse moduleDtoResponse = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(createModuleDtoRequest(), headers), ModuleDtoResponse.class).getBody();
        assert moduleDtoResponse != null;
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL) + "/" + moduleDtoResponse.getId(),
                        HttpMethod.DELETE, new HttpEntity<>(login(email2)), String.class));
        assertEquals(404, exc.getRawStatusCode());
    }

    @Test
    public void testDeleteNotExisting() {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL) + "/1",
                        HttpMethod.DELETE, new HttpEntity<>(login(email2)), String.class));
        assertEquals(404, exc.getRawStatusCode());
    }

    @Test
    public void testUploadAndDownloadFiles() {
        HttpHeaders headers = login(email1);
        ModuleDtoRequest dto = new ModuleDtoRequest()
                .setModule("TestModule")
                .setName("TestModule")
                .setContainer("left")
                .setForMenu(false)
                .setTransformer(true);
        ResponseEntity<ModuleDtoResponse> response = template.exchange(fullUrl(MODULES_URL), HttpMethod.POST,
                new HttpEntity<>(dto, headers), ModuleDtoResponse.class);
        ModuleDtoResponse moduleDtoResponse = response.getBody();
        assert moduleDtoResponse != null;
        int id = moduleDtoResponse.getId();
        headers.add("Content-Type", MediaType.TEXT_HTML_VALUE);
        ResponseEntity<String> responsePutHtml = template.exchange(fullUrl(MODULES_URL + "/" + id + HTML_URL),
                HttpMethod.PUT, new HttpEntity<>(TEST_MODULE_HTML, headers), String.class);
        headers.remove("Content-Type");
        headers.add("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        ResponseEntity<String> responsePutJs = template.exchange(fullUrl(MODULES_URL + "/" + id + JS_URL),
                HttpMethod.PUT, new HttpEntity<>(TEST_MODULE_JS, headers), String.class);
        headers.remove("Content-Type");
        ResponseEntity<String> responseGetHtml = template.exchange(fullUrl(MODULES_URL + "/" + id + HTML_URL),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        ResponseEntity<String> responseGetJs = template.exchange(fullUrl(MODULES_URL + "/" + id + JS_URL),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        HttpHeaders headers2 = login(email2);
        HttpClientErrorException excGetHtml = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + HTML_URL),
                        HttpMethod.GET, new HttpEntity<>(headers2), String.class));
        HttpClientErrorException excGetJs = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + JS_URL),
                        HttpMethod.GET, new HttpEntity<>(headers2), String.class));
        headers2.add("Content-Type", MediaType.TEXT_HTML_VALUE);
        HttpClientErrorException excPutHtml = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + HTML_URL),
                        HttpMethod.PUT, new HttpEntity<>(TEST_MODULE_HTML, headers2), String.class));
        headers2.remove("Content-Type");
        headers2.add("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        HttpClientErrorException excPutJs = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + JS_URL),
                        HttpMethod.PUT, new HttpEntity<>(TEST_MODULE_JS, headers2), String.class));
        HttpHeaders headers3 = new HttpHeaders();
        HttpClientErrorException excGetHtmlUnauthorized = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + HTML_URL),
                        HttpMethod.GET, new HttpEntity<>(headers3), String.class));
        HttpClientErrorException excGetJsUnauthorized = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + JS_URL),
                        HttpMethod.GET, new HttpEntity<>(headers3), String.class));
        headers3.add("Content-Type", MediaType.TEXT_HTML_VALUE);
        HttpClientErrorException excPutHtmlUnauthorized = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + HTML_URL),
                        HttpMethod.PUT, new HttpEntity<>(TEST_MODULE_HTML, headers3), String.class));
        headers3.remove("Content-Type");
        headers3.add("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        HttpClientErrorException excPutJsUnauthorized = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(MODULES_URL + "/" + id + JS_URL),
                        HttpMethod.PUT, new HttpEntity<>(TEST_MODULE_JS, headers3), String.class));
        assertAll(() -> assertEquals(200, responsePutHtml.getStatusCodeValue()),
                () -> assertEquals(200, responsePutJs.getStatusCodeValue()),
                () -> assertEquals(200, responseGetHtml.getStatusCodeValue()),
                () -> assertEquals(TEST_MODULE_HTML, responseGetHtml.getBody()),
                () -> assertEquals(200, responseGetJs.getStatusCodeValue()),
                () -> assertEquals(TEST_MODULE_JS, responseGetJs.getBody()),
                () -> assertEquals(404, excPutHtml.getRawStatusCode()),
                () -> assertEquals(404, excPutJs.getRawStatusCode()),
                () -> assertEquals(404, excGetHtml.getRawStatusCode()),
                () -> assertEquals(404, excGetJs.getRawStatusCode()),
                () -> assertEquals(401, excPutHtmlUnauthorized.getRawStatusCode()),
                () -> assertEquals(401, excPutJsUnauthorized.getRawStatusCode()),
                () -> assertEquals(404, excGetHtmlUnauthorized.getRawStatusCode()),
                () -> assertEquals(404, excGetJsUnauthorized.getRawStatusCode()));
    }

    private ModuleDtoRequest createModuleDtoRequest() {
        return new ModuleDtoRequest()
                .setModule("Module")
                .setName("Name")
                .setContainer("left")
                .setTransformer(true)
                .setForMenu(true);
    }

}
