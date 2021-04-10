package de.fh.kiel.advancedjava.pojomodel.repository;

import de.fh.kiel.advancedjava.pojomodel.model.Attribute;
import de.fh.kiel.advancedjava.pojomodel.model.Pojo;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface PojoRepository extends Neo4jRepository<Pojo, String> {
    boolean existsByClassName(String name);
    Optional<Pojo> findByClassName(String name);
}
