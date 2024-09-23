package org.example;

import model.Cursa;
import java.time.LocalDateTime;

public class StartClient {
    public static void main(String[] args) {
        // Creare instanță a clasei de test
        CursaControllerTest test = new CursaControllerTest();

        // Testează funcționalitatea de a găsi toate cursele
        System.out.println("=== Find All Curse ===");
        try {
            Cursa[] allCurse = test.findAll();
            for (Cursa cursa : allCurse) {
                System.out.println(cursa);
            }
        } catch (Exception e) {
            System.err.println("Error fetching all curse: " + e.getMessage());
        }
        System.out.println("=======================\n");

        // Testează funcționalitatea de a găsi o cursă după id

        // Testează funcționalitatea de a salva o cursă nouă
        System.out.println("=== Save New Cursa ===");
        Cursa newCursa = new Cursa("Oras B", LocalDateTime.now()); // Cursa de salvat
        Long savedId = null;
        try {
            savedId = test.save(newCursa);
            System.out.println("New Cursa saved with id: " + savedId);
        } catch (RuntimeException e) {
            System.err.println("Failed to save new Cursa: " + e.getMessage());
        }
        System.out.println("=======================\n");

        System.out.println("=== Find One Cursa ===");
        Long id = savedId; // Id-ul cursei de găsit
        try {
            Cursa foundCursa = test.findOne(id);
            System.out.println(foundCursa);
        } catch (Exception e) {
            System.err.println("Error fetching cursa with id " + id + ": " + e.getMessage());
        }
        System.out.println("=======================\n");
        if (savedId != null) {
            // Testează funcționalitatea de a actualiza cursa nou creată
            System.out.println("=== Update Cursa ===");
            try {
                Cursa cursaToUpdate = test.findOne(savedId);
                // Actualizează atributele cursei
                cursaToUpdate.setDestinatie("New Destination");
                test.update(savedId, cursaToUpdate);
                System.out.println("Cursa updated successfully");
            } catch (Exception e) {
                System.err.println("Error updating cursa with id " + savedId + ": " + e.getMessage());
            }
            System.out.println("=======================\n");

            // Testează funcționalitatea de a șterge cursa nou creată
            System.out.println("=== Delete Cursa ===");
            try {
                test.delete(savedId);
                System.out.println("Cursa deleted successfully");
            } catch (Exception e) {
                System.err.println("Error deleting cursa with id " + savedId + ": " + e.getMessage());
            }
            System.out.println("=======================\n");
        } else {
            System.err.println("Skipping update and delete tests due to failure in saving new Cursa.");
        }
    }
}
