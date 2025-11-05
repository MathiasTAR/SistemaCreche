package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.MatriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Matricula;
import com.salo.sistemacreche.entidades.Matricula.SituacaoMatricula;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListaMatriculasController {

    @FXML private Label welcomeText;
    @FXML private TextField fieldPesquisarMatricula;
    @FXML private ComboBox<String> comboSituacao;
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private VBox cardsContainer;

    @FXML
    public void initialize() {
        configurarComboBox();
        carregarMatriculas();
    }

    private void configurarComboBox() {
        // CORREÇÃO: Use os valores exatos que correspondem ao Enum
        comboSituacao.getItems().addAll("Todas", "Matriculado", "Concluída", "Cancelada");
        comboSituacao.setValue("Todas");
    }

    private SituacaoMatricula converterStringParaSituacaoEnum(String situacao) {
        switch(situacao) {
            case "Concluída": return SituacaoMatricula.CONCLUIDA;
            case "Cancelada": return SituacaoMatricula.CANCELADA;
            default: return SituacaoMatricula.ATIVA;
        }
    }

    @FXML
    public void buscarMatriculas() {
        carregarMatriculasComFiltro();
    }

    @FXML
    public void limparFiltros() {
        fieldPesquisarMatricula.clear();
        comboSituacao.setValue("Todas");
        datePickerInicio.setValue(null);
        datePickerFim.setValue(null);
        carregarMatriculas();
    }

    private void carregarMatriculas() {
        carregarMatriculasComFiltro();
    }

    private void carregarMatriculasComFiltro() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            if (em == null) {
                welcomeText.setText("❌ Erro de conexão com o banco");
                return;
            }

            if (!em.isOpen()) {
                welcomeText.setText("❌ EntityManager está fechado");
                return;
            }

            System.out.println("🔍 Executando busca com filtros...");
            System.out.println("📊 Situação selecionada: " + comboSituacao.getValue());

            // Usando JPQL para maior simplicidade e controle
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT m FROM Matricula m " +
                            "LEFT JOIN FETCH m.crianca c " +
                            "WHERE 1 = 1"
            );

            List<Object> parametros = new ArrayList<>();
            int paramIndex = 1;

            // FILTRO 1: Pesquisa por texto
            String termoPesquisa = fieldPesquisarMatricula.getText();
            if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                jpql.append(" AND (LOWER(c.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(c.nomeMae) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(c.nomePai) LIKE ?").append(paramIndex);
                jpql.append(" OR CAST(m.id AS string) LIKE ?").append(paramIndex).append(")");
                parametros.add("%" + termoPesquisa.toLowerCase() + "%");
                paramIndex++;
            }

            // FILTRO 2: Situação da matrícula - CORRIGIDO
            String situacaoSelecionada = comboSituacao.getValue();
            if (situacaoSelecionada != null && !situacaoSelecionada.equals("Todas")) {
                SituacaoMatricula situacaoEnum = converterStringParaSituacaoEnum(situacaoSelecionada);
                jpql.append(" AND m.situacaoMatricula = ?").append(paramIndex);
                parametros.add(situacaoEnum);
                paramIndex++;
            }

            // FILTRO 3: Data de início
            if (datePickerInicio.getValue() != null) {
                Date dataInicio = Date.from(datePickerInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                jpql.append(" AND m.dataMatricula >= ?").append(paramIndex);
                parametros.add(dataInicio);
                paramIndex++;
            }

            // FILTRO 4: Data de fim
            if (datePickerFim.getValue() != null) {
                LocalDate dataFimLocal = datePickerFim.getValue().plusDays(1);
                Date dataFim = Date.from(dataFimLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
                jpql.append(" AND m.dataMatricula < ?").append(paramIndex);
                parametros.add(dataFim);
                paramIndex++;
            }

            // Ordenação
            jpql.append(" ORDER BY m.dataMatricula DESC");

            System.out.println("📝 JPQL: " + jpql.toString());

            // Criar e executar a query
            TypedQuery<Matricula> query = em.createQuery(jpql.toString(), Matricula.class);

            // Aplicar parâmetros
            for (int i = 0; i < parametros.size(); i++) {
                query.setParameter(i + 1, parametros.get(i));
                System.out.println("📌 Parâmetro " + (i + 1) + ": " + parametros.get(i) + " (Tipo: " + parametros.get(i).getClass().getSimpleName() + ")");
            }

            List<Matricula> matriculas = query.getResultList();
            System.out.println("✅ " + matriculas.size() + " matrícula(s) encontrada(s)");

            atualizarInterface(matriculas);

        } catch (Exception e) {
            String errorMsg = "💥 Erro ao buscar matrículas: " + e.getMessage();
            welcomeText.setText(errorMsg);
            System.err.println(errorMsg);
            e.printStackTrace();

            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }
        }
    }

    private void atualizarInterface(List<Matricula> matriculas) {
        cardsContainer.getChildren().clear();

        if (matriculas.isEmpty()) {
            welcomeText.setText("Nenhuma matrícula encontrada com os filtros aplicados");
            EmptyCard cardVazio = new EmptyCard("Nenhuma matrícula encontrada");
            cardsContainer.getChildren().add(cardVazio);
        } else {
            welcomeText.setText("✅ " + matriculas.size() + " matrícula(s) encontrada(s)");

            for (Matricula matricula : matriculas) {
                MatriculaCard card = new MatriculaCard(matricula);
                card.setOnEditAction(() -> editarMatricula(matricula));
                cardsContainer.getChildren().add(card);
            }
        }
    }

    private void editarMatricula(Matricula matricula) {
        System.out.println("📝 Editando matrícula: " + matricula.getId());
        welcomeText.setText("Editando matrícula: " +
                (matricula.getCrianca() != null ? matricula.getCrianca().getNome() : matricula.getId()));
    }
}