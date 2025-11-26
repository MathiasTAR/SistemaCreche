package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.*;
import com.salo.sistemacreche.controller.extracadastro.FiliacaoResponsavelController;
import com.salo.sistemacreche.controller.extracadastro.MembroFamiliarController;
import com.salo.sistemacreche.controller.extracadastro.PessoaAutorizadaController;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.*;
import com.salo.sistemacreche.service.CadastroMatriculaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CadastroMatriculaController {

    private static final String CPF_LENGTH = "11";
    private static final String CNPJ_LENGTH = "14";
    private static final String CEP_LENGTH = "8";
    private static final String TELEFONE_LENGTH = "11";
    private static final String NIS_LENGTH = "11";
    private static final String ANO_LETIVO_LENGTH = "4";

    // Seção 1: Identificação da Criança
    @FXML private TextField fieldNomeCrianca;
    @FXML private TextField fieldRgCrianca;
    @FXML private DatePicker datePickerNascimento;
    @FXML private ComboBox<String> comboSexo;
    @FXML private ComboBox<String> comboCorRaca;
    @FXML private TextField fieldSus;
    @FXML private TextField fieldUnidadeSaude;
    @FXML private TextField fieldNis;
    @FXML private ComboBox<String> comboMobilidadeReduzida;
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
    private CheckBoxTemplate checkBoxTemplateBens;

    // Substitua a seção de bens no FXML por:
    @FXML private VBox containerBens;

    // Seção 7: Composição Familiar
    @FXML private TableView<MembroFamilia> tableComposicaoFamiliar;
    private final ObservableList<MembroFamilia> membrosFamiliares = FXCollections.observableArrayList();

    // Seção 8: Série
    @FXML private ComboBox<String> comboSerie;
    @FXML private TextField fieldAnoLetivo;

    // Seção 9: Pessoas Autorizadas
    @FXML private TableView<PessoaAutorizada> tablePessoasAutorizadas;
    private final ObservableList<PessoaAutorizada> pessoaAutorizadas = FXCollections.observableArrayList();

    // Seção 11: Irmão Gêmeo
    @FXML private VBox containerIrmaos;
    private final ObservableList<Crianca> irmaosEncontrados = FXCollections.observableArrayList();

    // Botões
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    @FXML private VBox cardsContainerMaes;
    @FXML private VBox cardsContainerPais;
    @FXML private VBox cardsContainerResponsaveis;

    // Responsáveis selecionados
    private Responsavel maeSelecionada;
    private Responsavel paiSelecionado;
    private Responsavel responsavelSelecionado;

    @FXML
    public void initialize() {
        configurarComboBoxFixos();
        carregarDadosDoBanco();
        pesquisarMae();
        pesquisarPai();
        pesquisarResponsavel();
        configurarPesquisaPorEnter();
        configurarTableViews();
        limparDetecaoIrmaos();
        configurarSelecaoResponsaveis();
        aplicarMascaras();
        configurarValidacoes();
    }

    private void carregarDadosDoBanco() {
        carregarClassificacoesEspeciais();
        carregarAlergias();
        carregarTiposAuxilio();
        configurarBensFamilia();
    }

    private void configurarComboBoxFixos() {
        comboSexo.setItems(FXCollections.observableArrayList(
                "Masculino", "Feminino", "Outro"
        ));

        comboTipoAuxilio.valueProperty().addListener((observable, oldValue, newValue) -> {
            validarNis();
        });

        comboCorRaca.setItems(FXCollections.observableArrayList(
                "Branca", "Preta", "Parda", "Amarela", "Indígena"
        ));

        comboMobilidadeReduzida.setItems(FXCollections.observableArrayList(
                "Não", "Sim, temporária", "Sim, permanente"
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
                "BERCARIO_I", "BERCARIO_II", "MATERNAL_I", "MATERNAL_II", "PRE_I", "PRE_II"
        ));
    }

    private void carregarClassificacoesEspeciais() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            TypedQuery<ClassificacaoEspecial> query = em.createQuery(
                    "SELECT c FROM ClassificacaoEspecial c WHERE c.statusClassificacaoEspecial = true " +
                            "ORDER BY CASE WHEN c.classificacaoEspecial = 'Nenhum' THEN 0 ELSE 1 END, c.classificacaoEspecial",
                    ClassificacaoEspecial.class
            );

            List<ClassificacaoEspecial> classificacoes = query.getResultList();

            List<String> nomesClassificacoes = classificacoes.stream()
                    .map(ClassificacaoEspecial::getClassificacaoEspecial)
                    .toList();

            comboClassificacaoEspecial.setItems(FXCollections.observableArrayList(nomesClassificacoes));


        } catch (Exception e) {
            System.err.println("Erro ao carregar classificações especiais: " + e.getMessage());
            e.printStackTrace();
            comboClassificacaoEspecial.setItems(FXCollections.observableArrayList("Nenhum"));
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
                    "SELECT a FROM Alergia a " +
                            "ORDER BY CASE WHEN a.nomeAlergia = 'Nenhum' THEN 0 ELSE 1 END, a.nomeAlergia",
                    Alergia.class
            );

            List<Alergia> alergias = query.getResultList();

            List<String> nomesAlergias = alergias.stream()
                    .map(Alergia::getNomeAlergia)
                    .toList();

            comboAlergias.setItems(FXCollections.observableArrayList(nomesAlergias));


        } catch (Exception e) {
            System.err.println("Erro ao carregar alergias: " + e.getMessage());
            e.printStackTrace();
            comboAlergias.setItems(FXCollections.observableArrayList("Nenhum"));
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
                    "SELECT t FROM TipoAuxilio t " +
                            "ORDER BY CASE WHEN t.nomeAuxilio = 'Nenhum' THEN 0 ELSE 1 END, t.nomeAuxilio",
                    TipoAuxilio.class
            );

            List<TipoAuxilio> tiposAuxilio = query.getResultList();

            List<String> nomesAuxilios = tiposAuxilio.stream()
                    .map(TipoAuxilio::getNomeAuxilio)
                    .toList();

            comboTipoAuxilio.setItems(FXCollections.observableArrayList(nomesAuxilios));

        } catch (Exception e) {
            System.err.println("Erro ao carregar tipos de auxílio: " + e.getMessage());
            e.printStackTrace();
            comboTipoAuxilio.setItems(FXCollections.observableArrayList("Nenhum"));
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void configurarBensFamilia() {
        try {
            // Inicializar o template
            checkBoxTemplateBens = new CheckBoxTemplate();

            // Carregar tipos de bem do banco
            List<TipoBem> todosTiposBem = carregarTiposBemDoBanco();
            checkBoxTemplateBens.carregarTiposBem(todosTiposBem);

            // Adicionar ao container
            containerBens.getChildren().clear();
            containerBens.getChildren().add(checkBoxTemplateBens);

        } catch (Exception e) {
            System.err.println("Erro crítico ao configurar bens da família: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<TipoBem> carregarTiposBemDoBanco() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            TypedQuery<TipoBem> query = em.createQuery(
                    "SELECT t FROM TipoBem t ORDER BY t.nomeBem",
                    TipoBem.class
            );

            List<TipoBem> tiposBem = query.getResultList();

            return tiposBem;

        } catch (Exception e) {
            System.err.println("Erro ao carregar tipos de bem: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // Obter bens selecionados para salvar
    private List<TipoBem> getBensSelecionados() {
        if (checkBoxTemplateBens != null) {
            List<TipoBem> selecionados = new ArrayList<>(checkBoxTemplateBens.getTiposBemSelecionados());

            return selecionados;
        }
        return new ArrayList<>();
    }

    // Limpar seleção de bens
    private void limparBensSelecionados() {
        if (checkBoxTemplateBens != null) {
            checkBoxTemplateBens.clearAllSelections();
        }
    }

    // === PESQUISAR MÃES ===
    @FXML
    private void pesquisarMae() {
        String termoPesquisa = fieldPesquisaMae.getText().trim();

        if (termoPesquisa.isEmpty()) {
            carregarMaes();
        } else {
            pesquisarResponsaveisPorTipoENome(2L, termoPesquisa, cardsContainerMaes, "Nenhuma mãe encontrada");
        }
    }

    // === PESQUISAR PAIS ===
    @FXML
    private void pesquisarPai() {
        String termoPesquisa = fieldPesquisaPai.getText().trim();

        if (termoPesquisa.isEmpty()) {
            carregarPais();
        } else {
            pesquisarResponsaveisPorTipoENome(1L, termoPesquisa, cardsContainerPais, "Nenhum pai encontrado");
        }
    }

    // === PESQUISAR RESPONSÁVEIS ===
    @FXML
    private void pesquisarResponsavel() {
        String termoPesquisa = fieldPesquisaResponsavel.getText().trim();

        if (termoPesquisa.isEmpty()) {
            carregarResponsaveis();
        } else {
            pesquisarResponsaveisPorTipoENome(3L, termoPesquisa, cardsContainerResponsaveis, "Nenhum responsável encontrado");
        }
    }

    // === MÉTODO GENÉRICO PARA PESQUISAR POR TIPO E NOME ===
    private void pesquisarResponsaveisPorTipoENome(Long tipoId, String termoPesquisa, VBox container, String mensagemVazio) {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            List<Responsavel> resultados = em.createQuery(
                            "SELECT r FROM Responsavel r " +
                                    "JOIN FETCH r.pessoa p " +
                                    "JOIN r.tipoResponsavel tr " +
                                    "WHERE tr.id = :tipoId " +
                                    "AND (UPPER(p.nome) LIKE UPPER(:termo) " +
                                    "     OR UPPER(p.cpf) LIKE UPPER(:termo)) " +
                                    "ORDER BY p.nome",
                            Responsavel.class
                    )
                    .setParameter("tipoId", tipoId)
                    .setParameter("termo", "%" + termoPesquisa + "%")
                    .setMaxResults(10)
                    .getResultList();

            atualizarCardsContainer(container, resultados, mensagemVazio);

        } catch (Exception e) {
            System.err.println("Erro na pesquisa: " + e.getMessage());
            e.printStackTrace();
            limparContainerComMensagem(container, "Erro na pesquisa");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === CARREGAR MÃES ===
    private void carregarMaes() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            List<Responsavel> maes = em.createQuery(
                    "SELECT r FROM Responsavel r " +
                            "JOIN FETCH r.pessoa p " +
                            "JOIN r.tipoResponsavel tr " +
                            "WHERE tr.id = 2 " +
                            "ORDER BY r.id DESC",
                    Responsavel.class
            ).setMaxResults(5).getResultList();

            atualizarCardsContainer(cardsContainerMaes, maes, "Nenhuma mãe cadastrada");

            atualizarSelecaoAposCarregar(cardsContainerMaes);

        } catch (Exception e) {
            System.err.println("Erro ao carregar mães: " + e.getMessage());
            limparContainerComMensagem(cardsContainerMaes, "Erro ao carregar mães");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === CARREGAR PAIS ===
    private void carregarPais() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            List<Responsavel> pais = em.createQuery(
                    "SELECT r FROM Responsavel r " +
                            "JOIN FETCH r.pessoa p " +
                            "JOIN r.tipoResponsavel tr " +
                            "WHERE tr.id = 1 " +
                            "ORDER BY r.id DESC",
                    Responsavel.class
            ).setMaxResults(5).getResultList();

            atualizarCardsContainer(cardsContainerPais, pais, "Nenhum pai cadastrado");

            atualizarSelecaoAposCarregar(cardsContainerPais);

        } catch (Exception e) {
            System.err.println("Erro ao carregar pais: " + e.getMessage());
            limparContainerComMensagem(cardsContainerPais, "Erro ao carregar pais");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === CARREGAR RESPONSÁVEIS ===
    private void carregarResponsaveis() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            List<Responsavel> responsaveis = em.createQuery(
                    "SELECT r FROM Responsavel r " +
                            "JOIN FETCH r.pessoa p " +
                            "JOIN r.tipoResponsavel tr " +
                            "WHERE tr.id = 3 " +
                            "ORDER BY r.id DESC",
                    Responsavel.class
            ).setMaxResults(5).getResultList();

            atualizarCardsContainer(cardsContainerResponsaveis, responsaveis, "Nenhum responsável cadastrado");

            atualizarSelecaoAposCarregar(cardsContainerResponsaveis);

        } catch (Exception e) {
            System.err.println("Erro ao carregar responsáveis: " + e.getMessage());
            limparContainerComMensagem(cardsContainerResponsaveis, "Erro ao carregar responsáveis");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    private void atualizarCardsContainer(VBox container, List<Responsavel> responsaveis, String mensagemVazio) {
        container.getChildren().clear();

        if (responsaveis == null || responsaveis.isEmpty()) {
            EmptyCard vazio = new EmptyCard(mensagemVazio);
            container.getChildren().add(vazio);
        } else {
            for (Responsavel responsavel : responsaveis) {
                try {
                    final Responsavel responsavelFinal = responsavel;
                    ResponsavelCard card = new ResponsavelCard(responsavelFinal);

                    // configura ações do card
                    configurarAcoesDoCard(card, container, responsavelFinal);

                    container.getChildren().add(card);

                } catch (Exception e) {
                    System.err.println("Erro ao criar card: " + e.getMessage());
                    Label erroCard = new Label("Erro ao carregar");
                    erroCard.setStyle("-fx-background-color: #ffebee; -fx-border-color: #f44336; " +
                            "-fx-padding: 10; -fx-border-radius: 5;");
                    container.getChildren().add(erroCard);
                }
            }
        }
    }

    // MÉTODOS PARA GERENCIAR SELEÇÃO DE RESPONSÁVEIS
    private void configurarSelecaoResponsaveis() {
    }

    // OBTER RESPONSÁVEL SELECIONADO DE UM CONTAINER
    private Responsavel getResponsavelSelecionado(VBox container) {
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof ResponsavelCard) {
                ResponsavelCard card = (ResponsavelCard) node;
                if (card.isSelecionado()) {
                    return card.getResponsavel();
                }
            }
        }
        return null;
    }

    // LIMPAR DETECÇÃO DE IRMÃOS
    private void limparDetecaoIrmaos() {
        irmaosEncontrados.clear();
        containerIrmaos.getChildren().clear();
        EmptyCard emptyCard = new EmptyCard("Selecione uma mãe ou pai para detectar irmãos automaticamente.");
        containerIrmaos.getChildren().add(emptyCard);
    }

    // ATUALIZAR DETECÇÃO DE IRMÃOS QUANDO CARDS SÃO ADICIONADOS
    private void atualizarSelecaoAposCarregar(VBox container) {
        Platform.runLater(() -> {
            for (javafx.scene.Node node : container.getChildren()) {
                if (node instanceof ResponsavelCard) {
                    ResponsavelCard card = (ResponsavelCard) node;
                    configurarAcoesDoCard(card, container, card.getResponsavel());
                }
            }
        });
    }

    // Método para configurar ações
    private void configurarAcoesDoCard(ResponsavelCard card, VBox container, Responsavel responsavel) {
        card.setOnSelectAction(() -> {

            // Desmarca outros cards no mesmo container (seleção única)
            for (javafx.scene.Node outroNode : container.getChildren()) {
                if (outroNode instanceof ResponsavelCard && outroNode != card) {
                    ResponsavelCard outroCard = (ResponsavelCard) outroNode;
                    if (outroCard.isSelecionado()) {
                        System.out.println("➖ Desmarcando: " + outroCard.getResponsavel().getPessoa().getNome());
                        outroCard.setSelecionado(false);
                    }
                }
            }

            // Atualiza a seleção baseada no container
            if (container == cardsContainerMaes) {
                maeSelecionada = responsavel;
            } else if (container == cardsContainerPais) {
                paiSelecionado = responsavel;
            } else if (container == cardsContainerResponsaveis) {
                responsavelSelecionado = responsavel;
            }

            detectarIrmaosAutomaticamente();
        });

        card.setOnDeselectAction(() -> {
            System.out.println("Deselecionado: " + responsavel.getPessoa().getNome());

            // Remove a seleção
            if (container == cardsContainerMaes) {
                maeSelecionada = null;
            } else if (container == cardsContainerPais) {
                paiSelecionado = null;
            } else if (container == cardsContainerResponsaveis) {
                responsavelSelecionado = null;
            }

            limparDetecaoIrmaos();
        });

        // CONFIGURAR AÇÃO DE EDIÇÃO SEPARADAMENTE
        card.setOnEditAction(() -> {
            System.out.println("✏EDIT ACTION - Editando: " + responsavel.getPessoa().getNome());
        });
    }

    // === CONFIGURAR PESQUISA POR ENTER ===
    private void configurarPesquisaPorEnter() {
        fieldPesquisaMae.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pesquisarMae();
            }
        });

        fieldPesquisaPai.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pesquisarPai();
            }
        });

        fieldPesquisaResponsavel.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pesquisarResponsavel();
            }
        });
    }

    // === MÉTODO PARA LIMPAR CONTAINER COM MENSAGEM ===
    private void limparContainerComMensagem(VBox container, String mensagem) {
        container.getChildren().clear();
        EmptyCard erro = new EmptyCard(mensagem);
        container.getChildren().add(erro);
    }

    // Métodos para abrir os modais
    @FXML
    private void abrirCadastroResponsavel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/salo/sistemacreche/extracadastro/filiaçãoResponsavel.fxml"));
            Parent root = loader.load();

            FiliacaoResponsavelController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastro de Responsável");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(btnSalvar.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                Responsavel responsavelSalvo = controller.getResponsavelSalvo();
                if (responsavelSalvo != null) {
                    Long tipoId = responsavelSalvo.getTipoResponsavel().getId();
                    if (tipoId == 2L) {
                        carregarMaes();
                    } else if (tipoId == 1L) {
                        carregarPais();
                    } else {
                        carregarResponsaveis();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao abrir cadastro de responsável");
        }
    }

    @FXML
    private void abrirCadastroMembroFamiliar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/salo/sistemacreche/extracadastro/membrofamiliar.fxml"));
            Parent root = loader.load();

            MembroFamiliarController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastro de Membro Familiar");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(btnSalvar.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                MembroFamiliarController.DadosMembroFamiliar dados = controller.getDadosMembro();
                adicionarMembroFamiliar(dados);
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao abrir cadastro de membro familiar");
        }
    }

    @FXML
    private void abrirCadastroPessoaAutorizada() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/salo/sistemacreche/extracadastro/pessoaautorizada.fxml"));
            Parent root = loader.load();

            PessoaAutorizadaController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastro de Pessoa Autorizada");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(btnSalvar.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                PessoaAutorizada pessoaSalva = controller.getPessoaAutorizadaSalva();
                if (pessoaSalva != null) {
                    adicionarPessoaAutorizada(pessoaSalva);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao abrir cadastro de pessoa autorizada");
        }
    }

    private void configurarTableViews() {
        configurarTableViewComposicaoFamiliar();
        configurarTableViewPessoasAutorizadas();
    }

    private void configurarTableViewComposicaoFamiliar() {
        tableComposicaoFamiliar.getColumns().clear();

        TableColumn<MembroFamilia, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(150);

        TableColumn<MembroFamilia, String> colIdade = new TableColumn<>("Idade");
        colIdade.setCellValueFactory(new PropertyValueFactory<>("idade"));
        colIdade.setPrefWidth(80);

        TableColumn<MembroFamilia, String> colParentesco = new TableColumn<>("Parentesco");
        colParentesco.setCellValueFactory(new PropertyValueFactory<>("parentesco"));
        colParentesco.setPrefWidth(100);

        TableColumn<MembroFamilia, String> colEscolaridade = new TableColumn<>("Escolaridade");
        colEscolaridade.setCellValueFactory(new PropertyValueFactory<>("situacaoEscolar"));
        colEscolaridade.setPrefWidth(120);

        TableColumn<MembroFamilia, String> colEmprego = new TableColumn<>("Emprego");
        colEmprego.setCellValueFactory(new PropertyValueFactory<>("situacaoEmprego"));
        colEmprego.setPrefWidth(120);

        TableColumn<MembroFamilia, String> colRenda = new TableColumn<>("Renda");
        colRenda.setCellValueFactory(new PropertyValueFactory<>("renda"));
        colRenda.setPrefWidth(100);

        tableComposicaoFamiliar.getColumns().addAll(colNome, colIdade, colParentesco, colEscolaridade, colEmprego, colRenda);
        tableComposicaoFamiliar.setItems(membrosFamiliares);
    }

    private void configurarTableViewPessoasAutorizadas() {
        tablePessoasAutorizadas.getColumns().clear();

        TableColumn<PessoaAutorizada, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(cellData -> {
            PessoaAutorizada pessoaAut = cellData.getValue();
            return new SimpleStringProperty(
                    pessoaAut.getPessoa() != null ? pessoaAut.getPessoa().getNome() : ""
            );
        });
        colNome.setPrefWidth(150);

        TableColumn<PessoaAutorizada, String> colParentesco = new TableColumn<>("Parentesco");
        colParentesco.setCellValueFactory(cellData -> {
            PessoaAutorizada pessoaAut = cellData.getValue();
            return new SimpleStringProperty(
                    pessoaAut.getParentesco() != null ? pessoaAut.getParentesco().toString() : ""
            );
        });
        colParentesco.setPrefWidth(100);

        TableColumn<PessoaAutorizada, String> colRg = new TableColumn<>("RG");
        colRg.setCellValueFactory(cellData -> {
            PessoaAutorizada pessoaAut = cellData.getValue();
            return new SimpleStringProperty(
                    pessoaAut.getPessoa() != null ? pessoaAut.getPessoa().getRg() : ""
            );
        });
        colRg.setPrefWidth(120);

        TableColumn<PessoaAutorizada, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(cellData -> {
            PessoaAutorizada pessoaAut = cellData.getValue();
            return new SimpleStringProperty(
                    pessoaAut.getPessoa() != null ? pessoaAut.getPessoa().getTelefone() : ""
            );
        });
        colTelefone.setPrefWidth(120);

        tablePessoasAutorizadas.getColumns().addAll(colNome, colParentesco, colRg, colTelefone);
        tablePessoasAutorizadas.setItems(pessoaAutorizadas);
    }

    // Método para adicionar membro familiar à tabela
    public void adicionarMembroFamiliar(MembroFamiliarController.DadosMembroFamiliar dados) {
        try {
            MembroFamilia membro = new MembroFamilia();

            membro.setNome(dados.getNome());

            try {
                membro.setIdade(Integer.parseInt(dados.getIdade()));
            } catch (NumberFormatException e) {
                membro.setIdade(0);
            }

            MembroFamilia.Parentesco parentesco = converterStringParaParentesco(dados.getParentesco());
            membro.setParentesco(parentesco);

            MembroFamilia.SituacaoEscolar escolaridade = converterParaSituacaoEscolar(dados.getEscolaridade());
            membro.setSituacaoEscolar(escolaridade);

            MembroFamilia.SituacaoEmprego emprego = converterParaSituacaoEmprego(dados.getEmprego());
            membro.setSituacaoEmprego(emprego);

            if (!dados.getRenda().isEmpty()) {
                try {
                    String rendaFormatada = dados.getRenda().replace(",", ".");
                    membro.setRenda(new BigDecimal(rendaFormatada));
                } catch (NumberFormatException e) {
                    membro.setRenda(BigDecimal.ZERO);
                }
            } else {
                membro.setRenda(BigDecimal.ZERO);
            }

            membrosFamiliares.add(membro);
            tableComposicaoFamiliar.refresh();


        } catch (Exception e) {
            System.err.println("Erro ao adicionar membro familiar: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao adicionar membro familiar: " + e.getMessage());
        }
    }

    // Método para adicionar pessoa autorizada à tabela
    public void adicionarPessoaAutorizada(PessoaAutorizada pessoaAutorizada) {
        try {
            pessoaAutorizadas.add(pessoaAutorizada);
            tablePessoasAutorizadas.refresh();

        } catch (Exception e) {
            System.err.println("Erro ao adicionar pessoa autorizada: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao adicionar pessoa autorizada: " + e.getMessage());
        }
    }

    // Método para converter string para enum SituacaoEscolar
    private MembroFamilia.SituacaoEscolar converterParaSituacaoEscolar(String escolaridade) {
        if (escolaridade == null) return MembroFamilia.SituacaoEscolar.NAO_INFORMADO;

        try {
            return MembroFamilia.SituacaoEscolar.valueOf(escolaridade);
        } catch (IllegalArgumentException e) {
            System.err.println("Valor de escolaridade não encontrado: " + escolaridade);
            return MembroFamilia.SituacaoEscolar.NAO_INFORMADO;
        }
    }

    // Método para converter string para enum SituacaoEmprego
    private MembroFamilia.SituacaoEmprego converterParaSituacaoEmprego(String emprego) {
        if (emprego == null) return MembroFamilia.SituacaoEmprego.OUTRO;

        try {
            return MembroFamilia.SituacaoEmprego.valueOf(emprego);
        } catch (IllegalArgumentException e) {
            System.err.println("Valor de emprego não encontrado: " + emprego);
            return MembroFamilia.SituacaoEmprego.OUTRO;
        }
    }

    private MembroFamilia.Parentesco converterStringParaParentesco(String parentesco) {
        if (parentesco == null) return MembroFamilia.Parentesco.OUTRO;

        String parentescoUpper = parentesco.toUpperCase()
                .replace("Ã", "A")
                .replace("Õ", "O")
                .replace("Â", "A")
                .replace("Ô", "O");

        try {
            return MembroFamilia.Parentesco.valueOf(parentescoUpper);
        } catch (IllegalArgumentException e) {
            switch (parentescoUpper) {
                case "MAE": case "MÃE": return MembroFamilia.Parentesco.MAE;
                case "PAI": return MembroFamilia.Parentesco.PAI;
                case "IRMAO": case "IRMÃO": return MembroFamilia.Parentesco.IRMAO;
                case "IRMA": case "IRMÃ": return MembroFamilia.Parentesco.IRMA;
                case "AVO": case "AVÔ": case "AVÓ": return MembroFamilia.Parentesco.AVO;
                case "TIO": return MembroFamilia.Parentesco.TIO;
                case "TIA": return MembroFamilia.Parentesco.TIA;
                default: return MembroFamilia.Parentesco.OUTRO;
            }
        }
    }

    // MÉTODO PARA EXIBIR IRMÃOS ENCONTRADOS
    private void exibirIrmaosEncontrados() {
        containerIrmaos.getChildren().clear();

        if (irmaosEncontrados.isEmpty()) {
            EmptyCard emptyCard = new EmptyCard("Nenhum irmão encontrado com os mesmos pais.");
            containerIrmaos.getChildren().add(emptyCard);
            return;
        }

        Label titulo = new Label("Irmãos encontrados (mesmos pais):");
        titulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f766e; -fx-font-size: 14;");
        containerIrmaos.getChildren().add(titulo);

        for (Crianca irmao : irmaosEncontrados) {
            IrmaoCard cardIrmao = criarIrmaoCard(irmao);
            containerIrmaos.getChildren().add(cardIrmao);
        }
    }

    // MÉTODO PARA CRIAR CARD DE IRMÃO
    private IrmaoCard criarIrmaoCard(Crianca irmao) {
        IrmaoCard card = new IrmaoCard(irmao);

        card.setOnGemeoSelected(() -> {
            System.out.println(irmao.getNome() + " marcado(a) como gêmeo(a)");
        });

        card.setOnGemeoDeselected(() -> {
            System.out.println(irmao.getNome() + " desmarcado(a) como gêmeo(a)");
        });

        return card;
    }

    // Método para obter o irmão gêmeo selecionado:
    private boolean possuiIrmaoGemeoSelecionado() {
        for (javafx.scene.Node node : containerIrmaos.getChildren()) {
            if (node instanceof IrmaoCard) {
                IrmaoCard card = (IrmaoCard) node;
                if (card.isSelecionado()) {
                    return true;
                }
            }
        }
        return false;
    }

    // MÉTODO para obter o irmão gêmeo selecionado
    private Crianca getIrmaoGemeoSelecionado() {
        for (javafx.scene.Node node : containerIrmaos.getChildren()) {
            if (node instanceof IrmaoCard) {
                IrmaoCard card = (IrmaoCard) node;
                if (card.isSelecionado()) {
                    return card.getIrmao();
                }
            }
        }
        return null;
    }

    public void limparSelecaoGemeos() {
        for (javafx.scene.Node node : containerIrmaos.getChildren()) {
            if (node instanceof IrmaoCard) {
                IrmaoCard card = (IrmaoCard) node;
                card.setSelecionado(false);
            }
        }
    }

    // MÉTODO PARA DETECTAR IRMÃOS AUTOMATICAMENTE
    private void detectarIrmaosAutomaticamente() {
        System.out.println("🔍 INICIANDO DETECÇÃO DE IRMÃOS...");

        irmaosEncontrados.clear();
        containerIrmaos.getChildren().clear();
        Responsavel maeAtual = maeSelecionada;
        Responsavel paiAtual = paiSelecionado;

        System.out.println("👩 Mãe selecionada: " + (maeAtual != null ? maeAtual.getPessoa().getNome() : "Nenhuma"));
        System.out.println("👨 Pai selecionado: " + (paiAtual != null ? paiAtual.getPessoa().getNome() : "Nenhum"));

        if (maeAtual == null && paiAtual == null) {
            EmptyCard emptyCard = new EmptyCard("Selecione uma mãe ou pai para detectar irmãos automaticamente.");
            containerIrmaos.getChildren().add(emptyCard);
            System.out.println("ℹ️ Nenhum pai/mãe selecionado - mostrando mensagem");
            return;
        }

        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            StringBuilder queryBuilder = new StringBuilder(
                    "SELECT c FROM Crianca c WHERE 1=1 "
            );

            // Usar IDs dos pais selecionados
            if (maeAtual != null) {
                queryBuilder.append("AND c.mae.id = :idMae ");
            }
            if (paiAtual != null) {
                queryBuilder.append("AND c.pai.id = :idPai ");
            }

            queryBuilder.append("ORDER BY c.dataNascimento DESC");

            TypedQuery<Crianca> query = em.createQuery(queryBuilder.toString(), Crianca.class);

            if (maeAtual != null) {
                query.setParameter("idMae", maeAtual.getPessoa().getId());
                System.out.println("Buscando por mãe ID: " + maeAtual.getPessoa().getId());
            }
            if (paiAtual != null) {
                query.setParameter("idPai", paiAtual.getPessoa().getId());
                System.out.println("Buscando por pai ID: " + paiAtual.getPessoa().getId());
            }

            List<Crianca> irmaos = query.getResultList();

            if (!irmaos.isEmpty()) {
                irmaosEncontrados.addAll(irmaos);
                exibirIrmaosEncontrados();

                System.out.println(irmaos.size() + " irmão(s) encontrado(s) com os mesmos pais");
                for (Crianca irmao : irmaos) {
                    System.out.println(irmao.getNome() + " - Nasc: " + irmao.getDataNascimento());
                }

            } else {
                EmptyCard emptyCard = new EmptyCard("Nenhum irmão encontrado com os pais selecionados.");
                containerIrmaos.getChildren().add(emptyCard);
                System.out.println("ℹ️ Nenhum irmão encontrado com os pais selecionados");
            }

        } catch (Exception e) {
            System.err.println("Erro ao detectar irmãos: " + e.getMessage());
            e.printStackTrace();
            EmptyCard erroCard = new EmptyCard("Erro ao buscar irmãos: " + e.getMessage());
            containerIrmaos.getChildren().add(erroCard);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @FXML
    public void cancelarCadastro() {
        limparFormulario();
        mostrarMensagem("Informação", "Cadastro de pré-matrícula cancelado");
    }

    // Configurar validações
    private void configurarValidacoes() {
        // Validar data de nascimento (não pode ser futura)
        datePickerNascimento.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isAfter(LocalDate.now())) {
                mostrarMensagem("Erro", "Data de nascimento não pode ser futura!");
                datePickerNascimento.setValue(oldValue);
            }
        });

        // Validar ano letivo
        fieldAnoLetivo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                fieldAnoLetivo.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 4) {
                fieldAnoLetivo.setText(newValue.substring(0, 4));
            }
        });
    }

    private boolean auxilioExigeNIS() {
        String tipoAuxilio = comboTipoAuxilio.getValue();
        return tipoAuxilio != null && !tipoAuxilio.equals("Nenhum");
    }

    // Validação em tempo real do NIS
    private void validarNis() {
        boolean exigeNIS = auxilioExigeNIS();
        String nis = fieldNis.getText().trim();

        if (exigeNIS) {
            if (nis.isEmpty()) {
                fieldNis.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2px;");
            } else if (nis.length() != 11) {
                fieldNis.setStyle("-fx-border-color: #ff8800; -fx-border-width: 2px;");
            } else {
                fieldNis.setStyle("-fx-border-color: #00ff00; -fx-border-width: 1px;");
            }
        } else {
            fieldNis.setStyle("");
        }
    }

    // Validação no salvamento
    private boolean validarAuxilioComNIS() {
        boolean exigeNIS = auxilioExigeNIS();
        String nis = fieldNis.getText().trim();

        if (exigeNIS && (nis.isEmpty() || nis.length() != 11)) {
            mostrarMensagem("Validação", "O auxílio selecionado exige o preenchimento do NIS (11 dígitos)!");
            fieldNis.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarCamposObrigatorios() {
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

        // Validar se pelo menos um responsável foi selecionado
        if (maeSelecionada == null && paiSelecionado == null && responsavelSelecionado == null) {
            mostrarMensagem("Erro", "Pelo menos um responsável (mãe, pai ou responsável) deve ser selecionado!");
            return false;
        }

        return true;
    }

    // MÉTODO COMPLETO PARA APLICAR TODAS AS MÁSCARAS
    private void aplicarMascaras() {
        aplicarMascaraNis();
        aplicarMascaraCPF();
        aplicarMascaraCEP();
        aplicarMascaraTelefone();
        aplicarMascaraAnoLetivo();
        aplicarMascaraValorAluguel();
        aplicarMascaraNumeroComodos();
        aplicarMascaraNumeroEndereco();
        aplicarMascaraRG();
        aplicarMascaraCertidao();
        aplicarMascaraCartorio();
    }

    // MÁSCARA PARA NIS
    private void aplicarMascaraNis() {
        fieldNis.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String apenasDigitos = newValue.replaceAll("[^\\d]", "");

            // Limitar tamanho
            if (apenasDigitos.length() > Integer.parseInt(NIS_LENGTH)) {
                apenasDigitos = apenasDigitos.substring(0, Integer.parseInt(NIS_LENGTH));
            }

            fieldNis.setText(apenasDigitos);
            validarNis();
        });
    }

    // MÁSCARA PARA CPF
    private void aplicarMascaraCPF() {
        fieldCpfCrianca.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String apenasDigitos = newValue.replaceAll("[^\\d]", "");

            // Limitar tamanho
            if (apenasDigitos.length() > Integer.parseInt(CPF_LENGTH)) {
                apenasDigitos = apenasDigitos.substring(0, Integer.parseInt(CPF_LENGTH));
            }

            // Aplicar formatação: 000.000.000-00
            if (apenasDigitos.length() <= 11) {
                StringBuilder formatado = new StringBuilder();
                for (int i = 0; i < apenasDigitos.length(); i++) {
                    if (i == 3 || i == 6) {
                        formatado.append(".");
                    } else if (i == 9) {
                        formatado.append("-");
                    }
                    formatado.append(apenasDigitos.charAt(i));
                }
                fieldCpfCrianca.setText(formatado.toString());
            } else {
                fieldCpfCrianca.setText(apenasDigitos);
            }
        });
    }

    // MÁSCARA PARA CEP
    private void aplicarMascaraCEP() {
        fieldCEP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String apenasDigitos = newValue.replaceAll("[^\\d]", "");

            // Limitar tamanho
            if (apenasDigitos.length() > Integer.parseInt(CEP_LENGTH)) {
                apenasDigitos = apenasDigitos.substring(0, Integer.parseInt(CEP_LENGTH));
            }

            // Aplicar formatação: 00000-000
            if (apenasDigitos.length() > 5) {
                String parte1 = apenasDigitos.substring(0, 5);
                String parte2 = apenasDigitos.substring(5);
                fieldCEP.setText(parte1 + "-" + parte2);
            } else {
                fieldCEP.setText(apenasDigitos);
            }
        });
    }

    // MÁSCARA PARA TELEFONE (aceita celular e fixo)
    private void aplicarMascaraTelefone() {
        // Telefone residencial
        fieldTelefoneResidencial.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String apenasDigitos = newValue.replaceAll("[^\\d]", "");

            // Limitar tamanho
            if (apenasDigitos.length() > Integer.parseInt(TELEFONE_LENGTH)) {
                apenasDigitos = apenasDigitos.substring(0, Integer.parseInt(TELEFONE_LENGTH));
            }

            fieldTelefoneResidencial.setText(formatarTelefone(apenasDigitos));
        });

        // Telefone contato
        fieldTelefoneContato.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String apenasDigitos = newValue.replaceAll("[^\\d]", "");

            // Limitar tamanho
            if (apenasDigitos.length() > Integer.parseInt(TELEFONE_LENGTH)) {
                apenasDigitos = apenasDigitos.substring(0, Integer.parseInt(TELEFONE_LENGTH));
            }

            fieldTelefoneContato.setText(formatarTelefone(apenasDigitos));
        });
    }

    // MÉTODO AUXILIAR PARA FORMATAR TELEFONE
    private String formatarTelefone(String digitos) {
        if (digitos == null || digitos.isEmpty()) return "";

        if (digitos.length() == 11) { // Celular com DDD: (00) 00000-0000
            return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 7) + "-" + digitos.substring(7);
        } else if (digitos.length() == 10) { // Fixo com DDD: (00) 0000-0000
            return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 6) + "-" + digitos.substring(6);
        } else if (digitos.length() == 8) { // Número sem DDD: 0000-0000
            return digitos.substring(0, 4) + "-" + digitos.substring(4);
        } else {
            return digitos;
        }
    }

    // MÁSCARA PARA ANO LETIVO
    private void aplicarMascaraAnoLetivo() {
        fieldAnoLetivo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String apenasDigitos = newValue.replaceAll("[^\\d]", "");

            // Limitar a 4 dígitos
            if (apenasDigitos.length() > Integer.parseInt(ANO_LETIVO_LENGTH)) {
                apenasDigitos = apenasDigitos.substring(0, Integer.parseInt(ANO_LETIVO_LENGTH));
            }

            fieldAnoLetivo.setText(apenasDigitos);

            // Validação adicional: ano não pode ser menor que 2000
            if (!apenasDigitos.isEmpty()) {
                try {
                    int ano = Integer.parseInt(apenasDigitos);
                    if (ano < 2000) {
                        fieldAnoLetivo.setStyle("-fx-border-color: #ff4444;");
                    } else {
                        fieldAnoLetivo.setStyle("");
                    }
                } catch (NumberFormatException e) {
                    // Ignora - já filtramos apenas dígitos
                }
            }
        });
    }

    // MÁSCARA PARA VALOR DO ALUGUEL
    private void aplicarMascaraValorAluguel() {
        fieldValorAluguel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;

            // Permite números, vírgula e ponto
            String valorLimpo = newValue.replaceAll("[^\\d,.]", "");

            // Garante que há apenas uma vírgula ou ponto decimal
            if (valorLimpo.chars().filter(ch -> ch == ',' || ch == '.').count() > 1) {
                // Mantém o valor antigo se tentar adicionar múltiplos separadores
                fieldValorAluguel.setText(oldValue);
                return;
            }

            // Se terminar com vírgula ou ponto
            if (valorLimpo.matches(".*[,.]$")) {
                fieldValorAluguel.setText(valorLimpo);
                return;
            }

            // Formata como moeda brasileira
            try {
                if (!valorLimpo.isEmpty()) {
                    // Substitui vírgula por ponto para parse
                    String paraParse = valorLimpo.replace(",", ".");
                    double valor = Double.parseDouble(paraParse);

                    // Formata para o padrão brasileiro: 1.234,56
                    java.text.NumberFormat format = java.text.NumberFormat.getNumberInstance(new java.util.Locale("pt", "BR"));
                    format.setMinimumFractionDigits(2);
                    format.setMaximumFractionDigits(2);
                    fieldValorAluguel.setText(format.format(valor));
                }
            } catch (NumberFormatException e) {
                // Em caso de erro, mantém o valor limpo
                fieldValorAluguel.setText(valorLimpo);
            }
        });
    }

    // MÁSCARA PARA NÚMERO DE CÔMODOS
    private void aplicarMascaraNumeroComodos() {
        fieldNumeroComodos.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String apenasDigitos = newValue.replaceAll("[^\\d]", "");

            // Limitar a 2 dígitos
            if (apenasDigitos.length() > 2) {
                apenasDigitos = apenasDigitos.substring(0, 2);
            }

            fieldNumeroComodos.setText(apenasDigitos);
        });
    }

    // MÁSCARA PARA NÚMERO DO ENDEREÇO
    private void aplicarMascaraNumeroEndereco() {
        fieldNumero.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            // Permite números, letras e alguns caracteres especiais comuns em endereços
            String valorLimpo = newValue.replaceAll("[^\\d\\w\\s\\-\\/]", "");

            // Limitar tamanho
            if (valorLimpo.length() > 10) {
                valorLimpo = valorLimpo.substring(0, 10);
            }

            fieldNumero.setText(valorLimpo);
        });
    }

    // MÁSCARA PARA RG
    private void aplicarMascaraRG() {
        fieldRgCrianca.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            // Permite números, pontos, traços e X (para dígito verificador)
            String valorLimpo = newValue.replaceAll("[^\\d\\-\\.Xx]", "");

            // Converte X para maiúsculo
            valorLimpo = valorLimpo.toUpperCase();

            fieldRgCrianca.setText(valorLimpo);
        });
    }

    // MÁSCARA PARA CERTIDÃO DE NASCIMENTO
    private void aplicarMascaraCertidao() {
        fieldCertidaoNascimento.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            // Permite números, letras, traços, pontos e barras
            String valorLimpo = newValue.replaceAll("[^\\d\\w\\-\\.\\/]", "");

            fieldCertidaoNascimento.setText(valorLimpo);
        });
    }

    // MÁSCARA PARA CARTÓRIO DE REGISTRO
    private void aplicarMascaraCartorio() {
        fieldCartorioRegistro.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            // Permite números, letras, traços, pontos e espaços
            String valorLimpo = newValue.replaceAll("[^\\d\\w\\-\\.\\s]", "");

            fieldCartorioRegistro.setText(valorLimpo);
        });
    }

    // MÉTODOS PARA OBTER DADOS LIMPOS

    private String getCpfLimpo() {
        return fieldCpfCrianca.getText() != null ?
                fieldCpfCrianca.getText().replaceAll("[^\\d]", "") : "";
    }

    private String getCepLimpo() {
        return fieldCEP.getText() != null ?
                fieldCEP.getText().replaceAll("[^\\d]", "") : "";
    }

    private String getTelefoneResidencialLimpo() {
        return fieldTelefoneResidencial.getText() != null ?
                fieldTelefoneResidencial.getText().replaceAll("[^\\d]", "") : "";
    }

    private String getTelefoneContatoLimpo() {
        return fieldTelefoneContato.getText() != null ?
                fieldTelefoneContato.getText().replaceAll("[^\\d]", "") : "";
    }

    private String getNisLimpo() {
        return fieldNis.getText() != null ?
                fieldNis.getText().replaceAll("[^\\d]", "") : "";
    }

    private BigDecimal getValorAluguelLimpo() {
        if (fieldValorAluguel.getText() == null || fieldValorAluguel.getText().trim().isEmpty()) {
            return null;
        }

        try {
            String valorLimpo = fieldValorAluguel.getText()
                    .replace(".", "")
                    .replace(",", ".");
            return new BigDecimal(valorLimpo);
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter valor do aluguel: " + fieldValorAluguel.getText());
            return null;
        }
    }

    private Integer getNumeroComodosLimpo() {
        if (fieldNumeroComodos.getText() == null || fieldNumeroComodos.getText().trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(fieldNumeroComodos.getText());
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter número de cômodos: " + fieldNumeroComodos.getText());
            return null;
        }
    }

    @FXML
    public void salvarPreMatricula() {
        try {
            if (!validarCamposObrigatorios()) return;
            if (!validarAuxilioComNIS()) return;

            // Obter série e ano letivo
            String serie = comboSerie.getValue();
            String anoLetivo = fieldAnoLetivo.getText().trim();

            // Usar o service para criar as entidades
            CadastroMatriculaService service = new CadastroMatriculaService();

            // Obter listas dos componentes visuais
            List<TipoBem> bensSelecionados = getBensSelecionados();
            List<MembroFamilia> membrosFamiliares = new ArrayList<>(this.membrosFamiliares);
            List<PessoaAutorizada> pessoasAutorizadas = new ArrayList<>(this.pessoaAutorizadas);

            boolean possuiIrmaoGemeo = possuiIrmaoGemeoSelecionado();
            Crianca irmaoGemeoSelecionado = getIrmaoGemeoSelecionado();

            // Salvar tudo usando o service
            PreMatricula preMatriculaSalva = service.salvarPreMatriculaCompleta(
                    fieldNomeCrianca.getText().trim(),
                    datePickerNascimento.getValue(),
                    fieldRgCrianca.getText().trim(),
                    fieldCpfCrianca.getText().trim(),
                    fieldCertidaoNascimento.getText().trim(),
                    fieldMunicipioNascimento.getText().trim(),
                    fieldCartorioRegistro.getText().trim(),
                    fieldMunicipioRegistro.getText().trim(),
                    datePickerEmissaoRG.getValue(),
                    fieldOrgaoEmissor.getText().trim(),
                    comboSexo.getValue(),
                    comboCorRaca.getValue(),
                    fieldSus.getText().trim(),
                    fieldUnidadeSaude.getText().trim(),
                    comboMobilidadeReduzida.getValue(),
                    "Sim".equals(comboEducacaoEspecial.getValue()),
                    comboClassificacaoEspecial.getValue(),
                    comboAlergias.getValue(),
                    possuiIrmaoGemeo,
                    comboTipoAuxilio.getValue(),
                    fieldNis.getText().trim(),
                    maeSelecionada != null ? maeSelecionada.getPessoa() : null,
                    paiSelecionado != null ? paiSelecionado.getPessoa() : null,
                    responsavelSelecionado,
                    fieldEndereco.getText().trim(),
                    fieldNumero.getText().trim(),
                    fieldBairro.getText().trim(),
                    fieldMunicipio.getText().trim(),
                    fieldCEP.getText().trim(),
                    comboUF.getValue(),
                    fieldPontoReferencia.getText().trim(),
                    fieldTelefoneResidencial.getText().trim(),
                    fieldTelefoneContato.getText().trim(),
                    comboTipoMoradia.getValue(),
                    fieldValorAluguel.getText().trim(),
                    fieldNumeroComodos.getText().trim(),
                    comboTipoPiso.getValue(),
                    comboMaterialParede.getValue(),
                    comboTipoCobertura.getValue(),
                    checkFossa.isSelected(),
                    checkCifon.isSelected(),
                    checkEnergiaEletrica.isSelected(),
                    checkAguaEncanada.isSelected(),
                    bensSelecionados,
                    membrosFamiliares,
                    pessoasAutorizadas,
                    serie,
                    anoLetivo
            );

            mostrarMensagem("Sucesso", "Pré-matrícula cadastrada com sucesso! Série: " + serie + ", Ano: " + anoLetivo);
            limparFormulario();

        } catch (Exception e) {
            System.err.println("Erro ao salvar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao salvar pré-matrícula: " + e.getMessage());
        }
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
        fieldNis.clear();

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
        fieldTelefoneResidencial.clear();
        fieldTelefoneContato.clear();

        // Limpar documentos
        fieldCertidaoNascimento.clear();
        fieldCpfCrianca.clear();
        fieldMunicipioNascimento.clear();
        fieldCartorioRegistro.clear();
        fieldMunicipioRegistro.clear();
        datePickerEmissaoRG.setValue(null);
        fieldOrgaoEmissor.clear();

        // Limpar situação habitacional
        comboTipoMoradia.setValue(null);
        fieldValorAluguel.clear();
        fieldNumeroComodos.clear();
        comboTipoPiso.setValue(null);
        comboMaterialParede.setValue(null);
        comboTipoCobertura.setValue(null);

        // Limpar bens
        limparBensSelecionados();

        // Limpar série
        comboSerie.setValue(null);
        fieldAnoLetivo.clear();

        // Limpar tabelas
        membrosFamiliares.clear();
        pessoaAutorizadas.clear();

        // Limpar seleção de responsáveis
        maeSelecionada = null;
        paiSelecionado = null;
        responsavelSelecionado = null;

        // Recarregar os containers de responsáveis
        carregarMaes();
        carregarPais();
        carregarResponsaveis();

        // Limpar irmãos
        limparDetecaoIrmaos();
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void importarAnexo() {
        System.out.println("Importando anexo...");
        // TODO: Implementar importação de anexos
    }
}