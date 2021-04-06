package de.fh.kiel.advancedjava.pojomodel.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Objects;
import java.util.Set;

@Node
public class Pojo {

    @Id
    @GeneratedValue
    private Long id;

    private String className;
    private String packageName;

    @Relationship(type = "attributes")
    private Set<Attribute> attributes;

    @Relationship(type = "parent")
    private Pojo parentClass;

    @Relationship(type = "interfaces")
    private Set<String> interfaces;

    public Pojo() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public Pojo(String className, String packageName, Set<Attribute> attributes, Pojo parentClass, Set<String> interfaces) {
        this.id = id;
        this.className = className;
        this.packageName = packageName;
        this.attributes = attributes;
        this.parentClass = parentClass;
        this.interfaces = interfaces;
    }
    public Pojo(String className, String packageName){
        this.className = className;
        this.packageName = packageName;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Pojo getParentClass() {
        return parentClass;
    }

    public void setParentClass(Pojo parentClass) {
        this.parentClass = parentClass;
    }

    public Set<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<String> interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pojo pojo = (Pojo) o;
        return Objects.equals(id, pojo.id) &&
                Objects.equals(className, pojo.className) &&
                Objects.equals(packageName, pojo.packageName) &&
                Objects.equals(attributes, pojo.attributes) &&
                Objects.equals(parentClass, pojo.parentClass) &&
                Objects.equals(interfaces, pojo.interfaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, className, packageName, attributes, parentClass, interfaces);
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", attributes=" + attributes +
                ", parentClass='" + parentClass + '\'' +
                ", interfaces=" + interfaces +
                '}';
    }
}
