package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.TipoBem;
import javafx.scene.control.CheckBox;

public class CheckBoxTipoBem extends CheckBox {
    private final TipoBem tipoBem;

    public CheckBoxTipoBem(TipoBem tipoBem) {
        super(formatDisplayName(tipoBem.getNomeBem()));
        this.tipoBem = tipoBem;
        setStyle("-fx-font-size: 12px; -fx-text-fill: #495057; -fx-cursor: hand;");
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    private static String formatDisplayName(TipoBem.NomeBem nomeBem) {
        switch (nomeBem) {
            case TV: return "TV";
            case DVD: return "DVD";
            case RADIO: return "Rádio";
            case COMPUTADOR: return "Computador";
            case NOTEBOOK: return "Notebook";
            case TELEFONE_FIXO: return "Tel. Fixo";
            case TELEFONE_CELULAR: return "Celular";
            case TABLET: return "Tablet";
            case INTERNET: return "Internet";
            case TV_ASSINATURA: return "TV Assinatura";
            case FOGAO: return "Fogão";
            case GELADEIRA: return "Geladeira";
            case FREEZER: return "Freezer";
            case MICROONDAS: return "Microondas";
            case MAQUINA_LAVAR_ROUPA: return "Máq. Lavar";
            case AR_CONDICIONADO: return "Ar Cond.";
            case BICICLETA: return "Bicicleta";
            case MOTO: return "Moto";
            case AUTOMOVEL: return "Carro";
            default: return nomeBem.toString();
        }
    }
}