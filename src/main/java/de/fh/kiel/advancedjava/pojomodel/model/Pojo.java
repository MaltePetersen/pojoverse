package de.fh.kiel.advancedjava.pojomodel.model;

import lombok.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Node
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pojo {

    private boolean emptyHull;

    @Id
    private String completePath;
    private String className;
    private Package aPackage;


    @Relationship(type = "attributes")
    private Set<Attribute> attributes = new HashSet<>();

    @Relationship(type = "parent")
    private Pojo parentClass;

    @Relationship(type = "interfaces")
    private Set<String> interfaces = new HashSet<>();


    public Pojo(String completePath, String className, Package aPackage) {
        this.emptyHull = true;
        this.completePath = completePath;
        this.className = className;
        this.aPackage = aPackage;
    }




}
