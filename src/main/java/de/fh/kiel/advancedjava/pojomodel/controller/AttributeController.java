package de.fh.kiel.advancedjava.pojomodel.controller;

import de.fh.kiel.advancedjava.pojomodel.dto.AddAttributeDTO;
import de.fh.kiel.advancedjava.pojomodel.dto.AttributeDeleteDTO;
import de.fh.kiel.advancedjava.pojomodel.model.Attribute;
import de.fh.kiel.advancedjava.pojomodel.model.Pojo;
import de.fh.kiel.advancedjava.pojomodel.service.AttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("attribute")
public class AttributeController {
    private final AttributeService attributeService;

    AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @PostMapping("/{className}")
    public ResponseEntity<Pojo> addAttribute(@PathVariable String className, @RequestBody AddAttributeDTO addAttributeDTO) {
        return ResponseEntity.ok(this.attributeService.addAttribute(className, addAttributeDTO));
    }

    @PutMapping
    public ResponseEntity<Pojo> deleteAttribute(@RequestBody() AttributeDeleteDTO attributeDeleteDTO) {
        return ResponseEntity.ok(attributeService.deleteAttribute(attributeDeleteDTO));
    }
}

