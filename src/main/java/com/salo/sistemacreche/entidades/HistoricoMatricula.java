package com.salo.sistemacreche.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "HISTORICO_MATRICULA")
public class HistoricoMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HIST_MATRI")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MATRICULA", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRIANCA", nullable = false)
    private Crianca crianca;

    @Column(name = "DATA_REGISTRO", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACAO", nullable = false)
    private AcaoHistorico acao;

    // Construtores
    public HistoricoMatricula() {
        this.dataRegistro = LocalDateTime.now();
    }

    public HistoricoMatricula(Matricula matricula, Crianca crianca, AcaoHistorico acao) {
        this();
        this.matricula = matricula;
        this.crianca = crianca;
        this.acao = acao;
    }

    // Enum para as ações do histórico
    public enum AcaoHistorico {
        CRIACAO,
        RENOVACAO,
        CANCELAMENTO,
        CONCLUSAO,
        ALTERACAO
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Crianca getCrianca() {
        return crianca;
    }

    public void setCrianca(Crianca crianca) {
        this.crianca = crianca;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public AcaoHistorico getAcao() {
        return acao;
    }

    public void setAcao(AcaoHistorico acao) {
        this.acao = acao;
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoMatricula that = (HistoricoMatricula) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "HistoricoMatricula{" +
                "id=" + id +
                ", matricula=" + (matricula != null ? matricula.getId() : "null") +
                ", crianca=" + (crianca != null ? crianca.getNome() : "null") +
                ", dataRegistro=" + dataRegistro +
                ", acao=" + acao +
                '}';
    }
}