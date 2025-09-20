package com.outworkit.outworkit.repository;

import com.outworkit.outworkit.entity.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuscleRepository extends JpaRepository<Muscle, Long>{
}
