package com.lumotalent.LumoTalent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class CurriculoIAService {

    private static final Logger logger = LoggerFactory.getLogger(CurriculoIAService.class);

    private final String apiKey;
    private final String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public CurriculoIAService(@Value("${gemini.api.key}") String apiKey,
                              @Value("${gemini.api.url}") String apiUrl,
                              WebClient.Builder webClientBuilder,
                              ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String analisarCurriculo(String textoCurriculo) {
        String prompt = """
            Analise o currículo abaixo e extraia as informações no seguinte formato JSON:
            {
              "nome": "",
              "email": "",
              "telefone": "",
              "endereco": "",
              "experiencia": "",
              "cursos": "",
              "outrasInformacoes": ""
            }
            Responda APENAS com o JSON, sem explicações.
            
            CURRÍCULO:
            """ + textoCurriculo;

        String requestBody = """
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }]
            }
            """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        try {
            if (apiKey == null || apiKey.isBlank() || apiUrl == null || apiUrl.isBlank()) {
                String erro = "Configuração da API Gemini ausente (apiKey/apiUrl)";
                logger.error(erro);
                throw new RuntimeException(erro);
            }

            logger.info("Chamando API Gemini para análise de currículo");

            String response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), (ClientResponse resp) ->
                            resp.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        String erro = "Erro da Gemini: " + resp.statusCode() + " - " + body;
                                        logger.error(erro);
                                        return Mono.error(new RuntimeException(erro));
                                    }))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .retryWhen(Retry.backoff(1, Duration.ofSeconds(2)).maxBackoff(Duration.ofSeconds(5)))
                    .block();

            if (response == null || response.isBlank()) {
                String erro = "Resposta vazia da API Gemini";
                logger.error(erro);
                throw new RuntimeException(erro);
            }

            logger.debug("Resposta da Gemini recebida com sucesso");

            JsonNode root = objectMapper.readTree(response);

            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                String erro = "Resposta da Gemini sem campo 'candidates' ou vazio: " + response;
                logger.error(erro);
                throw new RuntimeException(erro);
            }

            JsonNode content = candidates.get(0).path("content");
            JsonNode parts = content.path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                String erro = "Resposta da Gemini sem 'parts' no content: " + response;
                logger.error(erro);
                throw new RuntimeException(erro);
            }

            String text = parts.get(0).path("text").asText(null);
            if (text == null) {
                String erro = "Campo 'text' não encontrado na resposta da Gemini: " + response;
                logger.error(erro);
                throw new RuntimeException(erro);
            }

            logger.info("Análise de currículo concluída com sucesso");
            return text;

        } catch (Exception e) {
            logger.error("Erro ao chamar Gemini: ", e);
            throw new RuntimeException("Erro ao chamar Gemini: " + e.getMessage(), e);
        }
    }
}