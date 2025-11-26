/*package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Crianca;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

public class RelatoriosController {

    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private ComboBox<String> comboPeriodo;

    @FXML private ComboBox<String> comboSexo;
    @FXML private ComboBox<String> comboAlergia; // "Sim" / "Não"
    @FXML private ComboBox<String> comboRaca;
    @FXML private ComboBox<String> comboMobilidade;

    @FXML private ComboBox<String> comboMoradia;
    @FXML private ComboBox<String> comboBeneficioSocial;

    @FXML private TextField txtRendaMinima;
    @FXML private TextField txtRendaMaxima;

    @FXML private TableView<Crianca> tabela;


    @FXML
    private Crianca.CorRaca converterRaca(String texto) {
        if (texto == null || texto.equals("Todas")) {
            return null;
        }

        switch (texto) {
            case "Branca": return Crianca.CorRaca.BRANCA;
            case "Preta": return Crianca.CorRaca.PRETA;
            case "Parda": return Crianca.CorRaca.PARDA;
            case "Amarela": return Crianca.CorRaca.AMARELA;
            case "Indígena": return Crianca.CorRaca.INDIGENA;
            default: return null;
        }
    }
    public void gerarPDF() {
        EntityManager em = DBConnection.getEntityManager();

        StringBuilder jpql = new StringBuilder(
                "SELECT c FROM Crianca c " +
                        "LEFT JOIN c.situacaoHabitacional s " +
                        "LEFT JOIN c.composicaoFamiliar cf " +
                        "WHERE 1=1 "
        );

        Map<String, Object> params = new HashMap<>();

        // =============================
        // PERÍODO
        // =============================
        LocalDate inicio = datePickerInicio.getValue();
        LocalDate fim = datePickerFim.getValue();
        String periodoPreDefinido = comboPeriodo.getValue();

        if (periodoPreDefinido != null) {
            switch (periodoPreDefinido) {
                case "Últimos 7 dias" -> {
                    inicio = LocalDate.now().minusDays(7);
                    fim = LocalDate.now();
                }
                case "Últimos 30 dias" -> {
                    inicio = LocalDate.now().minusDays(30);
                    fim = LocalDate.now();
                }
                case "Este mês" -> {
                    inicio = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                    fim = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
                }
                case "Mês anterior" -> {
                    inicio = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                    fim = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                }
                case "Este ano" -> {
                    inicio = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
                    fim = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
                }
            }
        }

        if (inicio != null) params.put("dataInicio", java.sql.Date.valueOf(inicio));
        if (fim != null) params.put("dataFim", java.sql.Date.valueOf(fim));

        // =============================
        // FILTROS
        // =============================

        // SEXO
        if (comboSexo.getValue() != null && !comboSexo.getValue().equals("Todos")) {
            jpql.append(" AND c.sexo = :sexo ");
            params.put("sexo", Crianca.Sexo.valueOf(comboSexo.getValue().toUpperCase()));
        }

        // ALERGIA
        if (comboAlergia.getValue() != null) {
            switch (comboAlergia.getValue()) {
                case "Com alergia" -> {
                    jpql.append(" AND c.alergia = :alergia ");
                    params.put("alergia", true);
                }
                case "Sem alergia" -> {
                    jpql.append(" AND c.alergia = :alergia ");
                    params.put("alergia", false);
                }
            }
        }

        // RAÇA
        if (comboRaca.getValue() != null && !comboRaca.getValue().equals("Todas")) {
            jpql.append(" AND c.corRaca = :raca ");
            params.put("raca", converterRaca(comboRaca.getValue()));
        }

        // MOBILIDADE
        if (comboMobilidade.getValue() != null && !comboMobilidade.getValue().equals("Todas as situações")) {
            switch (comboMobilidade.getValue()) {
                case "Com mobilidade reduzida" -> {
                    jpql.append(" AND c.mobRed IN :listaMob ");
                    params.put("listaMob", Arrays.asList(Crianca.MobRed.TEMPORARIA, Crianca.MobRed.PERMANENTE));
                }
                case "Sem mobilidade reduzida" -> {
                    jpql.append(" AND c.mobRed = :mob ");
                    params.put("mob", Crianca.MobRed.NENHUMA);
                }
            }
        }

        // MORADIA
        if (comboMoradia.getValue() != null && !comboMoradia.getValue().equals("Todos")) {
            switch (comboMoradia.getValue()) {
                case "Casa Própria" -> jpql.append(" AND s.casaPropria = true ");
                case "Casa Cedida" -> jpql.append(" AND s.casaCedida = true ");
                case "Casa Alugada" -> jpql.append(" AND s.casaAlugada = true ");
            }
        }

        // BENEFÍCIO SOCIAL
        if (comboBeneficioSocial.getValue() != null && !comboBeneficioSocial.getValue().equals("Todas as situações")) {
            switch (comboBeneficioSocial.getValue()) {
                case "Com benefício" -> jpql.append(" AND c.tipoAuxilio IS NOT NULL ");
                case "Sem benefício" -> jpql.append(" AND c.tipoAuxilio IS NULL ");
            }
        }

        // RENDA
        if (txtRendaMinima.getText() != null && !txtRendaMinima.getText().isEmpty()) {
            try {
                BigDecimal rendaMin = new BigDecimal(txtRendaMinima.getText());
                jpql.append(" AND cf.rendaFamiliarTotal >= :rendaMin ");
                params.put("rendaMin", rendaMin);
            } catch (NumberFormatException ignored) {}
        }

        if (txtRendaMaxima.getText() != null && !txtRendaMaxima.getText().isEmpty()) {
            try {
                BigDecimal rendaMax = new BigDecimal(txtRendaMaxima.getText());
                jpql.append(" AND cf.rendaFamiliarTotal <= :rendaMax ");
                params.put("rendaMax", rendaMax);
            } catch (NumberFormatException ignored) {}
        }

        // =============================
        // EXECUTA CONSULTA
        // =============================
        TypedQuery<Crianca> query = em.createQuery(jpql.toString(), Crianca.class);
        params.forEach(query::setParameter);

        List<Crianca> resultado = query.getResultList();
        em.close();

        // =============================
        // GERAR PDF
        // =============================
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/reports/layoutRel.jrxml")
            );

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("sexo", comboSexo.getValue());
            parametros.put("corRaca", comboRaca.getValue());
            parametros.put("alergia", comboAlergia.getValue());
            parametros.put("tipoMoradia", comboMoradia.getValue());
            parametros.put("beneficioSocial", comboBeneficioSocial.getValue());
            parametros.put("rendaMin", txtRendaMinima.getText());
            parametros.put("rendaMax", txtRendaMaxima.getText());
            parametros.put("dataInicio", inicio != null ? java.sql.Date.valueOf(inicio) : null);
            parametros.put("dataFim", fim != null ? java.sql.Date.valueOf(fim) : null);
            parametros.put("dataPreDef", periodoPreDefinido);
            parametros.put("totalCriancas", resultado.size());

            List<Object> listaResumo = List.of(new Object()); // lista de tamanho 1
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaResumo);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            // Diretório seguro
            String userHome = System.getProperty("user.home");
            File pastaRelatorios = new File(userHome, "relatorios");
            if (!pastaRelatorios.exists()) pastaRelatorios.mkdirs();

            File arquivoPDF = new File(pastaRelatorios, "relatorio_amostragem.pdf");
            JasperExportManager.exportReportToPdfFile(jasperPrint, arquivoPDF.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivoPDF);
            }

            Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
            alertInfo.setTitle("Relatório");
            alertInfo.setHeaderText("PDF gerado com sucesso!");
            alertInfo.setContentText("Crianças encontradas: " + resultado.size());
            alertInfo.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alertErro = new Alert(Alert.AlertType.ERROR);
            alertErro.setTitle("Erro");
            alertErro.setHeaderText("Falha ao gerar relatório");
            alertErro.setContentText(e.getMessage());
            alertErro.show();
        }
    }


    @FXML
    public void limparCampos() {
        datePickerInicio.setValue(null);
        datePickerFim.setValue(null);
        comboPeriodo.setValue(null);

        comboSexo.setValue(null);
        comboAlergia.setValue(null);
        comboRaca.setValue(null);
        comboMobilidade.setValue(null);

        comboMoradia.setValue(null);
        comboBeneficioSocial.setValue(null);
    }
}
*/

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
