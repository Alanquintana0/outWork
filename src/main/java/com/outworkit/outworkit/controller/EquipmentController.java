package com.outworkit.outworkit.controller;


import com.outworkit.outworkit.entity.Equipment;
import com.outworkit.outworkit.repository.EquipmentRepository;
import com.outworkit.outworkit.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/equipment")
@CrossOrigin(origins = "http://localhost:3000/")
public class EquipmentController {

    @Autowired
    private final EquipmentService equipmentService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    public EquipmentController(EquipmentService equipmentService){
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> getAll(){
        return equipmentService.getEquipments();
    }

}
