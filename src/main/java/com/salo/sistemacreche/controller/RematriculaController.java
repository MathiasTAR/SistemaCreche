package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.RematriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Matricula;
import com.salo.sistemacreche.entidades.Matricula.SituacaoMatricula;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Date;
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
                System.err.println("Erro de conexão com o banco");
                return;
            }

            em.getTransaction().begin();

            // UPDATE EM LOTE
            int atualizadas = em.createQuery(
                            "UPDATE Matricula m SET m.situacaoMatricula = :vencida " +
                                    "WHERE m.situacaoMatricula = :ativa AND m.dataVencimento < :hoje")
                    .setParameter("vencida", SituacaoMatricula.VENCIDA)
                    .setParameter("ativa", SituacaoMatricula.ATIVA)
                    .setParameter("hoje", java.sql.Date.valueOf(LocalDate.now()))
                    .executeUpdate();

            em.getTransaction().commit();

            if (atualizadas > 0) {
                System.out.println(atualizadas + " matrícula(s) atualizada(s) para VENCIDA");
            } else {
                System.out.println("Nenhuma matrícula vencida encontrada");
            }

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao verificar matrículas vencidas: " + e.getMessage());
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
                mostrarMensagemErro("Erro de conexão com o banco de dados");
                return;
            }

            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT m FROM Matricula m " +
                            "LEFT JOIN FETCH m.crianca c " +
                            "LEFT JOIN FETCH c.mae mae " +
                            "LEFT JOIN FETCH c.pai pai " +
                            "WHERE m.situacaoMatricula = :situacaoVencida " +
                            "AND m.dataVencimento < CURRENT_DATE" // Só matrículas que realmente venceram
            );

            List<Object> parametros = new ArrayList<>();
            int paramIndex = 1;

            // FILTRO: Pesquisa por texto (nome da criança)
            String termoPesquisa = fieldPesquisarAluno.getText();
            if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                jpql.append(" AND (LOWER(c.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(mae.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(pai.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR CAST(m.id AS string) LIKE ?").append(paramIndex).append(")");
                parametros.add("%" + termoPesquisa.toLowerCase() + "%");
                paramIndex++;
            }

            // Ordenação por data de vencimento mais antiga primeiro
            jpql.append(" ORDER BY m.dataVencimento ASC");

            // Criar e executar a query
            TypedQuery<Matricula> query = em.createQuery(jpql.toString(), Matricula.class);

            // Aplicar parâmetro fixo para situação VENCIDA
            query.setParameter("situacaoVencida", SituacaoMatricula.VENCIDA);

            // Aplicar parâmetros dinâmicos
            for (int i = 0; i < parametros.size(); i++) {
                query.setParameter(i + 1, parametros.get(i));
            }

            List<Matricula> matriculasVencidas = query.getResultList();

            atualizarInterface(matriculasVencidas);

        } catch (Exception e) {
            String errorMsg = "Erro ao buscar matrículas vencidas: " + e.getMessage();
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
                RematriculaCard card = new RematriculaCard(matricula);

                // Configurar ações específicas para rematrícula
                card.setOnRenovarAction(() -> renovarMatricula(matricula));
                card.setOnVisualizarAction(() -> visualizarMatricula(matricula));

                containerAlunos.getChildren().add(card);
            }
        }
    }

    private void renovarMatricula(Matricula matricula) {

        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();

            // Buscar a matrícula gerenciada
            Matricula matriculaManaged = em.find(Matricula.class, matricula.getId());

            // RENOVAR A MATRÍCULA
            renovarDadosMatricula(matriculaManaged);

            em.merge(matriculaManaged);
            em.getTransaction().commit();
            em.close();

            mostrarMensagemSucesso("Matrícula renovada para: " +
                    (matricula.getCrianca() != null ? matricula.getCrianca().getNome() : ""));

            buscarAlunos(); // Recarrega a lista

        } catch (Exception e) {
            System.err.println("Erro ao renovar matrícula: " + e.getMessage());
            e.printStackTrace();
            mostrarMensagemErro("Erro ao renovar matrícula: " + e.getMessage());
        }
    }

    // Renovar dados da matrícula
    private void renovarDadosMatricula(Matricula matricula) {
        // Atualizar situação para ATIVA
        matricula.setSituacaoMatricula(Matricula.SituacaoMatricula.ATIVA);

        // Limpar data de desligamento (se houver)
        matricula.setDataDesligamento(null);

        // Calcular nova data de vencimento (1 ano a partir de hoje)
        LocalDate hoje = LocalDate.now();
        matricula.setDataMatricula(Date.valueOf(hoje));
        LocalDate novaDataVencimento = hoje.plusYears(1);
        matricula.setDataVencimento(java.sql.Date.valueOf(novaDataVencimento));

        // Avançar para a próxima série (se aplicável)
        avancarSerie(matricula);

        // Incrementar ano letivo
        if (matricula.getAnoLetivo() != null) {
            matricula.setAnoLetivo(matricula.getAnoLetivo() + 1);
        } else {
            matricula.setAnoLetivo(LocalDate.now().getYear());
        }
    }

    // Avançar para próxima série
    private void avancarSerie(Matricula matricula) {
        if (matricula.getSerie() == null) return;

        String serieAtual = matricula.getSerie().toUpperCase();

        switch (serieAtual) {
            case "BERCARIO_I":
                matricula.setSerie("BERCARIO_II");
                break;
            case "BERCARIO_II":
                matricula.setSerie("MATERNAL_I");
                break;
            case "MATERNAL_I":
                matricula.setSerie("MATERNAL_II");
                break;
            case "MATERNAL_II":
                matricula.setSerie("PRE_I");
                break;
            case "PRE_I":
                matricula.setSerie("PRE_II");
                break;
            case "PRE_II":
                // Última série - manter Pré II
                System.out.println("Aluno na última série (Pré II) - mantendo mesma série");
                break;
            default:
                System.out.println("ℹSérie não reconhecida: " + serieAtual + " - mantendo mesma série");
        }
    }

    private void visualizarMatricula(Matricula matricula) {
        System.out.println("👁Visualizando matrícula: " +
                (matricula.getCrianca() != null ?
                        matricula.getCrianca().getNome() : "Matrícula " + matricula.getId()));
    }

    private void mostrarMensagemErro(String mensagem) {
        containerAlunos.getChildren().clear();
        EmptyCard cardErro = new EmptyCard(mensagem);
        containerAlunos.getChildren().add(cardErro);
    }

    private void mostrarMensagemSucesso(String mensagem) {
        // Recarregar a lista após rematrícula
        carregarMatriculasVencidas();
    }
}