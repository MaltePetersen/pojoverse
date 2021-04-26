package de.fh.kiel.advancedjava.pojomodel.controller;

import de.fh.kiel.advancedjava.pojomodel.repository.PojoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Given the developer wants to remove an attribute of a file")
@Nested
public class Story9IntegrationTests {

    private static String attributeChangeDTO;
    private static String defaultClass;
    private static String badAttributeChangeDTO;

    private static String pathToExampleData ="/Users/mpetersen/Desktop/pojo-malte/src/test/java/de/fh/kiel/advancedjava/pojomodel/exampleData/";

    private static String pathToJSONFolder = pathToExampleData +"json/";

    private static String pathToBase64Folder = pathToExampleData +"base64Encoded/";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PojoRepository pojoRepository;

    public static String loadData(String location) throws IOException {
        return Files.readString(Paths.get(location));
    }

    @BeforeAll()
    static void loadClassesEncodedInBase64() throws IOException {
        badAttributeChangeDTO = loadData(pathToJSONFolder + "BadAttributeChangeDTO.json");
        attributeChangeDTO = loadData(pathToJSONFolder + "AttributeChangeDTO.json");
        defaultClass = loadData(pathToBase64Folder + "DefaultClass.txt");
    }

    @AfterEach()
    void deleteAllSavedClasses(){
        this.pojoRepository.deleteAll();
    }

    @BeforeEach()
    void SetUp(){
        pojoRepository.deleteAll();
    }

    @Nested
    @DisplayName("When the developer sends a deleteAttrbute request")
    class deleteAttribute {
        @BeforeEach()
        void SetUp() throws Exception {
            mvc.perform(MockMvcRequestBuilders.post("/pojo")
                    .content(defaultClass)
                    .accept(MediaType.APPLICATION_JSON));

        }

        @Test
        @DisplayName("Then the endpoint should return an 200 ok")
        public void attributeChange() throws Exception {
            mvc.perform(MockMvcRequestBuilders.put("/pojo")
                    .content(attributeChangeDTO).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL)).andExpect(status().isOk())
                    .andReturn();
            assertFalse( pojoRepository.findById("de.fh.kiel.advancedjava.pojomodel.exampleData.DefaultClass").get().getAttributes().stream().anyMatch((data)-> data.getName().equals("name")));
        }
    }
    @Nested
    @DisplayName("When the developer sends a bad deleteAttrbute request")
    class badDeleteAttribute {
        @BeforeEach()
        void SetUp() throws Exception {
            mvc.perform(MockMvcRequestBuilders.post("/pojo")
                    .content(defaultClass)
                    .accept(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Then the endpoint should return an 500 internal server error")
        public void attributeChange() throws Exception {
            mvc.perform(MockMvcRequestBuilders.put("/pojo")
                    .content(badAttributeChangeDTO).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL)).andExpect(status().isBadRequest())
                    .andReturn();
        }
    }
}
