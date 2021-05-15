package de.fh.kiel.advancedjava.pojomodel.service;

import de.fh.kiel.advancedjava.pojomodel.AttributeName;
import de.fh.kiel.advancedjava.pojomodel.Class;
import de.fh.kiel.advancedjava.pojomodel.TestingUtil;
import de.fh.kiel.advancedjava.pojomodel.exception.PackageNameNotAllowed;
import de.fh.kiel.advancedjava.pojomodel.exception.PojoAlreadyExists;
import de.fh.kiel.advancedjava.pojomodel.facade.PojoFacadeService;
import de.fh.kiel.advancedjava.pojomodel.model.Package;
import de.fh.kiel.advancedjava.pojomodel.repository.AttributeRepository;
import de.fh.kiel.advancedjava.pojomodel.repository.PackageRepository;
import de.fh.kiel.advancedjava.pojomodel.repository.PojoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class PojoFacadeServiceTest {

    @Autowired
    private PojoFacadeService pojoFacadeService;
    @Autowired
    PojoRepository pojoRepository;
    @Autowired
    AttributeRepository attributeRepository;
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    private TestingUtil testingUtil;

    @BeforeEach
    void deletePojos(){
        pojoRepository.deleteAll();
        attributeRepository.deleteAll();
        packageRepository.deleteAll();
    }
    @Test
    @DisplayName("Create a new Pojo")
    void createPojo()  {
        var pojo = testingUtil.getPojo(Class.DEFAULT_CLASS.name);
        pojoFacadeService.createPojo(pojo.getCompletePath(), pojo.getClassName(), pojo.getAPackage().getId(), pojo.getParentClass().getCompletePath(), pojo.getParentClass().getClassName(), pojo.getParentClass().getAPackage().getId(),pojo.getInterfaces(), pojo.getAttributes());
        var savedPojo = pojoRepository.findById(pojo.getCompletePath()).get();
        assertEquals(pojo, savedPojo);

    }
    @Test
    @DisplayName("Create a Pojo with attributes")
    void createPojoWithAttributes()  {
        var pojo = testingUtil.getPojo(Class.CLASS_WITH_PRIMTIVES.name);
        pojoFacadeService.createPojo(pojo.getCompletePath(), pojo.getClassName(), pojo.getAPackage().getId(), pojo.getParentClass().getCompletePath(), pojo.getParentClass().getClassName(), pojo.getParentClass().getAPackage().getId(),pojo.getInterfaces(), pojo.getAttributes());
        var savedPojo = pojoRepository.findById(pojo.getCompletePath()).get();
        assertEquals(pojo, savedPojo);
    }
    @Test
    @DisplayName("Create a new Pojo as an empty hull")
    void createPojoEmptyHulll()  {
        var pojo = testingUtil.getEmptyHull(Class.DEFAULT_CLASS.name);
        pojoFacadeService.createPojo(pojo.getCompletePath(), pojo.getClassName(), pojo.getAPackage().getId());
        var savedPojo = pojoRepository.findById(pojo.getCompletePath()).get();
        assertEquals(pojo, savedPojo);
    }    @Test
    @DisplayName("Create a new Pojo but a pojo already exists")
    void createPojoAlreadyExists()  {
        var pojo = testingUtil.getPojo(Class.DEFAULT_CLASS.name);
        pojoFacadeService.createPojo(pojo.getCompletePath(), pojo.getClassName(), pojo.getAPackage().getId(), pojo.getParentClass().getCompletePath(), pojo.getParentClass().getClassName(), pojo.getParentClass().getAPackage().getId(),pojo.getInterfaces(), pojo.getAttributes());
        assertThrows(PojoAlreadyExists.class , () -> pojoFacadeService.createPojo(pojo.getCompletePath(), pojo.getClassName(), pojo.getAPackage().getId(), pojo.getParentClass().getCompletePath(), pojo.getParentClass().getClassName(), pojo.getParentClass().getAPackage().getId(),pojo.getInterfaces(), pojo.getAttributes()));
    }    @Test
    @DisplayName("Create multiplePojos")
    void createPojos()  {
        var pojo1 = testingUtil.getPojo(Class.DEFAULT_CLASS.name);
        var pojo2 = testingUtil.getPojo(Class.CLASS_WITH_PRIMTIVES.name);
        var savedPojo1 = pojoFacadeService.createPojo(pojo1.getCompletePath(), pojo1.getClassName(), pojo1.getAPackage().getId(), pojo1.getParentClass().getCompletePath(), pojo1.getParentClass().getClassName(), pojo1.getParentClass().getAPackage().getId(),pojo1.getInterfaces(), pojo1.getAttributes());
        var savedPojo2 = pojoFacadeService.createPojo(pojo2.getCompletePath(), pojo2.getClassName(), pojo2.getAPackage().getId(), pojo2.getParentClass().getCompletePath(), pojo2.getParentClass().getClassName(), pojo2.getParentClass().getAPackage().getId(),pojo2.getInterfaces(), pojo2.getAttributes());

        assertEquals(pojo1, savedPojo1);
        assertEquals(pojo2, savedPojo2);

    }    @Test
    @DisplayName("Create a new Pojo from JSON")
    void createPojoFromJSON()  {
        var pojo = testingUtil.getPojo(Class.DEFAULT_CLASS.name);
        var savedPojo = pojoFacadeService.createPojo(pojo);
        assertEquals(pojo, savedPojo);
    }    @Test
    @DisplayName("Create a new Pojo from a PojoInfo")
    void createPojoFromPojoInfo()  {
       var pojoInfo = testingUtil.getPojoInfo(Class.DEFAULT_CLASS.name);
       var pojo = pojoFacadeService.createPojo(pojoInfo);
       assertEquals(testingUtil.getPojo(Class.DEFAULT_CLASS.name), pojo);
    }
    @Test
    @DisplayName("Create a new Attribute")
    void createAttribute()  {
        var pojo = testingUtil.getPojo(Class.DEFAULT_CLASS.name);
        var attribute = testingUtil.getAttribute(AttributeName.DEFAULT_CLASSNAME.name);
        var savedAttribute = pojoFacadeService.createAttribute(attribute.getName(), attribute.getClazz().getCompletePath(), attribute.getAccessModifier(), attribute.getClazz().getClassName(), attribute.getClazz().getAPackage().getId(), pojo.getCompletePath());
        assertEquals(attribute, savedAttribute);
    }
    @Test
    @DisplayName("Create a new Attribute but it already exists ")
    void createAttributeAlreadyExists()  {
        var pojo = testingUtil.getPojo(Class.DEFAULT_CLASS.name);
        var attribute = testingUtil.getAttribute(AttributeName.DEFAULT_CLASSNAME.name);
        pojoFacadeService.createAttribute(attribute.getName(), attribute.getClazz().getCompletePath(), attribute.getAccessModifier(), attribute.getClazz().getClassName(), attribute.getClazz().getAPackage().getId(), pojo.getCompletePath());
        var savedAttribute = pojoFacadeService.createAttribute(attribute.getName(), attribute.getClazz().getCompletePath(), attribute.getAccessModifier(), attribute.getClazz().getClassName(), attribute.getClazz().getAPackage().getId(), pojo.getCompletePath());

        assertEquals(attribute, savedAttribute);
    }
    @Test
    @DisplayName("Create a new Package")
    void createPackage()  {
        assertEquals(pojoFacadeService.createPackage("de"), new Package("de", "de", null) );

    }
    @Test
    @DisplayName("Create a new Package but it already exists")
    void createPackageAlreadyExists()  {
        assertThrows(PackageNameNotAllowed.class,() -> pojoFacadeService.createPackage("de..fh"));

    }

}
