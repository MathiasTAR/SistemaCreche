package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.PreMatriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Matricula;
import com.salo.sistemacreche.entidades.PreMatricula;
import com.salo.sistemacreche.entidades.PreMatricula.SituacaoPreMatricula;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListaPreMatriculasController {

    @FXML private TextField fieldPesquisarPreMatricula;
    @FXML private ComboBox<String> comboSituacao;
    @FXML private VBox cardsContainer;

    @FXML
    public void initialize() {
        configurarComboBox();
        carregarPreMatriculas();
        configurarListeners();
    }

    private void configurarComboBox() {
        comboSituacao.getItems().addAll("Todas", "Em Analise", "Aprovada", "Reprovada", "Cancelada");
        comboSituacao.setValue("Todas");
    }

    private void configurarListeners() {
        // Buscar automaticamente ao pressionar Enter no campo de pesquisa
        fieldPesquisarPreMatricula.setOnAction(event -> buscarPreMatriculas());

        // Buscar automaticamente ao alterar a situação
        comboSituacao.setOnAction(event -> buscarPreMatriculas());
    }

    private SituacaoPreMatricula converterStringParaSituacaoEnum(String situacao) {
        switch(situacao) {
            case "Aprovada": return SituacaoPreMatricula.APROVADA;
            case "Em Analise": return SituacaoPreMatricula.EM_ANALISE;
            case "Cancelada": return SituacaoPreMatricula.CANCELADA;
            case "Reprovada": return SituacaoPreMatricula.REPROVADA;
            default: return null; // Para "Todas"
        }
    }

    @FXML
    public void buscarPreMatriculas() {
        carregarPreMatriculasComFiltro();
    }

    @FXML
    public void limparFiltros() {
        fieldPesquisarPreMatricula.clear();
        comboSituacao.setValue("Todas");
        carregarPreMatriculas();
    }

    private void carregarPreMatriculas() {
        carregarPreMatriculasComFiltro();
    }

    private void carregarPreMatriculasComFiltro() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            if (em == null || !em.isOpen()) {
                System.err.println("Erro de conexão com o banco");
                mostrarMensagemErro("Erro de conexão com o banco de dados");
                return;
            }

            // CORREÇÃO: Query JPQL corrigida
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT pm FROM PreMatricula pm " +
                            "LEFT JOIN FETCH pm.crianca c " +
                            "LEFT JOIN FETCH c.mae mae " +
                            "LEFT JOIN FETCH c.pai pai " +
                            "LEFT JOIN FETCH pm.situacaoHabitacional sh " +
                            "WHERE 1 = 1"
            );

            List<Object> parametros = new ArrayList<>();
            int paramIndex = 1;

            // FILTRO 1: Pesquisa por texto (nome da criança, mãe, pai ou ID)
            String termoPesquisa = fieldPesquisarPreMatricula.getText();
            if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                jpql.append(" AND (LOWER(c.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(mae.nome) LIKE ?").append(paramIndex); // CORREÇÃO: mae.nome
                jpql.append(" OR LOWER(pai.nome) LIKE ?").append(paramIndex); // CORREÇÃO: pai.nome
                jpql.append(" OR CAST(pm.id AS string) LIKE ?").append(paramIndex).append(")");
                parametros.add("%" + termoPesquisa.toLowerCase() + "%");
                paramIndex++;
            }

            // FILTRO 2: Situação da pré-matrícula
            String situacaoSelecionada = comboSituacao.getValue();
            if (situacaoSelecionada != null && !situacaoSelecionada.equals("Todas")) {
                SituacaoPreMatricula situacaoEnum = converterStringParaSituacaoEnum(situacaoSelecionada);
                if (situacaoEnum != null) {
                    jpql.append(" AND pm.situacaoPreMatricula = ?").append(paramIndex);
                    parametros.add(situacaoEnum);
                    paramIndex++;
                }
            }

            // Ordenação por data de pré-matrícula mais recente primeiro
            jpql.append(" ORDER BY pm.dataPreMatricula DESC");

            // Criar e executar a query
            TypedQuery<PreMatricula> query = em.createQuery(jpql.toString(), PreMatricula.class);

            // Aplicar parâmetros
            for (int i = 0; i < parametros.size(); i++) {
                query.setParameter(i + 1, parametros.get(i));
            }

            List<PreMatricula> preMatriculas = query.getResultList();

            atualizarInterface(preMatriculas);

        } catch (Exception e) {
            String errorMsg = "Erro ao buscar pré-matrículas: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            mostrarMensagemErro("Erro ao buscar pré-matrículas: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void atualizarInterface(List<PreMatricula> preMatriculas) {
        cardsContainer.getChildren().clear();

        if (preMatriculas.isEmpty()) {
            EmptyCard cardVazio = new EmptyCard("Nenhuma pré-matrícula encontrada");
            cardsContainer.getChildren().add(cardVazio);
        } else {
            for (PreMatricula preMatricula : preMatriculas) {
                PreMatriculaCard card = new PreMatriculaCard(preMatricula);
                card.setOnAprovarAction(() -> aprovarPreMatricula(preMatricula));
                card.setOnReprovarAction(() -> reprovarPreMatricula(preMatricula));
                card.setOnCancelarAction(() -> cancelarPreMatricula(preMatricula));
                cardsContainer.getChildren().add(card);
            }
        }
    }

    private void mostrarMensagemErro(String mensagem) {
        cardsContainer.getChildren().clear();
        EmptyCard cardErro = new EmptyCard(mensagem);
        cardsContainer.getChildren().add(cardErro);
    }

    private void editarPreMatricula(PreMatricula preMatricula) {
        System.out.println("📝 Editando pré-matrícula: " + preMatricula.getId());
    }

    private void aprovarPreMatricula(PreMatricula preMatricula) {
        System.out.println("✅ Aprovando pré-matrícula: " + preMatricula.getId());

        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();

            // Buscar a pré-matrícula gerenciada
            PreMatricula preMatriculaManaged = em.find(PreMatricula.class, preMatricula.getId());
            preMatriculaManaged.setSituacaoPreMatricula(SituacaoPreMatricula.APROVADA);

            // CRIAR MATRÍCULA AUTOMATICAMENTE
            Matricula novaMatricula = criarMatriculaAPartirPreMatricula(preMatriculaManaged);
            em.persist(novaMatricula);

            em.merge(preMatriculaManaged);
            em.getTransaction().commit();
            em.close();

            buscarPreMatriculas(); // Recarrega a lista

        } catch (Exception e) {
            System.err.println("Erro ao aprovar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagemErro("Erro ao aprovar pré-matrícula: " + e.getMessage());
        }
    }

    // Método para criar matrícula a partir da pré-matrícula
    private Matricula criarMatriculaAPartirPreMatricula(PreMatricula preMatricula) {
        Matricula matricula = new Matricula();

        // Dados básicos
        matricula.setCrianca(preMatricula.getCrianca());
        matricula.setPreMatricula(preMatricula);
        matricula.setDataMatricula(new java.sql.Date(System.currentTimeMillis()));

        // Série e ano letivo (usar os dados temporários da pré-matrícula)
        if (preMatricula.getSerieTemp() != null && !preMatricula.getSerieTemp().isEmpty()) {
            matricula.setSerie(preMatricula.getSerieTemp());
        }

        if (preMatricula.getAnoLetivoTemp() != null) {
            matricula.setAnoLetivo(preMatricula.getAnoLetivoTemp());
        } else {
            // Usar ano atual como fallback
            matricula.setAnoLetivo(LocalDate.now().getYear());
        }

        // Situação inicial
        matricula.setSituacaoMatricula(Matricula.SituacaoMatricula.ATIVA);

        // Calcular data de vencimento (1 ano a partir de agora)
        LocalDate hoje = LocalDate.now();
        LocalDate vencimento = hoje.plusYears(1);
        matricula.setDataVencimento(java.sql.Date.valueOf(vencimento));

        // Outros campos
        matricula.setOrientacaoRecebida(false);

        return matricula;
    }

    private void reprovarPreMatricula(PreMatricula preMatricula) {

        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();

            // Buscar a pré-matrícula gerenciada
            PreMatricula preMatriculaManaged = em.find(PreMatricula.class, preMatricula.getId());
            preMatriculaManaged.setSituacaoPreMatricula(SituacaoPreMatricula.REPROVADA);

            em.merge(preMatriculaManaged);
            em.getTransaction().commit();
            em.close();

            buscarPreMatriculas(); // Recarrega a lista
        } catch (Exception e) {
            System.err.println("Erro ao reprovar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagemErro("Erro ao reprovar pré-matrícula: " + e.getMessage());
        }
    }

    private void cancelarPreMatricula(PreMatricula preMatricula) {

        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();

            // Buscar a pré-matrícula gerenciada
            PreMatricula preMatriculaManaged = em.find(PreMatricula.class, preMatricula.getId());
            preMatriculaManaged.setSituacaoPreMatricula(SituacaoPreMatricula.CANCELADA);

            em.merge(preMatriculaManaged);
            em.getTransaction().commit();
            em.close();

            buscarPreMatriculas(); // Recarrega a lista
        } catch (Exception e) {
            System.err.println("Erro ao cancelar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagemErro("Erro ao cancelar pré-matrícula: " + e.getMessage());
        }
    }
}