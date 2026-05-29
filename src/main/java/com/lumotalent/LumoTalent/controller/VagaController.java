package com.lumotalent.LumoTalent.controller;

import com.lumotalent.LumoTalent.model.Vaga;
import com.lumotalent.LumoTalent.service.VagaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vagas")
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @PostMapping
    public ResponseEntity<Vaga> criar(@RequestBody Vaga vaga) {
        return ResponseEntity.ok(vagaService.criar(vaga));
    }

    @GetMapping
    public ResponseEntity<List<Vaga>> listarTodas() {
        return ResponseEntity.ok(vagaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(vagaService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Vaga> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(vagaService.atualizarStatus(id, status));
    }

    @PostMapping("/{vagaId}/candidatos/{candidatoId}")
    public ResponseEntity<Vaga> adicionarCandidato(@PathVariable Long vagaId, @PathVariable Long candidatoId) {
        try {
            return ResponseEntity.ok(vagaService.adicionarCandidato(vagaId, candidatoId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        vagaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}