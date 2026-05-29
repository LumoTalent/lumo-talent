package com.lumotalent.LumoTalent.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessoSeletivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vaga_id")
    private com.lumotalent.LumoTalent.model.Vaga vaga;

    @ManyToOne
    @JoinColumn(name = "candidato_id")
    private com.lumotalent.LumoTalent.model.Candidato candidato;

    // TRIAGEM, ENTREVISTA_RH, ENTREVISTA_TECNICA, APROVADO, REPROVADO
    private String etapaAtual;

    @Column(length = 3000)
    private String observacoes;
}