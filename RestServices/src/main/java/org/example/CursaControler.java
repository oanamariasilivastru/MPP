package org.example;

import model.Cursa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.RepoCursa;

import java.time.LocalDateTime;
import java.util.List;

public class CursaControler {
    private RepoCursa repoCursa;

    @GetMapping
    public ResponseEntity<List<Cursa>> getAllCurse() {
        List<Cursa> curse = (List<Cursa>) repoCursa.findAll();
        return ResponseEntity.ok(curse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cursa> getCursaById(@PathVariable Long id) {
        Cursa cursa = repoCursa.findOne(id);
        if (cursa != null) {
            return ResponseEntity.ok(cursa);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Long> createCursa(@RequestBody Cursa cursa) {
        try {
            System.out.println("Attempting to save new Cursa: " + cursa);            Cursa savedCursa = repoCursa.save(cursa);
            Long id = savedCursa.getId();
            System.out.println("Successfully saved Cursa with id: " + id);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (Exception e) {
            System.err.println("Error occurred while saving Cursa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCursa(@PathVariable Long id, @RequestBody Cursa cursa) {
        try {
            cursa.setId(id);
            repoCursa.update(cursa);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error occurred while updating Cursa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCursa(@PathVariable Long id) {
        try {
            repoCursa.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error occurred while deleting Cursa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
