package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Alergia;
import com.salo.sistemacreche.entidades.ClassificacaoEspecial;
import com.salo.sistemacreche.entidades.TipoAuxilio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

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

    // Combos que virão do banco
    @FXML private ComboBox<String> comboClassificacaoEspecial;
    @FXML private ComboBox<String> comboAlergias;
    @FXML private ComboBox<String> comboTipoAuxilio;

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

    // Seção 11: Irmão Gêmeo
    @FXML private CheckBox checkIrmaoGemeo;

    // Botões
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    @FXML
    public void initialize() {
        configurarComboBoxFixos();
        carregarDadosDoBanco();
    }

    private void configurarComboBoxFixos() {
        comboSexo.setItems(FXCollections.observableArrayList(
                "Masculino", "Feminino"
        ));

        comboCorRaca.setItems(FXCollections.observableArrayList(
                "Branca", "Preta", "Parda", "Amarela", "Indígena"
        ));

        comboRestricaoAlimentar.setItems(FXCollections.observableArrayList(
                "Não", "Sim"
        ));

        comboAlergia.setItems(FXCollections.observableArrayList(
                "Não", "Sim"
        ));

        comboMobilidadeReduzida.setItems(FXCollections.observableArrayList(
                "Não", "Sim, temporária", "Sim, permanente"
        ));

        comboDeficienciasMultiplas.setItems(FXCollections.observableArrayList(
                "Não", "Sim"
        ));

        comboEducacaoEspecial.setItems(FXCollections.observableArrayList(
                "Não", "Sim"
        ));

        comboUF.setItems(FXCollections.observableArrayList(
                "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
                "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
                "RS", "RO", "RR", "SC", "SP", "SE", "TO"
        ));

        comboTipoMoradia.setItems(FXCollections.observableArrayList(
                "Casa própria", "Casa cedida", "Casa alugada"
        ));

        comboTipoPiso.setItems(FXCollections.observableArrayList(
                "Cimento", "Lajota", "Chão batido", "Outro"
        ));

        comboMaterialParede.setItems(FXCollections.observableArrayList(
                "Tijolo", "Taipa", "Madeira", "Outro"
        ));

        comboTipoCobertura.setItems(FXCollections.observableArrayList(
                "Telha", "Zinco", "Palha", "Outro"
        ));

        comboSerie.setItems(FXCollections.observableArrayList(
                "Berçário I", "Berçário II", "Maternal I", "Maternal II", "Pré I", "Pré II"
        ));
    }

    private void carregarDadosDoBanco() {
        carregarClassificacoesEspeciais();
        carregarAlergias();
        carregarTiposAuxilio();
    }

    private void carregarClassificacoesEspeciais() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            TypedQuery<ClassificacaoEspecial> query = em.createQuery(
                    "SELECT c FROM ClassificacaoEspecial c WHERE c.statusClassificacaoEspecial = true ORDER BY c.classificacaoEspecial",
                    ClassificacaoEspecial.class
            );

            List<ClassificacaoEspecial> classificacoes = query.getResultList();

            // Converter para lista de strings para o ComboBox
            List<String> nomesClassificacoes = classificacoes.stream()
                    .map(ClassificacaoEspecial::getClassificacaoEspecial)
                    .toList();

            comboClassificacaoEspecial.setItems(FXCollections.observableArrayList(nomesClassificacoes));

            System.out.println("✅ " + classificacoes.size() + " classificação(ões) especial(is) carregada(s)");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar classificações especiais: " + e.getMessage());
            e.printStackTrace();

            // Opção fallback caso haja erro
            comboClassificacaoEspecial.setItems(FXCollections.observableArrayList(
                    "Altas Habilidades", "Deficiência Física", "Deficiência Visual"
            ));
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void carregarAlergias() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            TypedQuery<Alergia> query = em.createQuery(
                    "SELECT a FROM Alergia a ORDER BY a.nome_Alergia",
                    Alergia.class
            );

            List<Alergia> alergias = query.getResultList();

            // Converter para lista de strings para o ComboBox
            List<String> nomesAlergias = alergias.stream()
                    .map(Alergia::getNomeAlergia)
                    .toList();

            comboAlergias.setItems(FXCollections.observableArrayList(nomesAlergias));

            System.out.println("✅ " + alergias.size() + " alergia(s) carregada(s)");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar alergias: " + e.getMessage());
            e.printStackTrace();

            // Opção fallback caso haja erro
            comboAlergias.setItems(FXCollections.observableArrayList(
                    "Leite", "Ovo", "Amendoim", "Glúten"
            ));
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void carregarTiposAuxilio() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            TypedQuery<TipoAuxilio> query = em.createQuery(
                    "SELECT t FROM TipoAuxilio t ORDER BY t.nomeAuxilio",
                    TipoAuxilio.class
            );

            List<TipoAuxilio> tiposAuxilio = query.getResultList();

            // Converter para lista de strings para o ComboBox
            List<String> nomesAuxilios = tiposAuxilio.stream()
                    .map(TipoAuxilio::getNomeAuxilio)
                    .toList();

            comboTipoAuxilio.setItems(FXCollections.observableArrayList(nomesAuxilios));

            System.out.println("✅ " + tiposAuxilio.size() + " tipo(s) de auxílio carregado(s)");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar tipos de auxílio: " + e.getMessage());
            e.printStackTrace();

            // Opção fallback caso haja erro
            comboTipoAuxilio.setItems(FXCollections.observableArrayList(
                    "Bolsa Família", "Auxílio Brasil", "BPC - LOAS"
            ));
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @FXML
    public void salvarMatricula() {
        if (validarCamposObrigatorios()) {
            System.out.println("Salvando matrícula...");

            // Capturar dados básicos
            String nomeCrianca = fieldNomeCrianca.getText();
            String sexo = comboSexo.getValue();
            String serie = comboSerie.getValue();

            // Capturar dados dos combos do banco
            String classificacaoSelecionada = comboClassificacaoEspecial.getValue();
            String alergiaSelecionada = comboAlergias.getValue();
            String auxilioSelecionado = comboTipoAuxilio.getValue();

            System.out.println("Criança: " + nomeCrianca);
            System.out.println("Sexo: " + sexo);
            System.out.println("Série: " + serie);
            System.out.println("Classificação: " + classificacaoSelecionada);
            System.out.println("Alergia: " + alergiaSelecionada);
            System.out.println("Auxílio: " + auxilioSelecionado);

            // TODO: Implementar lógica completa de salvamento no banco

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

        // Limpar combos do banco
        comboClassificacaoEspecial.setValue(null);
        comboAlergias.setValue(null);
        comboTipoAuxilio.setValue(null);

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