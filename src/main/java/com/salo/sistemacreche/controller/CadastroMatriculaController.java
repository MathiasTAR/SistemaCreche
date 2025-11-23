package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.*;
import com.salo.sistemacreche.controller.extracadastro.FiliacaoResponsavelController;
import com.salo.sistemacreche.controller.extracadastro.MembroFamiliarController;
import com.salo.sistemacreche.controller.extracadastro.PessoaAutorizadaController;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
    @FXML private CheckBox checkIrmaoGemeo;
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
        aplicarMascaraNis();
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
                "Berçário I", "Berçário II", "Maternal I", "Maternal II", "Pré I", "Pré II"
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

            System.out.println("✅ " + classificacoes.size() + " classificação(ões) especial(is) carregada(s)");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar classificações especiais: " + e.getMessage());
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

            System.out.println("✅ " + alergias.size() + " alergia(s) carregada(s)");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar alergias: " + e.getMessage());
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

            System.out.println("✅ " + tiposAuxilio.size() + " tipo(s) de auxílio carregado(s)");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar tipos de auxílio: " + e.getMessage());
            e.printStackTrace();
            comboTipoAuxilio.setItems(FXCollections.observableArrayList("Nenhum"));
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // NOVO MÉTODO: Configurar sistema de bens da família
    private void configurarBensFamilia() {
        // Inicializar o template
        checkBoxTemplateBens = new CheckBoxTemplate();

        // Carregar tipos de bem do banco
        List<TipoBem> todosTiposBem = carregarTiposBemDoBanco();
        checkBoxTemplateBens.carregarTiposBem(todosTiposBem);

        // Adicionar ao container
        containerBens.getChildren().clear();
        containerBens.getChildren().add(checkBoxTemplateBens);
    }

    // NOVO MÉTODO: Carregar tipos de bem do banco
    private List<TipoBem> carregarTiposBemDoBanco() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            TypedQuery<TipoBem> query = em.createQuery(
                    "SELECT t FROM TipoBem t ORDER BY t.nomeBem",
                    TipoBem.class
            );

            List<TipoBem> tiposBem = query.getResultList();
            System.out.println("✅ " + tiposBem.size() + " tipo(s) de bem carregado(s)");

            return tiposBem;

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar tipos de bem: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Retorna lista vazia em caso de erro
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // NOVO MÉTODO: Obter bens selecionados para salvar
    private List<TipoBem> getBensSelecionados() {
        if (checkBoxTemplateBens != null) {
            // Converter ObservableList para ArrayList
            System.out.println("Selecionado");
            return new ArrayList<>(checkBoxTemplateBens.getTiposBemSelecionados());
        }
        return new ArrayList<>();
    }

    // NOVO MÉTODO: Para edição - pré-selecionar bens
    private void setBensSelecionados(List<TipoBem> bensSelecionados) {
        if (checkBoxTemplateBens != null && bensSelecionados != null) {
            checkBoxTemplateBens.setTiposBemSelecionados(bensSelecionados);
        }
    }

    // NOVO MÉTODO: Limpar seleção de bens
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

            System.out.println("🔍 " + resultados.size() + " resultado(s) encontrado(s) para: " + termoPesquisa);

        } catch (Exception e) {
            System.err.println("❌ Erro na pesquisa: " + e.getMessage());
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
            System.out.println("✅ " + maes.size() + " mãe(s) carregada(s)");

            atualizarSelecaoAposCarregar(cardsContainerMaes);

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar mães: " + e.getMessage());
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
            System.out.println("✅ " + pais.size() + " pai(s) carregado(s)");

            atualizarSelecaoAposCarregar(cardsContainerPais);

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar pais: " + e.getMessage());
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
            System.out.println("✅ " + responsaveis.size() + " responsável(eis) carregado(s)");

            atualizarSelecaoAposCarregar(cardsContainerResponsaveis);

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar responsáveis: " + e.getMessage());
            limparContainerComMensagem(cardsContainerResponsaveis, "Erro ao carregar responsáveis");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === MÉTODO GENÉRICO PARA ATUALIZAR CONTAINER - VERSÃO CORRIGIDA ===
    private void atualizarCardsContainer(VBox container, List<Responsavel> responsaveis, String mensagemVazio) {
        container.getChildren().clear();

        if (responsaveis == null || responsaveis.isEmpty()) {
            EmptyCard vazio = new EmptyCard(mensagemVazio);
            container.getChildren().add(vazio);
        } else {
            for (Responsavel responsavel : responsaveis) {
                try {
                    // ✅ CORREÇÃO: Usar variável final para o lambda
                    final Responsavel responsavelFinal = responsavel;
                    ResponsavelCard card = new ResponsavelCard(responsavelFinal);

                    // ✅ CORREÇÃO: Configurar ações ANTES de adicionar ao container
                    configurarAcoesDoCard(card, container, responsavelFinal);

                    container.getChildren().add(card);

                } catch (Exception e) {
                    System.err.println("❌ Erro ao criar card: " + e.getMessage());
                    Label erroCard = new Label("Erro ao carregar");
                    erroCard.setStyle("-fx-background-color: #ffebee; -fx-border-color: #f44336; " +
                            "-fx-padding: 10; -fx-border-radius: 5;");
                    container.getChildren().add(erroCard);
                }
            }
        }
    }

    // Configurar ações do card
    private void configurarAcoesDoCard(ResponsavelCard card, VBox container, Responsavel responsavel) {
        card.setOnEditAction(() -> {
            System.out.println("🎯 SELECT ACTION - Selecionado: " + responsavel.getPessoa().getNome());

            // Desmarca outros cards no mesmo container
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
                System.out.println("👩 Mãe selecionada: " + responsavel.getPessoa().getNome());
            } else if (container == cardsContainerPais) {
                paiSelecionado = responsavel;
                System.out.println("👨 Pai selecionado: " + responsavel.getPessoa().getNome());
            } else if (container == cardsContainerResponsaveis) {
                responsavelSelecionado = responsavel;
                System.out.println("👤 Responsável selecionado: " + responsavel.getPessoa().getNome());
            }

            // 🔥 DETECTA IRMÃOS AUTOMATICAMENTE
            detectarIrmaosAutomaticamente();
        });

        card.setOnDeselectAction(() -> {
            System.out.println("❌ DESELECT ACTION - Deselecionado: " + responsavel.getPessoa().getNome());

            // Remove a seleção
            if (container == cardsContainerMaes) {
                maeSelecionada = null;
                System.out.println("👩 Mãe deselecionada");
            } else if (container == cardsContainerPais) {
                paiSelecionado = null;
                System.out.println("👨 Pai deselecionado");
            } else if (container == cardsContainerResponsaveis) {
                responsavelSelecionado = null;
                System.out.println("👤 Responsável deselecionado");
            }

            limparDetecaoIrmaos();
        });

        // ✅ Configurar ação de edição também (se necessário)
//        card.setOnEditAction(() -> {
//            System.out.println("✏️ EDIT ACTION - Editando: " + responsavel.getPessoa().getNome());
//            // Aqui você pode adicionar lógica de edição se quiser
//        });
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
                    System.out.println("✅ Responsável salvo com sucesso! Tipo: " + responsavelSalvo.getTipoResponsavel().getTipoResponsavel());
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
                System.out.println("✅ Membro familiar adicionado: " + dados.getNome());
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
                    System.out.println("✅ Pessoa autorizada adicionada: " + pessoaSalva.getPessoa().getNome());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao abrir cadastro de pessoa autorizada");
        }
    }

    // MÉTODOS PARA GERENCIAR SELEÇÃO DE RESPONSÁVEIS
    private void configurarSelecaoResponsaveis() {
        configurarSelecaoUnica(cardsContainerMaes);
        configurarSelecaoUnica(cardsContainerPais);
        configurarSelecaoUnica(cardsContainerResponsaveis);
    }

    // CONFIGURAR SELEÇÃO ÚNICA POR CONTAINER
    private void configurarSelecaoUnica(VBox container) {
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof ResponsavelCard) {
                ResponsavelCard card = (ResponsavelCard) node;

                card.setOnSelectAction(() -> {
                    for (javafx.scene.Node outroNode : container.getChildren()) {
                        if (outroNode instanceof ResponsavelCard && outroNode != node) {
                            ((ResponsavelCard) outroNode).setSelecionado(false);
                        }
                    }
                    detectarIrmaosAutomaticamente();
                });

                card.setOnDeselectAction(() -> {
                    limparDetecaoIrmaos();
                });
            }
        }
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
            configurarSelecaoUnica(container);
        });
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

    @FXML
    public void salvarMatricula() {
        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            if (!validarCamposObrigatorios()) {
                return;
            }

            if (!validarAuxilioComNIS()) {
                return;
            }

            em = DBConnection.getEntityManager();
            transaction = em.getTransaction();
            transaction.begin();

            // 1. Criar e salvar a Criança
            Crianca crianca = criarCrianca(em);
            em.persist(crianca);

            // 3. Criar e salvar Endereço
            Endereco endereco = criarEndereco();
            em.persist(endereco);

            // REMOVIDO: crianca.setEndereco(endereco); // Se não existe na entidade Crianca
            // Em vez disso, ajuste sua entidade Endereco para ter referência à Crianca se necessário

            // 4. Criar e salvar Matrícula
            Matricula matricula = criarMatricula(crianca);
            em.persist(matricula);

            // 5. Salvar Situação Habitacional
            SituacaoHabitacional situacaoHabitacional = criarSituacaoHabitacional(crianca);
            em.persist(situacaoHabitacional);

            // 6. Salvar Bens da Família
            salvarBensFamilia(em, crianca);

            // 7. Salvar Membros da Família
            salvarMembrosFamilia(em, crianca);

            // 8. Salvar Pessoas Autorizadas
            salvarPessoasAutorizadas(em, crianca);

            // 9. Salvar Composição Familiar
            salvarComposicaoFamiliar(em, crianca);

            transaction.commit();

            mostrarMensagem("Sucesso", "Matrícula cadastrada com sucesso!");
            limparFormulario();

            System.out.println("✅ Matrícula salva com ID: " + matricula.getId());

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Erro ao salvar matrícula: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao salvar matrícula: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void salvarBensFamilia(EntityManager em, Crianca crianca) {
        List<TipoBem> bensSelecionados = getBensSelecionados();

        if (bensSelecionados.isEmpty()) {
            System.out.println("ℹ️ Nenhum bem selecionado para salvar");
            return;
        }

        for (TipoBem tipoBem : bensSelecionados) {
            try {
                // Verificar se o tipoBem já está managed
                TipoBem managedTipoBem = em.find(TipoBem.class, tipoBem.getIdTipoBem());
                if (managedTipoBem == null) {
                    // Se não estiver managed, tentar fazer merge
                    managedTipoBem = em.merge(tipoBem);
                }

                // Criar e persistir o BensFamilia
                TipoBem bemFamilia = new TipoBem(crianca, managedTipoBem);
                em.persist(bemFamilia);

                System.out.println("✅ Bem salvo: " + tipoBem.getNomeBem() + " (ID: " + tipoBem.getIdTipoBem() + ")");

            } catch (Exception e) {
                System.err.println("❌ Erro ao salvar bem: " + tipoBem.getNomeBem() + " - " + e.getMessage());
            }
        }

        System.out.println("✅ " + bensSelecionados.size() + " bem(ns) da família salvos no banco");
    }

    // NOVO MÉTODO: Associar responsáveis à criança
    private void associarResponsaveisACrianca(EntityManager em, Crianca crianca) {
        if (maeSelecionada != null) {
            crianca.setMae(maeSelecionada.getPessoa());
            System.out.println("👩 Mãe associada: " + maeSelecionada.getPessoa().getNome());
        }

        if (paiSelecionado != null) {
            crianca.setPai(paiSelecionado.getPessoa());
            System.out.println("👨 Pai associado: " + paiSelecionado.getPessoa().getNome());
        }

        if (responsavelSelecionado != null) {
            crianca.setResponsavel(responsavelSelecionado);
            System.out.println("👤 Responsável associado: " + responsavelSelecionado.getPessoa().getNome());
        }
    }

    // NOVO MÉTODO: Validar se auxílio obrigatório tem NIS
    private boolean validarAuxilioComNIS() {
        String tipoAuxilio = comboTipoAuxilio.getValue();
        String nis = fieldNis.getText().trim();

        List<String> auxiliosQueExigemNIS = List.of(
                "Bolsa Família", "BPC", "Auxílio Brasil", "Auxílio Emergencial"
        );

        boolean temAuxilio = tipoAuxilio != null && !tipoAuxilio.equals("Nenhum");
        boolean auxilioExigeNIS = temAuxilio && auxiliosQueExigemNIS.contains(tipoAuxilio);

        if (auxilioExigeNIS && (nis.isEmpty() || nis.length() != 11)) {
            mostrarMensagem("Validação",
                    "O auxílio " + tipoAuxilio + " exige o preenchimento do NIS (11 dígitos)!");
            fieldNis.requestFocus();
            return false;
        }

        return true;
    }

    // MÉTODO CRIAR CRIANÇA COMPLETO
    private Crianca criarCrianca(EntityManager em) {
        Crianca crianca = new Crianca();

        // === DADOS BÁSICOS OBRIGATÓRIOS ===
        crianca.setNome(fieldNomeCrianca.getText().trim());

        if (datePickerNascimento.getValue() != null) {
            crianca.setDataNascimento(java.sql.Date.valueOf(datePickerNascimento.getValue()));
        } else {
            throw new IllegalArgumentException("Data de nascimento é obrigatória");
        }

        // === DOCUMENTOS ===
        crianca.setRG(fieldRgCrianca.getText().trim());
        crianca.setCPF(fieldCpfCrianca.getText().trim());
        crianca.setCertidaoNascimentoNum(fieldCertidaoNascimento.getText().trim());
        crianca.setMunicipioNascimento(fieldMunicipioNascimento.getText().trim());
        crianca.setCartorioRegistro(fieldCartorioRegistro.getText().trim());
        crianca.setMunicipioRegistro(fieldMunicipioRegistro.getText().trim());

        if (datePickerEmissaoRG.getValue() != null) {
            crianca.setDataEmissaoCertidao(java.sql.Date.valueOf(datePickerEmissaoRG.getValue()));
        }
        crianca.setOrgEmissorCertidao(fieldOrgaoEmissor.getText().trim());

        // === SEXO ===
        String sexoValue = comboSexo.getValue();
        if (sexoValue != null) {
            switch (sexoValue.toLowerCase()) {
                case "masculino":
                    crianca.setSexo(Crianca.Sexo.MASCULINO);
                    break;
                case "feminino":
                    crianca.setSexo(Crianca.Sexo.FEMININO);
                    break;
                default:
                    crianca.setSexo(Crianca.Sexo.OUTRO);
                    break;
            }
        } else {
            throw new IllegalArgumentException("Sexo é obrigatório");
        }

        // === COR/RAÇA ===
        String corRacaValue = comboCorRaca.getValue();
        if (corRacaValue != null) {
            switch (corRacaValue.toLowerCase()) {
                case "branca":
                    crianca.setCorRaca(Crianca.CorRaca.BRANCA);
                    break;
                case "preta":
                    crianca.setCorRaca(Crianca.CorRaca.PRETA);
                    break;
                case "parda":
                    crianca.setCorRaca(Crianca.CorRaca.PARDA);
                    break;
                case "amarela":
                    crianca.setCorRaca(Crianca.CorRaca.AMARELA);
                    break;
                case "indígena":
                    crianca.setCorRaca(Crianca.CorRaca.INDIGENA);
                    break;
                default:
                    crianca.setCorRaca(Crianca.CorRaca.OUTRO);
                    break;
            }
        }

        // === SAÚDE ===
        crianca.setCartSus(fieldSus.getText().trim());
        crianca.setUnidadeSaude(fieldUnidadeSaude.getText().trim());

        // === MOBILIDADE REDUZIDA ===
        String mobilidadeValue = comboMobilidadeReduzida.getValue();
        if (mobilidadeValue != null) {
            if (mobilidadeValue.contains("temporária")) {
                crianca.setMobRed(Crianca.MobRed.TEMPORARIA);
            } else if (mobilidadeValue.contains("permanente")) {
                crianca.setMobRed(Crianca.MobRed.PERMANENTE);
            } else {
                crianca.setMobRed(Crianca.MobRed.NENHUMA);
            }
        } else {
            crianca.setMobRed(Crianca.MobRed.NENHUMA);
        }

        // === EDUCAÇÃO ESPECIAL ===
        String educacaoEspecialValue = comboEducacaoEspecial.getValue();
        crianca.setEducEspecial("Sim".equals(educacaoEspecialValue));

        // === CLASSIFICAÇÃO ESPECIAL ===
        String classificacaoValue = comboClassificacaoEspecial.getValue();
        if (classificacaoValue != null && !classificacaoValue.equals("Nenhum")) {
            try {
                ClassificacaoEspecial classificacao = em.createQuery(
                        "SELECT c FROM ClassificacaoEspecial c WHERE c.classificacaoEspecial = :nome",
                        ClassificacaoEspecial.class
                ).setParameter("nome", classificacaoValue).getSingleResult();
                crianca.setClassificacaoEspecial(classificacao);
                crianca.setStatusClassificacaoEspecial(true);
            } catch (Exception e) {
                System.err.println("❌ Classificação especial não encontrada: " + classificacaoValue);
                crianca.setStatusClassificacaoEspecial(false);
            }
        } else {
            crianca.setStatusClassificacaoEspecial(false);
        }

        // === ALERGIAS ===
        String alergiaValue = comboAlergias.getValue();
        crianca.setAlergia(alergiaValue != null && !alergiaValue.equals("Nenhum"));

        // === IRMÃO GÊMEO ===
        boolean possuiIrmaoGemeo = checkIrmaoGemeo != null && checkIrmaoGemeo.isSelected();
        crianca.setPossuiIrmaoGemeo(possuiIrmaoGemeo);

        // === CAMPOS COM VALORES PADRÃO (PARA EVITAR NULL) ===
        crianca.setDefMulti(false);
        crianca.setPossuiIrmaoCreche(false);
        crianca.setRestricaoAlimentar(false);

        // REMOVIDO: crianca.setObservacoes(null); // Se não existe na entidade

        // Se existir algum campo de observações na sua entidade, use o nome correto
        // crianca.setObservacao(null); // ou whatever o nome correto

        // === AUXÍLIO GOVERNO ===
        String auxilioValue = comboTipoAuxilio.getValue();
        boolean temAuxilio = auxilioValue != null && !auxilioValue.equals("Nenhum");
        crianca.setResponsavelBeneficiarioAuxilioGov(temAuxilio);

        if (temAuxilio) {
            try {
                TipoAuxilio tipoAuxilio = em.createQuery(
                        "SELECT t FROM TipoAuxilio t WHERE t.nomeAuxilio = :nome",
                        TipoAuxilio.class
                ).setParameter("nome", auxilioValue).getSingleResult();
                crianca.setTipoAuxilio(tipoAuxilio);
            } catch (Exception e) {
                System.err.println("❌ Tipo de auxílio não encontrado: " + auxilioValue);
            }
        }

        // === NIS (SALVAR NO RESPONSÁVEL SE EXISTIR) ===
        String nisValue = fieldNis.getText().trim();
        if (!nisValue.isEmpty()) {
            // Se já temos um responsável selecionado, associar o NIS a ele
            if (responsavelSelecionado != null) {
                // Buscar o responsável managed
                Responsavel responsavelManaged = em.find(Responsavel.class, responsavelSelecionado.getId());
                if (responsavelManaged != null) {
                    responsavelManaged.setNumeroNis(nisValue);
                    crianca.setResponsavel(responsavelManaged);
                }
            } else if (maeSelecionada != null) {
                // Ou associar à mãe se for o caso
                Responsavel maeManaged = em.find(Responsavel.class, maeSelecionada.getId());
                if (maeManaged != null) {
                    maeManaged.setNumeroNis(nisValue);
                }
            }
        }

        // === ASSOCIAR RESPONSÁVEIS ===
        if (maeSelecionada != null) {
            try {
                Pessoa maeManaged = em.find(Pessoa.class, maeSelecionada.getPessoa().getId());
                crianca.setMae(maeManaged);
                System.out.println("👩 Mãe associada: " + maeManaged.getNome());
            } catch (Exception e) {
                System.err.println("❌ Erro ao associar mãe: " + e.getMessage());
            }
        }

        if (paiSelecionado != null) {
            try {
                Pessoa paiManaged = em.find(Pessoa.class, paiSelecionado.getPessoa().getId());
                crianca.setPai(paiManaged);
                System.out.println("👨 Pai associado: " + paiManaged.getNome());
            } catch (Exception e) {
                System.err.println("❌ Erro ao associar pai: " + e.getMessage());
            }
        }

        if (responsavelSelecionado != null) {
            try {
                Responsavel responsavelManaged = em.find(Responsavel.class, responsavelSelecionado.getId());
                crianca.setResponsavel(responsavelManaged);
                System.out.println("👤 Responsável associado: " + responsavelManaged.getPessoa().getNome());
            } catch (Exception e) {
                System.err.println("❌ Erro ao associar responsável: " + e.getMessage());
            }
        }

        // === VALIDAÇÃO FINAL DOS CAMPOS OBRIGATÓRIOS ===
        validarCamposCrianca(crianca);

        System.out.println("✅ Criança criada com sucesso: " + crianca.getNome());
        System.out.println("   📅 Nascimento: " + crianca.getDataNascimento());
        System.out.println("   👦 Sexo: " + crianca.getSexo());
        System.out.println("   🏠 Mãe: " + (crianca.getMae() != null ? crianca.getMae().getNome() : "Nenhuma"));
        System.out.println("   🏠 Pai: " + (crianca.getPai() != null ? crianca.getPai().getNome() : "Nenhum"));

        return crianca;
    }

    // ✅ MÉTODO PARA VALIDAR CAMPOS OBRIGATÓRIOS DA CRIANÇA
    private void validarCamposCrianca(Crianca crianca) {
        System.out.println("=== VALIDAÇÃO DOS CAMPOS DA CRIANÇA ===");

        // Campos NOT NULL que devem ser verificados
        if (crianca.getDefMulti() == null) {
            System.err.println("❌ DEF_MULTI é null - definindo como false");
            crianca.setDefMulti(false);
        }

        if (crianca.getPossuiIrmaoCreche() == null) {
            System.err.println("❌ POSSUI_IRMAO_CRECHE é null - definindo como false");
            crianca.setPossuiIrmaoCreche(false);
        }

        if (crianca.getRestricaoAlimentar() == null) {
            System.err.println("❌ RESTRICAO_ALIMENTAR é null - definindo como false");
            crianca.setRestricaoAlimentar(false);
        }

        if (crianca.getAlergia() == null) {
            System.err.println("❌ ALERGIA é null - definindo como false");
            crianca.setAlergia(false);
        }

        if (crianca.getEducEspecial() == null) {
            System.err.println("❌ EDUC_ESPECIAL é null - definindo como false");
            crianca.setEducEspecial(false);
        }

        if (crianca.getResponsavelBeneficiarioAuxilioGov() == null) {
            System.err.println("❌ RESPONSAVEL_BENEFICIARIO_AUXILIO_GOV é null - definindo como false");
            crianca.setResponsavelBeneficiarioAuxilioGov(false);
        }

        System.out.println("✅ Todos os campos obrigatórios validados");
    }

    // 🔥 MÉTODO CORRIGIDO: Criar situação habitacional com bens
    private SituacaoHabitacional criarSituacaoHabitacional(Crianca crianca) {
        SituacaoHabitacional situacao = new SituacaoHabitacional();
        situacao.setCrianca(crianca);

        // Tipo de moradia
        if (comboTipoMoradia.getValue() != null) {
            String tipoMoradia = comboTipoMoradia.getValue();
            // Mapear para os campos booleanos corretos
            situacao.setCasaPropria("Casa própria".equals(tipoMoradia));
            situacao.setCasaCedida("Casa cedida".equals(tipoMoradia));
            situacao.setCasaAlugada("Casa alugada".equals(tipoMoradia));
        }

        // Valor aluguel
        if (!fieldValorAluguel.getText().trim().isEmpty()) {
            try {
                situacao.setValorAluguel(new BigDecimal(fieldValorAluguel.getText().trim().replace(",", ".")));
            } catch (NumberFormatException e) {
                System.err.println("❌ Erro ao converter valor do aluguel");
            }
        }

        // Número de cômodos
        if (!fieldNumeroComodos.getText().trim().isEmpty()) {
            try {
                situacao.setNumeroComodos(Integer.parseInt(fieldNumeroComodos.getText().trim()));
            } catch (NumberFormatException e) {
                System.err.println("❌ Erro ao converter número de cômodos");
            }
        }

        // Características da moradia
        situacao.setTipoPiso(converterParaTipoPiso(comboTipoPiso.getValue()));
        situacao.setTipoMoradia(converterParaTipoMoradia(comboMaterialParede.getValue()));
        situacao.setTipoCobertura(converterParaTipoCobertura(comboTipoCobertura.getValue()));

        return situacao;
    }

    // MÉTODOS AUXILIARES PARA CONVERSÃO DE ENUMS
    private SituacaoHabitacional.TipoPiso converterParaTipoPiso(String valor) {
        if (valor == null) return null;

        switch (valor) {
            case "Cimento": return SituacaoHabitacional.TipoPiso.CIMENTO;
            case "Lajota": return SituacaoHabitacional.TipoPiso.LAJOTA;
            case "Chão batido": return SituacaoHabitacional.TipoPiso.CHAO_BATIDO;
            case "Cerâmica": return SituacaoHabitacional.TipoPiso.CERAMICA;
            case "Madeira": return SituacaoHabitacional.TipoPiso.MADEIRA;
            default: return SituacaoHabitacional.TipoPiso.OUTRO;
        }
    }

    private SituacaoHabitacional.TipoMoradia converterParaTipoMoradia(String valor) {
        if (valor == null) return null;

        switch (valor) {
            case "Tijolo": return SituacaoHabitacional.TipoMoradia.TIJOLO;
            case "Taipa": return SituacaoHabitacional.TipoMoradia.TAIPA;
            case "Madeira": return SituacaoHabitacional.TipoMoradia.MADEIRA;
            case "Mista": return SituacaoHabitacional.TipoMoradia.MISTA;
            case "Alvenaria": return SituacaoHabitacional.TipoMoradia.ALVENARIA;
            default: return SituacaoHabitacional.TipoMoradia.OUTRO;
        }
    }

    private SituacaoHabitacional.TipoCobertura converterParaTipoCobertura(String valor) {
        if (valor == null) return null;

        switch (valor) {
            case "Telha": return SituacaoHabitacional.TipoCobertura.TELHA;
            case "Zinco": return SituacaoHabitacional.TipoCobertura.ZINCO;
            case "Palha": return SituacaoHabitacional.TipoCobertura.PALHA;
            case "Laje": return SituacaoHabitacional.TipoCobertura.LAJE;
            default: return SituacaoHabitacional.TipoCobertura.OUTRO;
        }
    }

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();

        endereco.setLogradouro(fieldEndereco.getText().trim());
        endereco.setNumero(fieldNumero.getText().trim());
        endereco.setBairro(fieldBairro.getText().trim());
        endereco.setMunicipio(fieldMunicipio.getText().trim());
        endereco.setCep(fieldCEP.getText().trim());
        endereco.setUf(comboUF.getValue());
        endereco.setPontoReferencia(fieldPontoReferencia.getText().trim());
        endereco.setTelefoneResidencial(fieldTelefoneResidencial.getText().trim());
        endereco.setNumero(fieldTelefoneContato.getText().trim());

        return endereco;
    }

    private Matricula criarMatricula(Crianca crianca) {
        Matricula matricula = new Matricula();

        matricula.setCrianca(crianca);
        matricula.setDataMatricula(new java.sql.Date(System.currentTimeMillis()));
        matricula.setSerie(comboSerie.getValue());

        // Ano letivo
        try {
            matricula.setAnoLetivo(Integer.parseInt(fieldAnoLetivo.getText().trim()));
        } catch (NumberFormatException e) {
            matricula.setAnoLetivo(java.time.LocalDate.now().getYear());
        }

        matricula.setOrientacaoRecebida(false);
        matricula.setSituacaoMatricula(Matricula.SituacaoMatricula.ATIVA);

        // Data de vencimento (1 ano a partir de hoje)
        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.time.LocalDate vencimento = hoje.plusYears(1);
        matricula.setDataVencimento(java.sql.Date.valueOf(vencimento));

        return matricula;
    }

    private void salvarMembrosFamilia(EntityManager em, Crianca crianca) {
        for (MembroFamilia membro : membrosFamiliares) {
            MembroFamilia novoMembro = new MembroFamilia();
            novoMembro.setCrianca(crianca);
            novoMembro.setNome(membro.getNome());
            novoMembro.setIdade(membro.getIdade());
            novoMembro.setParentesco(membro.getParentesco());
            novoMembro.setSituacaoEscolar(membro.getSituacaoEscolar());
            novoMembro.setSituacaoEmprego(membro.getSituacaoEmprego());
            novoMembro.setRenda(membro.getRenda());

            em.persist(novoMembro);
        }

        System.out.println("✅ " + membrosFamiliares.size() + " membro(s) familiar(es) salvo(s)");
    }

    private void salvarPessoasAutorizadas(EntityManager em, Crianca crianca) {
        for (PessoaAutorizada pessoa : pessoaAutorizadas) {
            pessoa.setCrianca(crianca);

            if (pessoa.getId() != null) {
                em.merge(pessoa);
            } else {
                em.persist(pessoa);
            }
        }

        System.out.println("✅ " + pessoaAutorizadas.size() + " pessoa(s) autorizada(s) salva(s)");
    }

    private void salvarComposicaoFamiliar(EntityManager em, Crianca crianca) {
        ComposicaoFamiliar composicao = new ComposicaoFamiliar();
        composicao.setCrianca(crianca);

        BigDecimal rendaTotal = BigDecimal.ZERO;
        for (MembroFamilia membro : membrosFamiliares) {
            if (membro.getRenda() != null) {
                rendaTotal = rendaTotal.add(membro.getRenda());
            }
        }
        composicao.setRendaFamiliarTotal(rendaTotal);

        int totalMembros = membrosFamiliares.size() + 1;
        if (totalMembros > 0) {
            BigDecimal rendaPerCapita = rendaTotal.divide(
                    new BigDecimal(totalMembros), 2, java.math.RoundingMode.HALF_UP
            );
            composicao.setRendaPerCapita(rendaPerCapita);
        } else {
            composicao.setRendaPerCapita(BigDecimal.ZERO);
        }

        composicao.setTotalMembros(totalMembros);

        em.persist(composicao);
        System.out.println("✅ Composição familiar salva");
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

            System.out.println("✅ Membro familiar adicionado: " + dados.getNome() + ", Idade: " + dados.getIdade());

        } catch (Exception e) {
            System.err.println("❌ Erro ao adicionar membro familiar: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagem("Erro", "Erro ao adicionar membro familiar: " + e.getMessage());
        }
    }

    // Método para adicionar pessoa autorizada à tabela
    public void adicionarPessoaAutorizada(PessoaAutorizada pessoaAutorizada) {
        try {
            pessoaAutorizadas.add(pessoaAutorizada);
            tablePessoasAutorizadas.refresh();

            System.out.println("✅ Pessoa autorizada adicionada à tabela: " +
                    pessoaAutorizada.getPessoa().getNome());

        } catch (Exception e) {
            System.err.println("❌ Erro ao adicionar pessoa autorizada: " + e.getMessage());
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
            System.err.println("❌ Valor de escolaridade não encontrado: " + escolaridade);
            return MembroFamilia.SituacaoEscolar.NAO_INFORMADO;
        }
    }

    // Método para converter string para enum SituacaoEmprego
    private MembroFamilia.SituacaoEmprego converterParaSituacaoEmprego(String emprego) {
        if (emprego == null) return MembroFamilia.SituacaoEmprego.OUTRO;

        try {
            return MembroFamilia.SituacaoEmprego.valueOf(emprego);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Valor de emprego não encontrado: " + emprego);
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
            System.out.println("✅ " + irmao.getNome() + " marcado(a) como gêmeo(a)");
        });

        card.setOnGemeoDeselected(() -> {
            System.out.println("❌ " + irmao.getNome() + " desmarcado(a) como gêmeo(a)");
        });

        return card;
    }

    // MÉTODO PARA OBTER IRMÃO GÊMEO SELECIONADO
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

    // MÉTODO PARA LIMPAR SELEÇÃO DE GÊMEOS
    public void limparSelecaoGemeos() {
        checkIrmaoGemeo.setSelected(false);
        for (javafx.scene.Node node : containerIrmaos.getChildren()) {
            if (node instanceof IrmaoCard) {
                IrmaoCard card = (IrmaoCard) node;
                card.setSelecionado(false);
            }
        }
    }

    // MÉTODO PARA DETECTAR IRMÃOS AUTOMATICAMENTE - COM MELHOR DEBUG
    private void detectarIrmaosAutomaticamente() {
        System.out.println("🔍 INICIANDO DETECÇÃO DE IRMÃOS...");

        irmaosEncontrados.clear();
        containerIrmaos.getChildren().clear();

        // ✅ CORREÇÃO: Usar as variáveis de instância diretamente
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

            // ✅ CORREÇÃO: Usar IDs dos pais selecionados
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
                System.out.println("🔍 Buscando por mãe ID: " + maeAtual.getPessoa().getId());
            }
            if (paiAtual != null) {
                query.setParameter("idPai", paiAtual.getPessoa().getId());
                System.out.println("🔍 Buscando por pai ID: " + paiAtual.getPessoa().getId());
            }

            List<Crianca> irmaos = query.getResultList();

            if (!irmaos.isEmpty()) {
                irmaosEncontrados.addAll(irmaos);
                exibirIrmaosEncontrados();

                System.out.println("✅ " + irmaos.size() + " irmão(s) encontrado(s) com os mesmos pais");
                for (Crianca irmao : irmaos) {
                    System.out.println("   👶 " + irmao.getNome() + " - Nasc: " + irmao.getDataNascimento());
                }

            } else {
                EmptyCard emptyCard = new EmptyCard("Nenhum irmão encontrado com os pais selecionados.");
                containerIrmaos.getChildren().add(emptyCard);
                System.out.println("ℹ️ Nenhum irmão encontrado com os pais selecionados");
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao detectar irmãos: " + e.getMessage());
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
        // TODO: Fechar a tela ou voltar para lista
    }

    private void aplicarMascaraNis() {
        fieldNis.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                fieldNis.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 11) {
                fieldNis.setText(newValue.substring(0, 11));
            }
            validarNis();
        });
    }

    // 🔥 NOVO: Aplicar máscaras para outros campos
    private void aplicarMascaras() {
        // Máscara para CPF
        fieldCpfCrianca.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                fieldCpfCrianca.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 11) {
                fieldCpfCrianca.setText(newValue.substring(0, 11));
            }
        });

        // Máscara para CEP
        fieldCEP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                fieldCEP.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 8) {
                fieldCEP.setText(newValue.substring(0, 8));
            }
        });

        // Máscara para telefone
        fieldTelefoneContato.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                fieldTelefoneContato.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        fieldTelefoneResidencial.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                fieldTelefoneResidencial.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    // 🔥 NOVO: Configurar validações
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

    // Validação em tempo real do NIS
    private void validarNis() {
        String tipoAuxilio = comboTipoAuxilio.getValue();
        String nis = fieldNis.getText().trim();

        List<String> auxiliosQueExigemNIS = List.of(
                "Bolsa Família", "BPC", "Auxílio Brasil", "Auxílio Emergencial"
        );

        boolean auxilioExigeNIS = tipoAuxilio != null &&
                !tipoAuxilio.equals("Nenhum") &&
                auxiliosQueExigemNIS.contains(tipoAuxilio);

        if (auxilioExigeNIS) {
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