package com.salo.sistemacreche.entidades;

import com.salo.sistemacreche.entidades.ComposicaoFamiliar;
import com.salo.sistemacreche.entidades.TipoBem;
import jakarta.persistence.*;

// Entidade para registrar quais bens a família possui
@Entity
@Table(name = "BENS_FAMILIA")
public class BensFamilia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_BENS_FAMILIA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_COMPOSICAO_FAMILIAR")
    private ComposicaoFamiliar composicaoFamiliar;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO_BEM")
    private TipoBem tipoBem;

    @Column(name = "POSSUI")
    private Boolean possui;

    @Column(name = "QUANTIDADE")
    private Integer quantidade;

    // Construtores, getters e setters
    public BensFamilia() {}

    public BensFamilia(ComposicaoFamiliar composicaoFamiliar, TipoBem tipoBem, Boolean possui, Integer quantidade) {
        this.composicaoFamiliar = composicaoFamiliar;
        this.tipoBem = tipoBem;
        this.possui = possui;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComposicaoFamiliar getComposicaoFamiliar() {
        return composicaoFamiliar;
    }

    public void setComposicaoFamiliar(ComposicaoFamiliar composicaoFamiliar) {
        this.composicaoFamiliar = composicaoFamiliar;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public Boolean getPossui() {
        return possui;
    }

    public void setPossui(Boolean possui) {
        this.possui = possui;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}