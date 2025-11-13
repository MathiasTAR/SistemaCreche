package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CadastroMatriculaController {

    // Seção 1: Identificação da Criança
    @FXML private TextField fieldNomeCrianca;
    @FXML private TextField fieldRgCrianca;
    @FXML private DatePicker datePickerNascimento;
    @FXML private ComboBox<String> comboSexo;
    @FXML private ComboBox<String> comboCorRaca;
    @FXML private TextField fieldSus;
    @FXML private TextField fieldUnidadeSaude;
    @FXML private ComboBox<String> comboRestricaoAlimentar;
    @FXML private ComboBox<String> comboAlergia;
    @FXML private ComboBox<String> comboMobilidadeReduzida;
    @FXML private ComboBox<String> comboDeficienciasMultiplas;
    @FXML private ComboBox<String> comboEducacaoEspecial;

    // Seção 2: Filiação/Responsáveis
    @FXML private TextField fieldPesquisaMae;
    @FXML private TextField fieldPesquisaPai;
    @FXML private TextField fieldPesquisaResponsavel;

    // Seção 3: Endereço
    @FXML private TextField fieldEndereco;
    @FXML private TextField fieldPontoReferencia;
    @FXML private TextField fieldBairro;
    @FXML private TextField fieldMunicipio;
    @FXML private TextField fieldNumero;
    @FXML private TextField fieldCEP;
    @FXML private ComboBox<String> comboUF;
    @FXML private TextField fieldTelefoneResidencial;
    @FXML private TextField fieldTelefoneContato;

    // Seção 4: Documentos
    @FXML private TextField fieldCertidaoNascimento;
    @FXML private TextField fieldNumeroMatricula;
    @FXML private TextField fieldMunicipioNascimento;
    @FXML private TextField fieldCartorioRegistro;
    @FXML private TextField fieldMunicipioRegistro;
    @FXML private TextField fieldCpfCrianca;
    @FXML private DatePicker datePickerEmissaoRG;
    @FXML private TextField fieldOrgaoEmissor;

    // Seção 5: Situação Habitacional
    @FXML private ComboBox<String> comboTipoMoradia;
    @FXML private TextField fieldValorAluguel;
    @FXML private TextField fieldNumeroComodos;
    @FXML private ComboBox<String> comboTipoPiso;
    @FXML private ComboBox<String> comboMaterialParede;
    @FXML private ComboBox<String> comboTipoCobertura;
    @FXML private CheckBox checkFossa;
    @FXML private CheckBox checkCifon;
    @FXML private CheckBox checkEnergiaEletrica;
    @FXML private CheckBox checkAguaEncanada;

    // Seção 6: Bens
    @FXML private CheckBox checkTV;
    @FXML private CheckBox checkDVD;
    @FXML private CheckBox checkComputador;
    @FXML private CheckBox checkInternet;
    @FXML private CheckBox checkGeladeira;
    @FXML private CheckBox checkFogao;
    @FXML private CheckBox checkMaquinaLavar;
    @FXML private CheckBox checkMicroondas;
    @FXML private CheckBox checkCarro;
    @FXML private CheckBox checkMoto;

    // Seção 7: Composição Familiar
    @FXML private TableView<?> tableComposicaoFamiliar;

    // Seção 8: Série
    @FXML private ComboBox<String> comboSerie;
    @FXML private TextField fieldAnoLetivo;

    // Seção 9: Pessoas Autorizadas
    @FXML private TableView<?> tablePessoasAutorizadas;

    // Seção 10: Declaração
    // (Sem campos específicos)

    // Seção 11: Irmão Gêmeo
    @FXML private CheckBox checkIrmaoGemeo;

    // Botões
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    @FXML
    public void initialize() {
        configurarComboBox();
    }

    private void configurarComboBox() {
        comboSexo.getItems().addAll("Masculino", "Feminino", "Outro");
        comboCorRaca.getItems().addAll("Branca", "Preta", "Parda", "Amarela", "Indigena");
        comboRestricaoAlimentar.getItems().addAll("Masculino", "Feminino", "Outro");
        comboAlergia.getItems().addAll("Mosquito", "Poeira", "Abelha");
        comboMobilidadeReduzida.getItems().addAll("Temporaria", "Permanente", "Outro");
        comboDeficienciasMultiplas.getItems().addAll("Cegueira", "Surdo", "TDHA", "Autismo");
        comboEducacaoEspecial.getItems().addAll("Atenção Extra", "Hiberfoco", "Austista");


        //comboSexo.getItems().addAll("Masculino", "Feminino", "Outro");
    }

    @FXML
    public void salvarMatricula() {
        if (validarCamposObrigatorios()) {
            System.out.println("Salvando matrícula...");

            // Capturar dados básicos
            String nomeCrianca = fieldNomeCrianca.getText();
            String sexo = comboSexo.getValue();
            String serie = comboSerie.getValue();

            System.out.println("Criança: " + nomeCrianca);
            System.out.println("Sexo: " + sexo);
            System.out.println("Série: " + serie);

            // TODO: Implementar lógica de salvamento no banco

            mostrarMensagem("Sucesso", "Matrícula cadastrada com sucesso!");
            limparFormulario();
        }
    }

    @FXML
    public void cancelarCadastro() {
        System.out.println("Cancelando cadastro...");
        limparFormulario();
        // TODO: Fechar a tela ou voltar para lista
    }

    private boolean validarCamposObrigatorios() {
        // Validar campos mais importantes
        if (fieldNomeCrianca.getText().trim().isEmpty()) {
            mostrarMensagem("Erro", "Nome da criança é obrigatório!");
            fieldNomeCrianca.requestFocus();
            return false;
        }

        if (comboSexo.getValue() == null) {
            mostrarMensagem("Erro", "Sexo é obrigatório!");
            comboSexo.requestFocus();
            return false;
        }

        if (datePickerNascimento.getValue() == null) {
            mostrarMensagem("Erro", "Data de nascimento é obrigatória!");
            datePickerNascimento.requestFocus();
            return false;
        }

        if (comboSerie.getValue() == null) {
            mostrarMensagem("Erro", "Série é obrigatória!");
            comboSerie.requestFocus();
            return false;
        }

        return true;
    }

    private void limparFormulario() {
        // Limpar campos básicos
        fieldNomeCrianca.clear();
        fieldRgCrianca.clear();
        datePickerNascimento.setValue(null);
        comboSexo.setValue(null);
        comboCorRaca.setValue(null);
        fieldSus.clear();
        fieldUnidadeSaude.clear();

        // Limpar endereço
        fieldEndereco.clear();
        fieldBairro.clear();
        fieldMunicipio.clear();
        fieldNumero.clear();
        fieldCEP.clear();
        comboUF.setValue(null);

        // Limpar série
        comboSerie.setValue(null);
        fieldAnoLetivo.clear();
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Métodos para ações dos botões de pesquisa (podem ser implementados depois)
    @FXML
    private void pesquisarMae() {
        System.out.println("Pesquisando mãe...");
    }

    @FXML
    private void pesquisarPai() {
        System.out.println("Pesquisando pai...");
    }

    @FXML
    private void pesquisarResponsavel() {
        System.out.println("Pesquisando responsável...");
    }

    @FXML
    private void adicionarMorador() {
        System.out.println("Adicionando morador...");
    }

    @FXML
    private void adicionarPessoaAutorizada() {
        System.out.println("Adicionando pessoa autorizada...");
    }

    @FXML
    private void importarAnexo() {
        System.out.println("Importando anexo...");
    }
}