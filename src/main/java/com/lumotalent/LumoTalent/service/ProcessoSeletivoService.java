package com.lumotalent.LumoTalent.service;

import com.lumotalent.LumoTalent.model.Candidato;
import com.lumotalent.LumoTalent.model.ProcessoSeletivo;
import com.lumotalent.LumoTalent.model.Vaga;
import com.lumotalent.LumoTalent.repository.CandidatoRepository;
import com.lumotalent.LumoTalent.repository.ProcessoSeletivoRepository;
import com.lumotalent.LumoTalent.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessoSeletivoService {

    private final ProcessoSeletivoRepository processoRepository;
    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;

    public ProcessoSeletivoService(ProcessoSeletivoRepository processoRepository,
                                   VagaRepository vagaRepository,
                                   CandidatoRepository candidatoRepository) {
        this.processoRepository = processoRepository;
        this.vagaRepository = vagaRepository;
        this.candidatoRepository = candidatoRepository;
    }

    // Inicia o processo seletivo de um candidato em uma vaga
    public ProcessoSeletivo iniciar(Long vagaId, Long candidatoId) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"));

        ProcessoSeletivo processo = ProcessoSeletivo.builder()
                .vaga(vaga)
                .candidato(candidato)
                .etapaAtual("TRIAGEM")
                .observacoes("")
                .build();

        return processoRepository.save(processo);
    }

    // Avança a etapa do processo
    public ProcessoSeletivo avancarEtapa(Long processoId, String novaEtapa, String observacoes) {
        ProcessoSeletivo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));

        processo.setEtapaAtual(novaEtapa);
        if (observacoes != null && !observacoes.isEmpty()) {
            processo.setObservacoes(observacoes);
        }

        return processoRepository.save(processo);
    }

    public List<ProcessoSeletivo> listarTodos() {
        return processoRepository.findAll();
    }

    public List<ProcessoSeletivo> listarPorVaga(Long vagaId) {
        return processoRepository.findAll().stream()
                .filter(p -> p.getVaga().getId().equals(vagaId))
                .toList();
    }

    public ProcessoSeletivo buscarPorId(Long id) {
        return processoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
    }
}