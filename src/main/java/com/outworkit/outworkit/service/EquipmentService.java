package com.outworkit.outworkit.service;

import com.outworkit.outworkit.controller.exception.BadRequestException;
import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.Equipment;
import com.outworkit.outworkit.repository.EquipmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class EquipmentService {

    EquipmentRepository equipmentRepository;

    public List<Equipment> getEquipments(){
        log.debug("Getting equipments");
        List<Equipment> equipments = equipmentRepository.findAll();
        log.info("Equipments retrieved");
        return equipments;
    }

    public Equipment getEquipment(Long id){

        log.debug("Looking for equipment with ID: {}", id);

        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        return equipmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Equipment not found with id {}", id);
                    return new ResourceNotFoundException(
                            String.format("Equipment not found with ID: %d", id)
                    );
                });
    }

    public Optional<Equipment> findEquipmentById(Long equipmentId) {
        log.debug("Searching for equipment with ID: {}", equipmentId);

        if (equipmentId == null) {
            return Optional.empty();
        }

        return equipmentRepository.findById(equipmentId);
    }

    @Transactional
    public Equipment saveOrUpdate(Equipment equipment) {
        log.debug("Saving/updating equipment: {}", equipment);

        validateEquipment(equipment);

        if (equipment.getId() != null) {
            if (!equipmentRepository.existsById(equipment.getId())) {
                throw new ResourceNotFoundException(
                        String.format("Cant update equipment not found with ID: %d", equipment.getId())
                );
            }
            log.info("Updating existing equipment ID: {}", equipment.getId());
        } else {
            log.info("Creating a new equipment: {}", equipment.getName());
        }

        Equipment savedEquipment = equipmentRepository.save(equipment);
        log.info("Equipment saved successfully ID: {}", savedEquipment.getId());

        return savedEquipment;
    }

    public void delete(Long id){
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Equipment not found with ID: %d", id));
        }
        equipmentRepository.deleteById(id);
    }

    private void validateEquipment(Equipment equipment) {
        if (equipment == null) {
            throw new BadRequestException("equipment cant be null");
        }

        if (equipment.getName() == null || equipment.getName().trim().isEmpty()) {
            throw new BadRequestException("equipment name is required");
        }

        if (equipment.getName().trim().length() > 255) {
            throw new BadRequestException("equipment name cant exceed 255");
        }

        Optional<Equipment> existingEquipment = equipmentRepository.findByNameIgnoreCase(equipment.getName().trim());
        if (existingEquipment.isPresent() &&
                !existingEquipment.get().getId().equals(equipment.getId())) {
            throw new BadRequestException(
                    String.format("Theres already an equipment with that name: %s", equipment.getName())
            );
        }

        log.debug("equipment validation performed successfully.");
    }
}
