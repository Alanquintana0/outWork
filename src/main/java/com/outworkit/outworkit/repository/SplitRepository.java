package com.outworkit.outworkit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.outworkit.outworkit.entity.Split;

@Repository
public interface SplitRepository extends JpaRepository<Split, Long>{
}
