package de.fh.kiel.advancedjava.pojomodel.controller;

import de.fh.kiel.advancedjava.pojomodel.model.Pojo;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Given the developer wants to add a pojo to the database")
@Nested
public class PojoControllerTest {

    private static String classWithPrimtives;
    private static String defaultClass;
    private static String notBase64EncodedClass;

    private static String pathToBase64Folder ="/Users/mpetersen/Desktop/pojo-malte/src/test/java/de/fh/kiel/advancedjava/pojomodel/exampleData/base64Encoded/";
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PojoRepository pojoRepository;

    public static String loadData(String location) throws IOException {
        return Files.readString(Paths.get(location));
    }

    @BeforeAll()
    static void loadClassesEncodedInBase64() throws IOException {
        classWithPrimtives = loadData( pathToBase64Folder + "ClassWithPrimtives.txt");
        defaultClass = loadData(pathToBase64Folder + "defaultClass.txt");
        notBase64EncodedClass = loadData(pathToBase64Folder + "notBase64EncodedClass.txt");
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
    @DisplayName("When we send a new compiled Class in base64 to the endpoint")
    class  NewClass{
        @Test
        @DisplayName("Then the endpoint should return 200 OK as an answer also with just objects")
        public void getPojoDefaultClass() throws Exception {
            mvc.perform(MockMvcRequestBuilders.post("/pojo")
                    .content(defaultClass)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        @Test
        @DisplayName("Then the endpoint should return 200 OK as an answer also with primitives")
        public void getPojoPrimitiveClass() throws Exception {
            mvc.perform(MockMvcRequestBuilders.post("/pojo")
                    .content(classWithPrimtives)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        }
    }
    @Nested
    @DisplayName("When class already exist as an empty hull")
    class  ClassExistsAsEmptyHull {
        @BeforeEach()
        public void createEmptyHullPojo() throws Exception {
            pojoRepository.save(new Pojo("de.fh.kiel.advancedjava.pojomodel.exampleData.DefaultClass", "de.fh.kiel.advancedjava.pojomodel.exampleData"));
        }
        @Test
        @DisplayName("Then the endpoint should return 200 OK as an answer")
        public void getPojo() throws Exception {
            mvc.perform(MockMvcRequestBuilders.post("/pojo")
                    .content(defaultClass)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Nested
        @DisplayName("When a already existing class is send to to the endpoint in base64 and the exisitng class is not an empty hull")
        class AlreadyExistingClass {
            @BeforeEach()
            public void createPojo() throws Exception {
                pojoRepository.save(new Pojo("de.fh.kiel.advancedjava.pojomodel.exampleData.DefaultClass", "de.fh.kiel.advancedjava.pojomodel.exampleData", null, null, null));
            }

            @Test
            @DisplayName("Then the endpoint should return an is internal Server error status")
            public void createTheSamePojoAgain()  {
             assertThrows(Exception.class, () -> {
                         mvc.perform(MockMvcRequestBuilders.post("/pojo")
                                 .content(defaultClass)
                                 .accept(MediaType.APPLICATION_JSON))
                                 .andExpect(status().isInternalServerError())
                                 .andReturn();
                     });
            }
        }

        @Nested
        @DisplayName("When the class is not base64 encoded")
        class InputNotBase64 {
            @Test
            @DisplayName("Then the endpoint should return an 500 internal Server error")
            public void getPojo() throws Exception {
                mvc.perform(MockMvcRequestBuilders.post("/pojo")
                        .content(notBase64EncodedClass)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isInternalServerError())
                        .andReturn();
            }
        }

    }
}