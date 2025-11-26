package com.salo.sistemacreche.entidades;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "MEMBRO_FAMILIA")
public class MembroFamilia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEMBRO_FAMILIA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIANCA", nullable = false)
    private Crianca crianca;

    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Column(name = "IDADE", nullable = false)
    private Integer idade;

    @Enumerated(EnumType.STRING)
    @Column(name = "PARENTESCO")
    private Parentesco parentesco;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO_ESCOLAR")
    private SituacaoEscolar situacaoEscolar;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO_EMPREGO")
    private SituacaoEmprego situacaoEmprego;

    @Column(name = "RENDA", nullable = false, precision = 10, scale = 2)
    private BigDecimal renda;

    // Enums
    public enum Parentesco {
        MAE, PAI, IRMAO, IRMA, AVO, TIO, TIA, RESPONSAVEL_LEGAL, OUTRO
    }

    public enum SituacaoEscolar {
        CRECHE, PRE_ESCOLA,
        ENSINO_FUNDAMENTAL_1_1_5_ANO,
        ENSINO_FUNDAMENTAL_2_6_9_ANO,
        ENSINO_FUNDAMENTAL_INCOMPLETO,
        ENSINO_FUNDAMENTAL_COMPLETO,
        ENSINO_MEDIO_INCOMPLETO,
        ENSINO_MEDIO_COMPLETO,
        ENSINO_TECNICO_INTEGRADO_MEDIO,
        ENSINO_MEDIO_PROFISSIONALIZANTE,
        ENSINO_SUPERIOR_INCOMPLETO,
        ENSINO_SUPERIOR_COMPLETO,
        POS_GRADUACAO_LATO_SENSU_ESPECIALIZACAO,
        MESTRADO, DOUTORADO, POS_DOUTORADO,
        EJA_FUNDAMENTAL, EJA_MEDIO,
        ALFABETIZACAO_DE_ADULTOS,
        ANALFABETO, NAO_INFORMADO
    }

    public enum SituacaoEmprego {
        EMPREGADO_FORMAL, EMPREGADO_INFORMAL, SERVIDOR_PUBLICO,
        AUTONOMO, EMPREGADOR, APOSENTADO, PENSIONISTA,
        DESEMPREGADO, DO_LAR, ESTUDANTE, BOLSISTA,
        INCAPAZ_TRABALHAR, OUTRO
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Crianca getCrianca() {
        return crianca;
    }

    public void setCrianca(Crianca crianca) {
        this.crianca = crianca;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public SituacaoEscolar getSituacaoEscolar() {
        return situacaoEscolar;
    }

    public void setSituacaoEscolar(SituacaoEscolar situacaoEscolar) {
        this.situacaoEscolar = situacaoEscolar;
    }

    public SituacaoEmprego getSituacaoEmprego() {
        return situacaoEmprego;
    }

    public void setSituacaoEmprego(SituacaoEmprego situacaoEmprego) {
        this.situacaoEmprego = situacaoEmprego;
    }

    public BigDecimal getRenda() {
        return renda;
    }

    public void setRenda(BigDecimal renda) {
        this.renda = renda;
    }
}
