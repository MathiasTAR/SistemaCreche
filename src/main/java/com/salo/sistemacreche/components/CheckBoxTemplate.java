package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.TipoBem;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckBoxTemplate extends GridPane {

    private final ObservableList<TipoBem> tiposBemSelecionados;
    private final List<CheckBox> checkBoxes;
    private final Map<CheckBox, TipoBem> mapaCheckBoxParaTipoBem;
    private List<TipoBem> todosTiposBem;

    public CheckBoxTemplate() {
        this.tiposBemSelecionados = FXCollections.observableArrayList();
        this.checkBoxes = new ArrayList<>();
        this.mapaCheckBoxParaTipoBem = new HashMap<>();
        this.todosTiposBem = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        setHgap(15);
        setVgap(10);
        setPadding(new Insets(10));
    }

    public void carregarTiposBem(List<TipoBem> tiposBem) {
        // Limpar checkboxes existentes
        getChildren().clear();
        checkBoxes.clear();
        mapaCheckBoxParaTipoBem.clear();
        tiposBemSelecionados.clear();

        this.todosTiposBem = new ArrayList<>(tiposBem);

        int row = 0;
        int col = 0;
        int maxCols = 6;

        for (TipoBem tipoBem : tiposBem) {
            CheckBox checkBox = criarCheckBox(tipoBem);
            checkBoxes.add(checkBox);
            mapaCheckBoxParaTipoBem.put(checkBox, tipoBem);

            add(checkBox, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private CheckBox criarCheckBox(TipoBem tipoBem) {
        CheckBox checkBox = new CheckBox(formatDisplayName(tipoBem.getNomeBem()));
        checkBox.setStyle("-fx-font-size: 13px; -fx-text-fill: #495057; -fx-cursor: hand;");

        // Listener para controlar os itens selecionados
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tiposBemSelecionados.add(tipoBem);
            } else {
                tiposBemSelecionados.remove(tipoBem);
            }
        });

        return checkBox;
    }

    private String formatDisplayName(TipoBem.NomeBem nomeBem) {
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

    // Métodos públicos para controle
    public ObservableList<TipoBem> getTiposBemSelecionados() {
        return FXCollections.unmodifiableObservableList(tiposBemSelecionados);
    }

    public void setTiposBemSelecionados(List<TipoBem> tiposBemParaSelecionar) {
        clearAllSelections();

        if (tiposBemParaSelecionar == null || tiposBemParaSelecionar.isEmpty()) {
            return;
        }

        // Criar conjunto para busca rápida
        Set<TipoBem.NomeBem> nomesParaSelecionar = new HashSet<>();
        for (TipoBem tipoBem : tiposBemParaSelecionar) {
            nomesParaSelecionar.add(tipoBem.getNomeBem());
        }

        // Marcar checkboxes usando o mapa
        for (CheckBox checkBox : checkBoxes) {
            TipoBem tipoBemAssociado = mapaCheckBoxParaTipoBem.get(checkBox);
            if (tipoBemAssociado != null && nomesParaSelecionar.contains(tipoBemAssociado.getNomeBem())) {
                checkBox.setSelected(true);
            }
        }
    }

    public void clearAllSelections() {
        tiposBemSelecionados.clear();
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(false);
        }
    }

    public boolean hasSelections() {
        return !tiposBemSelecionados.isEmpty();
    }

    // Método para obter a lista completa de tipos de bem (útil para debug)
    public List<TipoBem> getTodosTiposBem() {
        return new ArrayList<>(todosTiposBem);
    }

    // Método para verificar se um tipo específico está selecionado
    public boolean isTipoBemSelecionado(TipoBem.NomeBem nomeBem) {
        for (TipoBem tipoBem : tiposBemSelecionados) {
            if (tipoBem.getNomeBem() == nomeBem) {
                return true;
            }
        }
        return false;
    }
}