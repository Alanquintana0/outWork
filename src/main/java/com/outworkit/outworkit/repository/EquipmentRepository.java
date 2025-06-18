package com.outworkit.outworkit.repository;

import com.outworkit.outworkit.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByNameIgnoreCase(String name);
    List<Equipment> findByNameContainingIgnoreCase(String name);
}
