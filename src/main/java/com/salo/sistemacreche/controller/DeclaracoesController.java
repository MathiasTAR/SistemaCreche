package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DeclaracoesController {

    @FXML
    private ComboBox<String> comboAlunos;

    @FXML
    private ComboBox<String> comboDeclaracao;

    @FXML
    private DatePicker datePickerData;

    @FXML
    private TextArea textAreaDeclaracao;

    @FXML
    public void initialize() {
        System.out.println("📄 DeclaracoesController inicializado!");

        // Configurar comboboxes
        configurarComboboxes();

        // Configurar data padrão como hoje
        datePickerData.setValue(LocalDate.now());
    }

    private void configurarComboboxes() {
        // Exemplo de dados - você pode carregar do banco de dados
        comboAlunos.getItems().addAll(
                "Maria Vitória da Silva",
                "João Pedro Santos",
                "Ana Clara Oliveira",
                "Pedro Henrique Costa",
                "Laura Beatriz Souza"
        );

        comboDeclaracao.getItems().addAll(
                "Declaração de Matrícula",
                "Declaração de Frequência",
                "Declaração de Quitação",
                "Declaração de Transferência",
                "Declaração de Boa Conduta"
        );

        // Selecionar o primeiro item por padrão
        if (!comboDeclaracao.getItems().isEmpty()) {
            comboDeclaracao.setValue(comboDeclaracao.getItems().get(0));
        }
    }

    @FXML
    private void gerarDeclaracao() {
        System.out.println("🔄 Gerando declaração...");

        // Validar campos obrigatórios
        if (!validarCampos()) {
            return;
        }

        String aluno = comboAlunos.getValue();
        String tipoDeclaracao = comboDeclaracao.getValue();
        String data = datePickerData.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Gerar texto da declaração
        String declaracao = gerarTextoDeclaracao(aluno, tipoDeclaracao, data);

        // Exibir no preview
        textAreaDeclaracao.setText(declaracao);

        System.out.println("✅ Declaração gerada para: " + aluno);
    }

    @FXML
    private void imprimirDeclaracao() {
        if (textAreaDeclaracao.getText().isEmpty()) {
            mostrarAlerta("Aviso", "Gere uma declaração antes de imprimir.", Alert.AlertType.WARNING);
            return;
        }

        System.out.println("🖨️ Imprimindo declaração...");
        // TODO: Implementar lógica de impressão
        mostrarAlerta("Impressão", "Declaração enviada para impressão.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void salvarPdf() {
        if (textAreaDeclaracao.getText().isEmpty()) {
            mostrarAlerta("Aviso", "Gere uma declaração antes de salvar.", Alert.AlertType.WARNING);
            return;
        }

        System.out.println("💾 Salvando declaração como PDF...");
        // TODO: Implementar lógica para salvar PDF
        mostrarAlerta("Salvar PDF", "Declaração salva como PDF com sucesso!", Alert.AlertType.INFORMATION);
    }

    private boolean validarCampos() {
        if (comboAlunos.getValue() == null || comboAlunos.getValue().isEmpty()) {
            mostrarAlerta("Erro", "Selecione uma criança.", Alert.AlertType.ERROR);
            return false;
        }

        if (comboDeclaracao.getValue() == null || comboDeclaracao.getValue().isEmpty()) {
            mostrarAlerta("Erro", "Selecione o tipo de declaração.", Alert.AlertType.ERROR);
            return false;
        }

        if (datePickerData.getValue() == null) {
            mostrarAlerta("Erro", "Selecione uma data.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private String gerarTextoDeclaracao(String aluno, String tipoDeclaracao, String data) {
        StringBuilder declaracao = new StringBuilder();

        declaracao.append("DECLARAÇÃO\n\n");
        declaracao.append("========================================\n\n");

        switch (tipoDeclaracao) {
            case "Declaração de Matrícula":
                declaracao.append("Declaramos para os devidos fins que ");
                declaracao.append(aluno);
                declaracao.append(" encontra-se regularmente matriculado(a) nesta instituição de ensino - Creche Estrela do Oriente, para o ano letivo de 2024.\n\n");
                break;

            case "Declaração de Frequência":
                declaracao.append("Declaramos que ");
                declaracao.append(aluno);
                declaracao.append(" possui frequência regular nas atividades escolares, com aproveitamento satisfatório, conforme registros em nosso sistema.\n\n");
                break;

            case "Declaração de Quitação":
                declaracao.append("Certificamos que ");
                declaracao.append(aluno);
                declaracao.append(" encontra-se quite com todas as obrigações financeiras junto a esta instituição até a data presente.\n\n");
                break;

            case "Declaração de Transferência":
                declaracao.append("Declaramos que ");
                declaracao.append(aluno);
                declaracao.append(" teve sua matrícula transferida a pedido dos responsáveis, estando quite com todas as obrigações junto a esta instituição.\n\n");
                break;

            case "Declaração de Boa Conduta":
                declaracao.append("Atestamos que ");
                declaracao.append(aluno);
                declaracao.append(" demonstrou durante o período de permanência nesta instituição, comportamento adequado e conduta exemplar, participando ativamente das atividades propostas.\n\n");
                break;

            default:
                declaracao.append("Declaramos para os devidos fins sobre a situação de ");
                declaracao.append(aluno);
                declaracao.append(" junto a esta instituição de ensino.\n\n");
        }

        declaracao.append("Por ser verdade, firmamos a presente declaração.\n\n");
        declaracao.append("Data: ").append(data).append("\n\n");
        declaracao.append("____________________________________\n");
        declaracao.append("Creche Estrela do Oriente\n");
        declaracao.append("CNPJ: 12.345.678/0001-90\n");
        declaracao.append("Endereço: Rua Exemplo, 123 - Centro\n");
        declaracao.append("Telefone: (11) 1234-5678\n");

        return declaracao.toString();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}