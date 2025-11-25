package com.salo.sistemacreche.entidades;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "PRE_MATRICULA")
public class PreMatricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRE_MATRICULA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIANCA", nullable = false)
    private Crianca crianca;

    @Column(name = "DATA_PREMATRICULA", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataPreMatricula;

    @ManyToOne
    @JoinColumn(name = "SITUACAO_HABITACIONAL")
    private SituacaoHabitacional situacaoHabitacional;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO_PREMATRICULA")
    private SituacaoPreMatricula situacaoPreMatricula = SituacaoPreMatricula.EM_ANALISE;

    @Column(name = "OBSERVACAO", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "SERIE_TEMP")
    private String serieTemp;

    @Column(name = "ANO_LETIVO_TEMP")
    private Integer anoLetivoTemp;

    // Enum
    public enum SituacaoPreMatricula {
        APROVADA, EM_ANALISE, CANCELADA, REPROVADA
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

    public Date getDataPreMatricula() {
        return dataPreMatricula;
    }

    public void setDataPreMatricula(Date dataPreMatricula) {
        this.dataPreMatricula = dataPreMatricula;
    }

    public SituacaoHabitacional getSituacaoHabitacional() {
        return situacaoHabitacional;
    }

    public void setSituacaoHabitacional(SituacaoHabitacional situacaoHabitacional) {
        this.situacaoHabitacional = situacaoHabitacional;
    }

    public SituacaoPreMatricula getSituacaoPreMatricula() {
        return situacaoPreMatricula;
    }

    public void setSituacaoPreMatricula(SituacaoPreMatricula situacaoPreMatricula) {
        this.situacaoPreMatricula = situacaoPreMatricula;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    // Novos getters e setters
    public String getSerieTemp() {
        return serieTemp;
    }

    public void setSerieTemp(String serieTemp) {
        this.serieTemp = serieTemp;
    }

    public Integer getAnoLetivoTemp() {
        return anoLetivoTemp;
    }

    public void setAnoLetivoTemp(Integer anoLetivoTemp) {
        this.anoLetivoTemp = anoLetivoTemp;
    }
}