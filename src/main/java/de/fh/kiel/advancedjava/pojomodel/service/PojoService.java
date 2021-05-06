package de.fh.kiel.advancedjava.pojomodel.service;

import de.fh.kiel.advancedjava.pojomodel.dto.AttributeChangeDTO;
import de.fh.kiel.advancedjava.pojomodel.dto.PojoEmptyHullDTO;
import de.fh.kiel.advancedjava.pojomodel.exception.AttributeDoesNotExist;
import de.fh.kiel.advancedjava.pojomodel.exception.PojoAlreadyExists;
import de.fh.kiel.advancedjava.pojomodel.exception.PojoDoesNotExist;
import de.fh.kiel.advancedjava.pojomodel.model.Attribute;
import de.fh.kiel.advancedjava.pojomodel.model.AttributeInfo;
import de.fh.kiel.advancedjava.pojomodel.model.Pojo;
import de.fh.kiel.advancedjava.pojomodel.model.PojoInfo;
import de.fh.kiel.advancedjava.pojomodel.repository.AttributeRepository;
import de.fh.kiel.advancedjava.pojomodel.repository.PojoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PojoService {


    PojoRepository pojoRepository;
    ASMWrapperService asmWrapperService;
    AttributeService attributeService;
    private final PackageService packageService;
    private final AttributeRepository attributeRepository;

    PojoService(PackageService packageService, PojoRepository pojoRepository, ASMWrapperService asmWrapperService, AttributeService attributeService, AttributeRepository attributeRepository
    ){
        this.packageService =packageService;
        this.pojoRepository = pojoRepository;
        this.asmWrapperService = asmWrapperService;
        this.attributeService = attributeService;
        this.attributeRepository = attributeRepository;
    }



     public Pojo readByteCodeAndCreatePojo(byte[] clazz){

           var pojoInfo = this.asmWrapperService.read(clazz);

            var pojo = createPojoFromPojoInfo(pojoInfo);

            pojoRepository.save(pojo);

            return pojo;
    }
    public Pojo createPojoFromPojoInfo(PojoInfo pojoInfo){
        var pojo = pojoRepository.findById(pojoInfo.getCompletePath()).orElse(Pojo.builder().completePath(pojoInfo.getCompletePath())
                .className(pojoInfo.getClassName())
                .aPackage(packageService.createPackage(pojoInfo.getPackageName()))
            .build());
        var superClass = getSuperClass(pojoInfo.getParentClassCompletePath(),pojoInfo.getParentClassName(), pojoInfo.getParentClassPackageName());

        pojo.setParentClass(superClass);
        pojo.setInterfaces(pojoInfo.getInterfaces());
        Set<Attribute> attributes = constructAttributesFromAttributesInfo(pojoInfo.getAttributes(), pojoInfo.getCompletePath());
        pojo.setAttributes(attributes);
        pojo.setEmptyHull(false);

       return pojo;
    }

    private String buildCompletePath(String packageName, String className){
            return packageName + "." + className;
    }
    public Pojo createPojoEmptyHullFromJSON(PojoEmptyHullDTO emptyHull){
        var completePath = buildCompletePath(emptyHull.getPackageName(), emptyHull.getClassName());

        if ( pojoRepository.existsById(completePath) )
            throw new PojoAlreadyExists(completePath);

       var pojo = Pojo.builder()
               .emptyHull(true)
               .completePath(completePath)
               .className(emptyHull.getClassName())
               .aPackage(this.packageService.createPackage(emptyHull.getPackageName()))
               .build();

       return pojoRepository.save(pojo);
    }
    private Set<Attribute> constructAttributesFromAttributesInfo(Set<AttributeInfo> attributeInfos, String completePath){
        Set<Attribute> attributes =   attributeInfos.stream().map(a -> attributeService.createAttribute(a.getName(), a.getDataTypeName(), a.getAccessModifier(), a.getClassName(), a.getPackageName())).collect(Collectors.toSet());
        attributes = setIdOfAttributes(attributes, completePath);
        return attributes;
    }
    private Set<Attribute>   setIdOfAttributes(Set<Attribute> attributes, String completePath){
        return attributes.stream().map(attribute -> {  attribute.setId(completePath + "" + attribute.getName()); return attribute; }).collect(Collectors.toSet());
    }
    public void deletePojo(String pojoName){
        var pojo = pojoRepository.findById(pojoName).orElseThrow(() -> new PojoDoesNotExist(pojoName));
     if( attributeRepository.findAllByClazz_CompletePath(pojoName).isEmpty() ){
         pojo.setAttributes(Collections.emptySet());
         pojo.setEmptyHull(true);
         pojoRepository.deleteById(pojo.getCompletePath());
         pojoRepository.save(pojo);
     } else
         pojoRepository.deleteById(pojo.getCompletePath());
    }
    public Pojo changeAttribute(AttributeChangeDTO attributeChangeDTO){
        var pojo = pojoRepository.findById(attributeChangeDTO.getClassName()).orElseThrow(() -> new PojoDoesNotExist(attributeChangeDTO.getClassName()));

            var attr =  pojo.getAttributes().stream().filter(attribute-> attribute.getName().equals(attributeChangeDTO.getAttributeName())).findFirst().orElseThrow(() -> new AttributeDoesNotExist(attributeChangeDTO.getAttributeName(), attributeChangeDTO.getClassName()));

                pojo.getAttributes().remove(attr);
                pojoRepository.deleteById(pojo.getCompletePath());
                pojoRepository.save(pojo);
               return pojo;
    }
    private Pojo getSuperClass(String completePath, String className, String packageName){
      return pojoRepository.findById(completePath).orElseGet(() -> Pojo.builder()
                .completePath(completePath)
                .className(className)
                .aPackage(this.packageService.createPackage(packageName))
                .emptyHull(true).build());
    }
    public Pojo getPojo(String completePath){
        return pojoRepository.findById(completePath).orElseThrow(() -> new PojoDoesNotExist(completePath));
    }
}


