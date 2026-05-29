package com.lumotalent.LumoTalent.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String endereco;

    @Column(length = 5000)
    private String experiencia;

    @Column(length = 5000)
    private String cursos;

    @Column(length = 5000)
    private String outrasInformacoes;

    private String caminhoArquivoCurriculo;
}