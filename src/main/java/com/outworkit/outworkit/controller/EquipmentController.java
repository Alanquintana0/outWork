package com.outworkit.outworkit.controller;

import com.outworkit.outworkit.entity.Equipment;
import com.outworkit.outworkit.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
@Slf4j
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipments() {
        log.info("Solicitando todos los equipos");
        List<Equipment> equipments = equipmentService.getEquipments();
        return ResponseEntity.ok(equipments);
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long equipmentId) {
        log.info("Solicitando equipo con ID: {}", equipmentId);
        Equipment equipment = equipmentService.getEquipment(equipmentId);
        return ResponseEntity.ok(equipment);
    }

    @PostMapping
    public ResponseEntity<Equipment> createEquipment(@Valid @RequestBody Equipment equipment) {
        log.info("Creando nuevo equipo: {}", equipment.getName());
        Equipment savedEquipment = equipmentService.saveOrUpdate(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable Long equipmentId,
            @Valid @RequestBody Equipment equipment) {
        log.info("Actualizando equipo con ID: {}", equipmentId);
        equipment.setId(equipmentId); // Asegurar que el ID coincida
        Equipment updatedEquipment = equipmentService.saveOrUpdate(equipment);
        return ResponseEntity.ok(updatedEquipment);
    }

    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long equipmentId) {
        log.info("Eliminando equipo con ID: {}", equipmentId);
        equipmentService.delete(equipmentId);
        return ResponseEntity.noContent().build();
    }
}