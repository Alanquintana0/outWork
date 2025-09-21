package com.outworkit.outworkit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.Split;
import com.outworkit.outworkit.repository.SplitRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


//Faltan validaciones de objetos de tipo Split, solo permitir que splits validos sean guardados o actualizados (No campos nulo, etc).
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SplitService {
    
    SplitRepository splitRepository;

    public List<Split> getAllSplits(){
        try{
            return splitRepository.findAll();
        }catch(Exception e){
            log.error("Error retrieving splits", e);
            throw e;
        }
    }

    public Split getSplitById(Long id){
        try{
            if(id == null){
                throw new IllegalArgumentException("Id cannot be null");
            }
            return splitRepository.findById(id).orElse(null);
        }catch(Exception e){
            log.error("Error retrieving split with id {}", id, e);
            throw e;
        }
    }
    
    @Transactional
    public Split saveOrUpdateSplit(Split split){
        try{
            if(split.getId() != null){
                if(!splitRepository.existsById(split.getId())){
                    throw new IllegalArgumentException("Split with ID " + split.getId() + " does not exist.");
                }
                log.info("Updating split with ID: {}", split.getId());
            }else{
                log.info("Creating new split");
            }

            return splitRepository.save(split);
        }catch(Exception e){
            log.error("Error saving or updating split", e);
            throw e;
        }
    }

    public void delete(Long id){
        try{
            if(!splitRepository.existsById(id)){
                throw new ResourceNotFoundException(String.format("Equipment not found with ID: %d", id));
            }
            splitRepository.deleteById(id);
        }catch(Exception e){
            log.error("Error deleting split with id {}", id, e);
            throw e;
        }
    }
}
