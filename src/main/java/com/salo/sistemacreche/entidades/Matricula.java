package com.salo.sistemacreche.entidades;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "MATRICULA")
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MATRICULA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIANCA", nullable = false)
    private Crianca crianca;

    @ManyToOne
    @JoinColumn(name = "ID_PRE_MATRICULA")
    private PreMatricula preMatricula;

    @Column(name = "DATA_MATRICULA")
    @Temporal(TemporalType.DATE)
    private Date dataMatricula;

    @Column(name = "SERIE")
    private String serie;

    @Column(name = "ANO_LETIVO", nullable = false)
    private Integer anoLetivo;

    @Column(name = "ORIENT_RECEBIDA")
    private Boolean orientacaoRecebida;

    @Column(name = "DATA_DESLIGAMENTO")
    @Temporal(TemporalType.DATE)
    private Date dataDesligamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO_MAT")
    private SituacaoMatricula situacaoMatricula;


    public enum SituacaoMatricula {
        ATIVA, CONCLUIDA, CANCELADA
    }

    public Matricula() {
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

    public PreMatricula getPreMatricula() {
        return preMatricula;
    }

    public void setPreMatricula(PreMatricula preMatricula) {
        this.preMatricula = preMatricula;
    }

    public Date getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(Date dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String  serie) {
        this.serie = serie;
    }

    public Integer getAnoLetivo() {
        return anoLetivo;
    }

    public void setAnoLetivo(Integer anoLetivo) {
        this.anoLetivo = anoLetivo;
    }

    public Boolean getOrientacaoRecebida() {
        return orientacaoRecebida;
    }

    public void setOrientacaoRecebida(Boolean orientacaoRecebida) {
        this.orientacaoRecebida = orientacaoRecebida;
    }

    public Date getDataDesligamento() {
        return dataDesligamento;
    }

    public void setDataDesligamento(Date dataDesligamento) {
        this.dataDesligamento = dataDesligamento;
    }

    public SituacaoMatricula getSituacaoMatricula() {
        return situacaoMatricula;
    }

    public void setSituacaoMatricula(SituacaoMatricula situacaoMatricula) {
        this.situacaoMatricula = situacaoMatricula;
    }
}