package com.lumotalent.LumoTalent.service;

import com.lumotalent.LumoTalent.model.Candidato;
import com.lumotalent.LumoTalent.model.Vaga;
import com.lumotalent.LumoTalent.repository.CandidatoRepository;
import com.lumotalent.LumoTalent.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;

    public VagaService(VagaRepository vagaRepository, CandidatoRepository candidatoRepository) {
        this.vagaRepository = vagaRepository;
        this.candidatoRepository = candidatoRepository;
    }

    public Vaga criar(Vaga vaga) {
        vaga.setStatus("ABERTA");
        return vagaRepository.save(vaga);
    }

    public List<Vaga> listarTodas() {
        return vagaRepository.findAll();
    }

    public Vaga buscarPorId(Long id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));
    }

    public Vaga atualizarStatus(Long id, String status) {
        Vaga vaga = buscarPorId(id);
        vaga.setStatus(status);
        return vagaRepository.save(vaga);
    }

    public Vaga adicionarCandidato(Long vagaId, Long candidatoId) {
        Vaga vaga = buscarPorId(vagaId);
        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"));
        vaga.getCandidatos().add(candidato);
        return vagaRepository.save(vaga);
    }

    public void deletar(Long id) {
        vagaRepository.deleteById(id);
    }
}