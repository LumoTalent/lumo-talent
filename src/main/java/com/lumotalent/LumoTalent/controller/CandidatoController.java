package com.lumotalent.LumoTalent.controller;

import com.lumotalent.LumoTalent.model.Candidato;
import com.lumotalent.LumoTalent.service.CandidatoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;
    private static final Logger logger = LoggerFactory.getLogger(CandidatoController.class);

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCurriculo(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            if (arquivo.isEmpty()) {
                logger.warn("Tentativa de upload com arquivo vazio");
                return ResponseEntity.badRequest().body(Map.of("erro", "Arquivo vazio"));
            }
            
            Candidato candidato = candidatoService.processarCurriculo(arquivo);
            return ResponseEntity.ok(Map.of("mensagem", "Currículo processado com sucesso", "candidatoId", candidato.getId().toString()));
        } catch (RuntimeException e) {
            logger.error("Erro ao processar upload de currículo - RuntimeException", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar currículo", "detalhes", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao processar upload de currículo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar arquivo", "detalhes", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Candidato>> listarTodos() {
        return ResponseEntity.ok(candidatoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidato> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(candidatoService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        candidatoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}