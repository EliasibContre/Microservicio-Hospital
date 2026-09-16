package com.cesar.commons.controller;

import com.cesar.commons.service.CrudServices;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Validated
public class CrudController<RQ, RS, S extends CrudServices<RQ,RS>>{
    protected final S services;
    @GetMapping
    public ResponseEntity<List<RS>> listar(){
        return ResponseEntity.ok(services.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RS>obtenerPorId(
            @PathVariable @Positive(message = "el id debe ser positivo")Long id
    ){
        return ResponseEntity.ok(services.obtenerPorId(id));
    }
    @PostMapping
    public ResponseEntity<RS> Registrar(
            @Valid @RequestBody RQ request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(services.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RS>actualizar(
            @PathVariable @Positive(message = "el id debe ser positivo")Long id,
            @Valid @RequestBody RQ request
    ){
        return ResponseEntity.ok(services.actualizar(request,id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void>eliminar(
            @PathVariable @Positive(message = "el id debe ser positivo")Long id
    ){
        services.eliminar(id);
        return ResponseEntity.noContent().build();
    }


}
