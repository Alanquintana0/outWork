package com.outworkit.outworkit.service;

import com.outworkit.outworkit.entity.Equipment;
import com.outworkit.outworkit.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {
    @Autowired
    EquipmentRepository equipmentRepository;

    public List<Equipment> getEquipments(){
        return equipmentRepository.findAll();
    }

    public  Optional<Equipment> getEquipment(Long id){
        return equipmentRepository.findById(id);
    }
}
