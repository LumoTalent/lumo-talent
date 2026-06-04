package com.lumotalent.LumoTalent.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 3000)
    private String descricao;

    // ABERTA, EM_ANDAMENTO, ENCERRADA
    private String status;

    private String setor;
    private String area;
    private String gestor;

    @ManyToMany
    @JoinTable(
            name = "vaga_candidatos",
            joinColumns = @JoinColumn(name = "vaga_id"),
            inverseJoinColumns = @JoinColumn(name = "candidato_id")
    )
    @ToString.Exclude
    private List<com.lumotalent.LumoTalent.model.Candidato> candidatos;
}