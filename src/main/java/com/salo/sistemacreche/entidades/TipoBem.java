package com.salo.sistemacreche.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "TIPO_BEM")
public class TipoBem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO_BEM")
    private Integer idTipoBem;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOME_BEM", unique = true, nullable = false)
    private NomeBem nomeBem;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORIA", nullable = false)
    private CategoriaBem categoria;

    // ⚠️ CONSTRUTOR PADRÃO OBRIGATÓRIO (sem argumentos)
    public TipoBem() {
        // Construtor padrão necessário para o Hibernate
    }

    // ⚠️ REMOVA ou CORRIJA este construtor problemático
    // public TipoBem(Crianca crianca, TipoBem managedTipoBem) {}

    // Construtor com parâmetros úteis
    public TipoBem(NomeBem nomeBem, CategoriaBem categoria) {
        this.nomeBem = nomeBem;
        this.categoria = categoria;
    }

    public Integer getIdTipoBem() {
        return idTipoBem;
    }

    public void setIdTipoBem(Integer idTipoBem) {
        this.idTipoBem = idTipoBem;
    }

    public NomeBem getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(NomeBem nomeBem) {
        this.nomeBem = nomeBem;
    }

    public CategoriaBem getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaBem categoria) {
        this.categoria = categoria;
    }

    // toString, equals e hashCode
    @Override
    public String toString() {
        return "TipoBem{" +
                "idTipoBem=" + idTipoBem +
                ", nomeBem=" + nomeBem +
                ", categoria=" + categoria +
                '}';
    }

    public enum NomeBem {
        TV,
        DVD,
        RADIO,
        COMPUTADOR,
        NOTEBOOK,
        TELEFONE_FIXO,
        TELEFONE_CELULAR,
        TABLET,
        INTERNET,
        TV_ASSINATURA,
        FOGAO,
        GELADEIRA,
        FREEZER,
        MICROONDAS,
        MAQUINA_LAVAR_ROUPA,
        AR_CONDICIONADO,
        BICICLETA,
        MOTO,
        AUTOMOVEL
    }

    public enum CategoriaBem {
        ELETRODOMESTICO,
        ELETRONICO,
        VEICULO,
        COMUNICACAO
    }
}