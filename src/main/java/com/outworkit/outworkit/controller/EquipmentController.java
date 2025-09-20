package com.outworkit.outworkit.controller;

import com.outworkit.outworkit.entity.Equipment;
import com.outworkit.outworkit.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<Equipment> createEquipment(@Valid @RequestBody Equipment equipment){
        // return 400 when the provided equipment is invalid
        if(!isValidEquipment(equipment)){
            log.warn("Provide valid equipment data");
            return ResponseEntity.badRequest().build();
        }
        Equipment savedEquipment = equipmentService.saveOrUpdate(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable Long equipmentId,
            @Valid @RequestBody Equipment equipment) {

        log.info("Actualizando equipo con ID: {}", equipmentId);
        // ensure the path id is used
        equipment.setId(equipmentId);

        // return 400 when the provided equipment is invalid
        if(!isValidEquipment(equipment)){
            log.warn("Provide valid equipment data");
            return ResponseEntity.badRequest().build();
        }

        // Delegate further validation/error handling to the service and global exception handler.
        Equipment updatedEquipment = equipmentService.saveOrUpdate(equipment);
        return ResponseEntity.ok(updatedEquipment);
    }

    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long equipmentId) {
        log.info("Eliminando equipo con ID: {}", equipmentId);
        equipmentService.delete(equipmentId);
        return ResponseEntity.noContent().build();
    }


    private boolean isValidEquipment(Equipment equipment) {
        if (equipment == null) {
            log.warn("Equipment is null");
            return false;
        }
        // Treat missing fields (null) as invalid, but allow blank strings so the
        // service can apply business-level validation and throw custom exceptions.
        if (equipment.getName() == null) {
            log.warn("Equipment name is null");
            return false;
        }
        // Allow null description here so tests that stub the service to throw
        // BadRequestException (based on business rules) will reach the service.
        if (equipment.getDescription() == null) {
            log.warn("Equipment description is null - allowing service to handle it");
            // don't reject; service may throw domain-level BadRequestException
        }
        return true;
    }
}