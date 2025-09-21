package com.outworkit.outworkit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outworkit.outworkit.entity.Split;
import com.outworkit.outworkit.service.SplitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/splits")
@RequiredArgsConstructor
@Slf4j
public class SplitController {
    
    private final SplitService splitService;

    @GetMapping
    public ResponseEntity<List<Split>> getAllSplits() {
        return ResponseEntity.ok(splitService.getAllSplits());
    }

    @GetMapping("/{splitId}")
    public ResponseEntity<Split> getSplitById(Long splitId){
        return ResponseEntity.ok(splitService.getSplitById(splitId));
    }

    @PostMapping
    public ResponseEntity<Split> createSplit(@Valid @RequestBody Split split){
        Split newSplit = splitService.saveOrUpdateSplit(split);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSplit);
    }

    @PutMapping("/{splitId}")
    public ResponseEntity<Split> updateSplit(@PathVariable Long splitId, 
                                            @Valid @RequestBody Split split){
        split.setId(splitId);
        Split updatedSplit = splitService.saveOrUpdateSplit(split);
        return ResponseEntity.ok(updatedSplit);
    }

    @DeleteMapping("/{splitId}")
    public ResponseEntity<Void> deleteSplit(@PathVariable Long splitId){
        splitService.delete(splitId);
        return ResponseEntity.noContent().build();
    }
    
}
