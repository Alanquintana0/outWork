package com.outworkit.outworkit.controller;

import com.outworkit.outworkit.entity.Split;
import com.outworkit.outworkit.service.SplitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/splits")
@RequiredArgsConstructor
@Slf4j
public class SplitController {
    
    private final SplitService splitService;

    @GetMapping
    public ResponseEntity<List<Split>> getAllSplits() {
        log.info("Solicitando todos los splits");
        List<Split> splits = splitService.getAllSplits();
        return ResponseEntity.ok(splits);
    }

    @GetMapping("/{splitId}")
    public ResponseEntity<Split> getSplitById(@PathVariable Long splitId) {
        log.info("Solicitando split con ID: {}", splitId);
        Split split = splitService.getSplitById(splitId);
        return ResponseEntity.ok(split);
    }

    @PostMapping
    public ResponseEntity<Split> createSplit(@Valid @RequestBody Split split) {
        // return 400 when the provided split is invalid
        if (!isValidSplit(split)) {
            log.warn("Provide valid split data");
            return ResponseEntity.badRequest().build();
        }
        Split savedSplit = splitService.saveOrUpdateSplit(split);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSplit);
    }

    @PutMapping("/{splitId}")
    public ResponseEntity<Split> updateSplit(
            @PathVariable Long splitId, 
            @Valid @RequestBody Split split) {

        log.info("Actualizando split con ID: {}", splitId);
        // ensure the path id is used
        split.setId(splitId);

        // return 400 when the provided split is invalid
        if (!isValidSplit(split)) {
            log.warn("Provide valid split data");
            return ResponseEntity.badRequest().build();
        }

        // Delegate further validation/error handling to the service and global exception handler.
        Split updatedSplit = splitService.saveOrUpdateSplit(split);
        return ResponseEntity.ok(updatedSplit);
    }

    @DeleteMapping("/{splitId}")
    public ResponseEntity<Void> deleteSplit(@PathVariable Long splitId) {
        log.info("Eliminando split con ID: {}", splitId);
        splitService.delete(splitId);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidSplit(Split split) {
        if (split == null) {
            log.warn("Split is null");
            return false;
        }
        // Treat missing fields (null) as invalid, but allow blank strings so the
        // service can apply business-level validation and throw custom exceptions.
        if (split.getName() == null) {
            log.warn("Split name is null");
            return false;
        }
        // Allow null description here so tests that stub the service to throw
        // BadRequestException (based on business rules) will reach the service.
        if (split.getDescription() == null) {
            log.warn("Split description is null - allowing service to handle it");
            // don't reject; service may throw domain-level BadRequestException
        }
        return true;
    }
    
}
