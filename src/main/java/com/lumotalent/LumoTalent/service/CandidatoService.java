package com.lumotalent.LumoTalent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumotalent.LumoTalent.model.Candidato;
import com.lumotalent.LumoTalent.repository.CandidatoRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final CurriculoIAService curriculoIAService;
    private final ObjectMapper objectMapper;

    public CandidatoService(CandidatoRepository candidatoRepository,
                            CurriculoIAService curriculoIAService,
                            ObjectMapper objectMapper) {
        this.candidatoRepository = candidatoRepository;
        this.curriculoIAService = curriculoIAService;
        this.objectMapper = objectMapper;
    }

    public Candidato processarCurriculo(MultipartFile arquivo) throws Exception {

        // 1 - Extrai o texto do PDF com PDFBox
        PDDocument document = PDDocument.load(arquivo.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String textoCurriculo = stripper.getText(document);
        document.close();

        // 2 - Manda para o Gemini analisar
        String respostaIA = curriculoIAService.analisarCurriculo(textoCurriculo);

        // 3 - Limpa o JSON
        String jsonLimpo = respostaIA
                .replace("```json", "")
                .replace("```", "")
                .trim();

        // 4 - Converte para objeto Candidato
        JsonNode json = objectMapper.readTree(jsonLimpo);

        Candidato candidato = Candidato.builder()
                .nome(json.path("nome").asText())
                .email(json.path("email").asText())
                .telefone(json.path("telefone").asText())
                .endereco(json.path("endereco").asText())
                .experiencia(json.path("experiencia").asText())
                .cursos(json.path("cursos").asText())
                .outrasInformacoes(json.path("outrasInformacoes").asText())
                .caminhoArquivoCurriculo(arquivo.getOriginalFilename())
                .build();

        // 5 - Salva no banco
        return candidatoRepository.save(candidato);
    }

    public List<Candidato> listarTodos() {
        return candidatoRepository.findAll();
    }

    public Candidato buscarPorId(Long id) {
        return candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"));
    }

    public void deletar(Long id) {
        candidatoRepository.deleteById(id);
    }
}