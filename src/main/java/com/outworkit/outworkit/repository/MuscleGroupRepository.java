package com.outworkit.outworkit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.outworkit.outworkit.entity.MuscleGroup;

@Repository
public interface MuscleGroupRepository extends JpaRepository<MuscleGroup, Long>{}
