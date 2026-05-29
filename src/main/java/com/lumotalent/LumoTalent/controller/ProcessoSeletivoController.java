package com.lumotalent.LumoTalent.controller;

import com.lumotalent.LumoTalent.model.ProcessoSeletivo;
import com.lumotalent.LumoTalent.service.ProcessoSeletivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/processos")
public class ProcessoSeletivoController {

    private final ProcessoSeletivoService processoService;

    public ProcessoSeletivoController(ProcessoSeletivoService processoService) {
        this.processoService = processoService;
    }

    // Inicia processo seletivo
    @PostMapping("/iniciar")
    public ResponseEntity<ProcessoSeletivo> iniciar(
            @RequestParam Long vagaId,
            @RequestParam Long candidatoId) {
        try {
            return ResponseEntity.ok(processoService.iniciar(vagaId, candidatoId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Avança etapa do processo
    // Etapas: TRIAGEM → ENTREVISTA_RH → ENTREVISTA_TECNICA → APROVADO ou REPROVADO
    @PatchMapping("/{id}/etapa")
    public ResponseEntity<ProcessoSeletivo> avancarEtapa(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String novaEtapa = body.get("etapa");
            String observacoes = body.get("observacoes");
            return ResponseEntity.ok(processoService.avancarEtapa(id, novaEtapa, observacoes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lista todos os processos
    @GetMapping
    public ResponseEntity<List<ProcessoSeletivo>> listarTodos() {
        return ResponseEntity.ok(processoService.listarTodos());
    }

    // Lista processos por vaga
    @GetMapping("/vaga/{vagaId}")
    public ResponseEntity<List<ProcessoSeletivo>> listarPorVaga(@PathVariable Long vagaId) {
        return ResponseEntity.ok(processoService.listarPorVaga(vagaId));
    }

    // Busca processo por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProcessoSeletivo> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(processoService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}