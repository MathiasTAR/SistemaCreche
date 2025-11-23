package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.TipoBem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxTemplate extends GridPane {

    private ObservableList<TipoBem> tiposBemSelecionados;
    private List<CheckBox> checkBoxes;

    public CheckBoxTemplate() {
        this.tiposBemSelecionados = FXCollections.observableArrayList();
        this.checkBoxes = new ArrayList<>();
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
        tiposBemSelecionados.clear();

        int row = 0;
        int col = 0;
        int maxCols = 6;

        for (TipoBem tipoBem : tiposBem) {
            CheckBox checkBox = criarCheckBox(tipoBem);
            checkBoxes.add(checkBox);

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
        checkBox.setTooltip(new Tooltip(formatTooltipName(tipoBem.getNomeBem())));
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

    private String formatTooltipName(TipoBem.NomeBem nomeBem) {
        switch (nomeBem) {
            case TV: return "Televisão";
            case DVD: return "DVD";
            case RADIO: return "Rádio";
            case COMPUTADOR: return "Computador";
            case NOTEBOOK: return "Notebook";
            case TELEFONE_FIXO: return "Telefone Fixo";
            case TELEFONE_CELULAR: return "Telefone Celular";
            case TABLET: return "Tablet";
            case INTERNET: return "Internet";
            case TV_ASSINATURA: return "TV por Assinatura";
            case FOGAO: return "Fogão";
            case GELADEIRA: return "Geladeira";
            case FREEZER: return "Freezer";
            case MICROONDAS: return "Microondas";
            case MAQUINA_LAVAR_ROUPA: return "Máquina de Lavar Roupa";
            case AR_CONDICIONADO: return "Ar Condicionado";
            case BICICLETA: return "Bicicleta";
            case MOTO: return "Moto";
            case AUTOMOVEL: return "Automóvel";
            default: return nomeBem.toString();
        }
    }

    // Métodos públicos para controle
    public ObservableList<TipoBem> getTiposBemSelecionados() {
        return FXCollections.unmodifiableObservableList(tiposBemSelecionados);
    }

    public void setTiposBemSelecionados(List<TipoBem> tiposBem) {
        clearAllSelections();
        for (TipoBem tipoBem : tiposBem) {
            for (CheckBox checkBox : checkBoxes) {
                // Encontrar o checkbox correspondente pelo texto
                if (checkBox.getText().equals(formatDisplayName(tipoBem.getNomeBem()))) {
                    checkBox.setSelected(true);
                    break;
                }
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
}