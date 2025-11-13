package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.MatriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Matricula;
import com.salo.sistemacreche.entidades.Matricula.SituacaoMatricula;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RematriculaController {

    @FXML private TextField fieldPesquisarAluno;
    @FXML private VBox containerAlunos;

    @FXML
    public void initialize() {
        verificarMatriculasVencidas();
        carregarMatriculasVencidas();
        configurarListeners();
    }

    private void configurarListeners() {
        // Buscar automaticamente ao pressionar Enter no campo de pesquisa
        fieldPesquisarAluno.setOnAction(event -> buscarAlunos());
    }

    private void verificarMatriculasVencidas() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            if (em == null || !em.isOpen()) {
                System.err.println("❌ Erro de conexão com o banco");
                return;
            }

            em.getTransaction().begin();

            // Buscar todas as matrículas ATIVAS
            List<Matricula> matriculasAtivas = em.createQuery(
                            "SELECT m FROM Matricula m WHERE m.situacaoMatricula = :ativa", Matricula.class)
                    .setParameter("ativa", SituacaoMatricula.ATIVA)
                    .getResultList();

            int atualizadas = 0;
            for (Matricula matricula : matriculasAtivas) {
                if (matricula.getDataVencimento() != null &&
                        matricula.getDataVencimento().getYear() < LocalDate.now().getYear()) {

                    matricula.setSituacaoMatricula(SituacaoMatricula.VENCIDA);
                    em.merge(matricula);
                    atualizadas++;
                }
            }

            em.getTransaction().commit();
            System.out.println("✅ " + atualizadas + " matrícula(s) atualizada(s) para VENCIDA");

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erro ao verificar matrículas vencidas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @FXML
    public void buscarAlunos() {
        carregarMatriculasVencidasComFiltro();
    }

    private void carregarMatriculasVencidas() {
        carregarMatriculasVencidasComFiltro();
    }

    private void carregarMatriculasVencidasComFiltro() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            if (em == null || !em.isOpen()) {
                System.err.println("❌ Erro de conexão com o banco");
                mostrarMensagemErro("Erro de conexão com o banco de dados");
                return;
            }

            System.out.println("🔍 Buscando matrículas vencidas para rematrícula...");

            // Usando JPQL para buscar apenas matrículas VENCIDAS
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT m FROM Matricula m " +
                            "LEFT JOIN FETCH m.crianca c " +
                            "WHERE m.situacaoMatricula = :situacaoVencida"
            );

            List<Object> parametros = new ArrayList<>();
            int paramIndex = 1;

            // FILTRO: Pesquisa por texto (nome da criança)
            String termoPesquisa = fieldPesquisarAluno.getText();
            if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                jpql.append(" AND (LOWER(c.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(c.nomeMae) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(c.nomePai) LIKE ?").append(paramIndex);
                jpql.append(" OR CAST(m.id AS string) LIKE ?").append(paramIndex).append(")");
                parametros.add("%" + termoPesquisa.toLowerCase() + "%");
                paramIndex++;
            }

            // Ordenação por data de vencimento mais antiga primeiro
            jpql.append(" ORDER BY m.dataVencimento ASC");

            System.out.println("📝 JPQL: " + jpql.toString());

            // Criar e executar a query
            TypedQuery<Matricula> query = em.createQuery(jpql.toString(), Matricula.class);

            // Aplicar parâmetro fixo para situação VENCIDA
            query.setParameter("situacaoVencida", SituacaoMatricula.VENCIDA);

            // Aplicar parâmetros dinâmicos
            for (int i = 0; i < parametros.size(); i++) {
                query.setParameter(i + 1, parametros.get(i));
                System.out.println("📌 Parâmetro " + (i + 1) + ": " + parametros.get(i));
            }

            List<Matricula> matriculasVencidas = query.getResultList();
            System.out.println("✅ " + matriculasVencidas.size() + " matrícula(s) vencida(s) encontrada(s)");

            atualizarInterface(matriculasVencidas);

        } catch (Exception e) {
            String errorMsg = "💥 Erro ao buscar matrículas vencidas: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            mostrarMensagemErro("Erro ao buscar matrículas vencidas: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void atualizarInterface(List<Matricula> matriculas) {
        containerAlunos.getChildren().clear();

        if (matriculas.isEmpty()) {
            EmptyCard cardVazio = new EmptyCard("Nenhuma matrícula vencida encontrada");
            containerAlunos.getChildren().add(cardVazio);
        } else {
            for (Matricula matricula : matriculas) {
                MatriculaCard card = new MatriculaCard(matricula);
                // Configurar ação específica para rematrícula
                card.setOnEditAction(() -> realizarRematricula(matricula));
                containerAlunos.getChildren().add(card);
            }
        }
    }

    private void mostrarMensagemErro(String mensagem) {
        containerAlunos.getChildren().clear();
        EmptyCard cardErro = new EmptyCard(mensagem);
        containerAlunos.getChildren().add(cardErro);
    }

    private void realizarRematricula(Matricula matricula) {
        System.out.println("🔄 Iniciando rematrícula para: " +
                (matricula.getCrianca() != null ?
                        matricula.getCrianca().getNome() : "Matrícula " + matricula.getId()));

        // TODO: Implementar lógica de rematrícula
        // - Criar nova matrícula com dados da anterior
        // - Atualizar data de vencimento
        // - Registrar no histórico
        // - Fechar matrícula anterior

        mostrarMensagemSucesso("Rematrícula iniciada para: " +
                (matricula.getCrianca() != null ? matricula.getCrianca().getNome() : ""));
    }

    private void mostrarMensagemSucesso(String mensagem) {
        // TODO: Implementar alerta de sucesso
        System.out.println("✅ SUCESSO: " + mensagem);

        // Recarregar a lista após rematrícula
        carregarMatriculasVencidas();
    }
}