package com.salo.sistemacreche.entidades;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "SITUACAO_HABITACIONAL")
public class SituacaoHabitacional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SITUACAO_HAB")
    private Long id;

    // 🔥 ADICIONAR: Relação com Criança
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRIANCA")
    private Crianca crianca;

    @Column(name = "CASA_PROPRIA")
    private Boolean casaPropria;

    @Column(name = "CASA_CEDIDA")
    private Boolean casaCedida;

    @Column(name = "CASA_ALUGADA")
    private Boolean casaAlugada;

    @Column(name = "VALOR_ALUGUEL", precision = 10, scale = 2)
    private BigDecimal valorAluguel;

    @Column(name = "NUMERO_COMODOS")
    private Integer numeroComodos;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PISO")
    private TipoPiso tipoPiso;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_MORADIA")
    private TipoMoradia tipoMoradia;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_COBERTURA")
    private TipoCobertura tipoCobertura;

    @Column(name = "FOSSA")
    private Boolean fossa;

    @Column(name = "CIFON")
    private Boolean cifon;

    @Column(name = "ENERGIA_ELETRICA")
    private Boolean energiaEletrica;

    @Column(name = "AGUA_ENCANADA")
    private Boolean aguaEncanada;

    @Column(name = "COLETOR_PUBLICO")
    private Boolean coletorPublico;

    @Column(name = "TV")
    private Boolean tv;

    @Column(name = "DVD")
    private Boolean dvd;

    @Column(name = "COMPUTADOR")
    private Boolean computador;

    @Column(name = "GELADEIRA")
    private Boolean geladeira;

    @Column(name = "FOGAO")
    private Boolean fogao;

    @Column(name = "MAQUINA_LAVAR")
    private Boolean maquinaLavar;

    @Column(name = "CARRO")
    private Boolean carro;

    @Column(name = "MOTO")
    private Boolean moto;

    @Column(name = "INTERNET")
    private Boolean internet;

    // Enums
    public enum TipoPiso {
        CIMENTO, LAJOTA, CHAO_BATIDO, CERAMICA, MADEIRA, OUTRO
    }

    public enum TipoMoradia {
        TIJOLO, TAIPA, MADEIRA, MISTA, ALVENARIA, OUTRO
    }

    public enum TipoCobertura {
        TELHA, ZINCO, PALHA, LAJE, OUTRO
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 🔥 ADICIONAR: Getter e Setter para Crianca
    public Crianca getCrianca() {
        return crianca;
    }

    public void setCrianca(Crianca crianca) {
        this.crianca = crianca;
    }

    public Boolean getCasaPropria() {
        return casaPropria;
    }

    public void setCasaPropria(Boolean casaPropria) {
        this.casaPropria = casaPropria;
    }

    public Boolean getCasaCedida() {
        return casaCedida;
    }

    public void setCasaCedida(Boolean casaCedida) {
        this.casaCedida = casaCedida;
    }

    public Boolean getCasaAlugada() {
        return casaAlugada;
    }

    public void setCasaAlugada(Boolean casaAlugada) {
        this.casaAlugada = casaAlugada;
    }

    public BigDecimal getValorAluguel() {
        return valorAluguel;
    }

    public void setValorAluguel(BigDecimal valorAluguel) {
        this.valorAluguel = valorAluguel;
    }

    public Integer getNumeroComodos() {
        return numeroComodos;
    }

    public void setNumeroComodos(Integer numeroComodos) {
        this.numeroComodos = numeroComodos;
    }

    public TipoPiso getTipoPiso() {
        return tipoPiso;
    }

    public void setTipoPiso(TipoPiso tipoPiso) {
        this.tipoPiso = tipoPiso;
    }

    public TipoMoradia getTipoMoradia() {
        return tipoMoradia;
    }

    public void setTipoMoradia(TipoMoradia tipoMoradia) {
        this.tipoMoradia = tipoMoradia;
    }

    public TipoCobertura getTipoCobertura() {
        return tipoCobertura;
    }

    public void setTipoCobertura(TipoCobertura tipoCobertura) {
        this.tipoCobertura = tipoCobertura;
    }

    public Boolean getFossa() {
        return fossa;
    }

    public void setFossa(Boolean fossa) {
        this.fossa = fossa;
    }

    public Boolean getCifon() {
        return cifon;
    }

    public void setCifon(Boolean cifon) {
        this.cifon = cifon;
    }

    public Boolean getEnergiaEletrica() {
        return energiaEletrica;
    }

    public void setEnergiaEletrica(Boolean energiaEletrica) {
        this.energiaEletrica = energiaEletrica;
    }

    public Boolean getAguaEncanada() {
        return aguaEncanada;
    }

    public void setAguaEncanada(Boolean aguaEncanada) {
        this.aguaEncanada = aguaEncanada;
    }

    public Boolean getColetorPublico() {
        return coletorPublico;
    }

    public void setColetorPublico(Boolean coletorPublico) {
        this.coletorPublico = coletorPublico;
    }

    public Boolean getTv() {
        return tv;
    }

    public void setTv(Boolean tv) {
        this.tv = tv;
    }

    public Boolean getDvd() {
        return dvd;
    }

    public void setDvd(Boolean dvd) {
        this.dvd = dvd;
    }

    public Boolean getComputador() {
        return computador;
    }

    public void setComputador(Boolean computador) {
        this.computador = computador;
    }

    public Boolean getGeladeira() {
        return geladeira;
    }

    public void setGeladeira(Boolean geladeira) {
        this.geladeira = geladeira;
    }

    public Boolean getFogao() {
        return fogao;
    }

    public void setFogao(Boolean fogao) {
        this.fogao = fogao;
    }

    public Boolean getMaquinaLavar() {
        return maquinaLavar;
    }

    public void setMaquinaLavar(Boolean maquinaLavar) {
        this.maquinaLavar = maquinaLavar;
    }

    public Boolean getCarro() {
        return carro;
    }

    public void setCarro(Boolean carro) {
        this.carro = carro;
    }

    public Boolean getMoto() {
        return moto;
    }

    public void setMoto(Boolean moto) {
        this.moto = moto;
    }

    public Boolean getInternet() {
        return internet;
    }

    public void setInternet(Boolean internet) {
        this.internet = internet;
    }
}