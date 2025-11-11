package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RelatoriosController {

    // Principais controles
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private ComboBox<String> comboPeriodo;
    @FXML private Button btnLimparCampos;
    @FXML private Button btnGerarPDF;

    @FXML
    public void initialize() {
        System.out.println("📊 RelatoriosController inicializado!");
        configurarComboboxes();
    }

    private void configurarComboboxes() {
        // Combo período já está definido no FXML, apenas configurar valor padrão
        comboPeriodo.setValue("Este mês");

        // Configurar data padrão (este mês)
        datePickerInicio.setValue(java.time.LocalDate.now().withDayOfMonth(1));
        datePickerFim.setValue(java.time.LocalDate.now());
    }

    @FXML
    private void limparCampos() {
        System.out.println("🧹 Limpando campos...");

        // Apenas limpar os campos principais
        datePickerInicio.setValue(null);
        datePickerFim.setValue(null);
        comboPeriodo.setValue(null);

        mostrarMensagem("Campos limpos!", "Todos os campos foram resetados.");
    }

    @FXML
    private void gerarPDF() {
        System.out.println("📄 Gerando relatório PDF...");

        // Validar apenas datas
        if (datePickerInicio.getValue() == null || datePickerFim.getValue() == null) {
            mostrarErro("Selecione o período", "É necessário definir data inicial e final.");
            return;
        }

        if (datePickerInicio.getValue().isAfter(datePickerFim.getValue())) {
            mostrarErro("Data inválida", "Data inicial não pode ser depois da data final.");
            return;
        }

        // Simular geração do PDF
        String periodo = datePickerInicio.getValue() + " a " + datePickerFim.getValue();
        System.out.println("✅ Gerando PDF para o período: " + periodo);

        mostrarMensagem("PDF Gerado!", "Relatório criado com sucesso!\nPeríodo: " + periodo);
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}