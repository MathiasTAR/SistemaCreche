package com.salo.sistemacreche.controller.extracadastro;

import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Pessoa;
import com.salo.sistemacreche.entidades.Responsavel;
import com.salo.sistemacreche.entidades.TipoResponsavel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class FiliacaoResponsavelController {

    @FXML private TextField fieldNome;
    @FXML private TextField fieldCpf;
    @FXML private TextField fieldRg;
    @FXML private TextField fieldCelular;
    @FXML private TextField fieldOutroContato;
    @FXML private TextField fieldTrabalho;
    @FXML private ComboBox<String> comboTipoResponsavel;

    private Stage dialogStage;
    private boolean salvo = false;
    private Responsavel responsavelSalvo;

    @FXML
    public void initialize() {
        aplicarMascaraTelefone();
        aplicarMascaraCPF();
        aplicarMascaraNome();
        carregarTiposResponsavel();
    }

    private void carregarTiposResponsavel() {
        comboTipoResponsavel.setItems(javafx.collections.FXCollections.observableArrayList(
                "Mãe", "Pai", "Responsável"
        ));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSalvo() {
        return salvo;
    }

    public Responsavel getResponsavelSalvo() {
        return responsavelSalvo;
    }

    @FXML
    private void btnSalvarResponsavel() {
        if (validarCampos()) {
            EntityManager em = null;
            EntityTransaction transaction = null;

            try {
                em = DBConnection.getEntityManager();
                transaction = em.getTransaction();
                transaction.begin();

                // 1. Verificar se já existe uma pessoa com este CPF
                Pessoa pessoaExistente = encontrarPessoaPorCPF(em, getCpfFormatadoParaBanco());

                Pessoa pessoa;
                if (pessoaExistente != null) {
                    // Usa a pessoa existente
                    pessoa = pessoaExistente;
                } else {
                    // Cria nova pessoa
                    pessoa = new Pessoa();
                    pessoa.setNome(getNomeFormatadoParaBanco());
                    pessoa.setCpf(getCpfFormatadoParaBanco().replaceAll("[^0-9]", ""));
                    pessoa.setRg(getRgFormatadoParaBanco());
                    pessoa.setTelefone(getCelularFormatadoParaBanco());
                    pessoa.setOutroContato(getOutroContatoFormatadoParaBanco());

                    em.persist(pessoa);
                }

                // 2. Verificar se já existe um responsável com este tipo para esta pessoa
                Long tipoId = getTipoResponsavelId();
                Responsavel responsavelExistente = encontrarResponsavelPorPessoaETipo(em, pessoa.getId(), tipoId);

                if (responsavelExistente != null) {
                    // Atualiza o responsável existente
                    responsavelExistente.setTelefone(getCelularFormatadoParaBanco());
                    responsavelExistente.setLocalTrabalho(fieldTrabalho.getText().trim());
                    responsavelExistente.setAuxilioGov(false);
                    responsavelExistente.setNumeroNis("");

                    responsavelSalvo = em.merge(responsavelExistente);
                } else {
                    // Cria novo responsável
                    Responsavel responsavel = new Responsavel();
                    responsavel.setPessoa(pessoa);
                    responsavel.setTelefone(getCelularFormatadoParaBanco());
                    responsavel.setLocalTrabalho(fieldTrabalho.getText().trim());
                    responsavel.setAuxilioGov(false);
                    responsavel.setNumeroNis("");

                    // Buscar TipoResponsavel baseado na seleção
                    TipoResponsavel tipoResponsavel = em.find(TipoResponsavel.class, tipoId);
                    if (tipoResponsavel != null) {
                        responsavel.setTipoResponsavel(tipoResponsavel);
                    } else {
                        throw new RuntimeException("Tipo de responsável não encontrado para ID: " + tipoId);
                    }

                    em.persist(responsavel);
                    responsavelSalvo = responsavel;
                }

                transaction.commit();

                this.salvo = true;
                fecharDialog();

            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                mostrarErro("Erro ao salvar responsável: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    // Método para encontrar pessoa por CPF
    private Pessoa encontrarPessoaPorCPF(EntityManager em, String cpf) {
        try {
            String cpfLimpo = cpf.replaceAll("[^0-9]", "");
            return em.createQuery(
                            "SELECT p FROM Pessoa p WHERE p.cpf = :cpf", Pessoa.class)
                    .setParameter("cpf", cpfLimpo)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Erro ao buscar pessoa por CPF: " + e.getMessage());
            return null;
        }
    }

    // Método para encontrar responsável por pessoa e tipo
    private Responsavel encontrarResponsavelPorPessoaETipo(EntityManager em, Long pessoaId, Long tipoId) {
        try {
            return em.createQuery(
                            "SELECT r FROM Responsavel r WHERE r.pessoa.id = :pessoaId AND r.tipoResponsavel.id = :tipoId",
                            Responsavel.class)
                    .setParameter("pessoaId", pessoaId)
                    .setParameter("tipoId", tipoId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Erro ao buscar responsável existente: " + e.getMessage());
            return null;
        }
    }

    // Método para obter ID do tipo de responsável baseado na seleção
    private Long getTipoResponsavelId() {
        String tipoSelecionado = comboTipoResponsavel.getValue();
        if (tipoSelecionado == null) {
            return 3L;
        }

        switch (tipoSelecionado) {
            case "Mãe":
                return 2L;
            case "Pai":
                return 1L;
            case "Responsável":
            default:
                return 3L;
        }
    }

    @FXML
    private void btnCancelarResponsavel() {
        this.salvo = false;
        this.responsavelSalvo = null;
        fecharDialog();
    }

    @FXML
    private void fecharDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private boolean validarCampos() {
        // === NOME ===
        if (fieldNome.getText().trim().isEmpty()) {
            mostrarErro("Nome é obrigatório!");
            fieldNome.requestFocus();
            return false;
        }
        if (!validarNome(fieldNome.getText())) {
            mostrarErro("Informe nome e sobrenome completos!\nEx: Maria Silva Santos");
            fieldNome.requestFocus();
            return false;
        }

        // === CPF ===
        if (fieldCpf.getText().trim().isEmpty()) {
            mostrarErro("CPF é obrigatório!");
            fieldCpf.requestFocus();
            return false;
        }
        if (!validarCPF(fieldCpf.getText())) {
            mostrarErro("CPF inválido!\nVerifique os números digitados.");
            fieldCpf.requestFocus();
            return false;
        }

        // === RG ===
        if (fieldRg.getText().trim().isEmpty()) {
            mostrarErro("RG é obrigatório!");
            fieldRg.requestFocus();
            return false;
        }
        if (!validarRG(fieldRg.getText())) {
            mostrarErro("RG inválido!\nDeve ter entre 8 e 12 dígitos.");
            fieldRg.requestFocus();
            return false;
        }

        // === CELULAR ===
        if (fieldCelular.getText().trim().isEmpty()) {
            mostrarErro("Celular é obrigatório!");
            fieldCelular.requestFocus();
            return false;
        }
        if (!validarTelefone(fieldCelular.getText())) {
            mostrarErro("Celular inválido!\nFormato: (11) 99999-9999");
            fieldCelular.requestFocus();
            return false;
        }

        // === TIPO RESPONSÁVEL ===
        if (comboTipoResponsavel.getValue() == null) {
            mostrarErro("Tipo de responsável é obrigatório!");
            comboTipoResponsavel.requestFocus();
            return false;
        }

        // === OUTRO CONTATO ===
        if (!fieldOutroContato.getText().trim().isEmpty() &&
                !validarTelefone(fieldOutroContato.getText())) {
            mostrarErro("Outro contato inválido!\nFormato: (98) 9999-9999");
            fieldOutroContato.requestFocus();
            return false;
        }

        return true;
    }

    // === VALIDAÇÃO DE CPF ===
    private boolean validarCPF(String cpf) {
        if (cpf == null) return false;

        // Remove tudo que não é número
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula os dígitos verificadores
        try {
            int[] digitos = new int[11];
            for (int i = 0; i < 11; i++) {
                digitos[i] = Integer.parseInt(cpf.substring(i, i + 1));
            }

            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += digitos[i] * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;

            if (digito1 != digitos[9]) {
                return false;
            }

            // Segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += digitos[i] * (11 - i);
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;

            return digito2 == digitos[10];

        } catch (NumberFormatException e) {
            return false;
        }
    }

    // === FORMATAÇÃO DE CPF ===
    private String formatarCPF(String cpf) {
        String numeros = cpf.replaceAll("[^0-9]", "");
        if (numeros.length() == 11) {
            return numeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf;
    }

    public String getCpfFormatadoParaBanco() {
        return formatarCPF(fieldCpf.getText());
    }

    // === VALIDAÇÃO DE RG ===
    private boolean validarRG(String rg) {
        if (rg == null || rg.trim().isEmpty()) return false;

        // Remove caracteres especiais, mantém números e X
        String rgLimpo = rg.replaceAll("[^0-9Xx]", "").toUpperCase();

        // RG geralmente tem entre 8 e 12 caracteres
        return rgLimpo.length() >= 8 && rgLimpo.length() <= 12;
    }

    // === FORMATAÇÃO DE RG ===
    private String formatarRG(String rg) {
        String rgLimpo = rg.replaceAll("[^0-9Xx]", "").toUpperCase();
        // Você pode adicionar formatação específica se quiser
        return rgLimpo;
    }

    public String getRgFormatadoParaBanco() {
        return formatarRG(fieldRg.getText());
    }

    // === VALIDAÇÃO DE NOME ===
    private boolean validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        // Remove espaços extras e verifica se tem pelo menos 2 palavras
        String nomeLimpo = nome.trim().replaceAll("\\s+", " ");
        String[] partes = nomeLimpo.split(" ");

        // Deve ter pelo menos nome e sobrenome
        if (partes.length < 2) {
            return false;
        }

        // Cada parte deve ter pelo menos 2 caracteres
        for (String parte : partes) {
            if (parte.length() < 2) {
                return false;
            }
        }

        // Verifica se tem apenas letras, espaços e acentos
        return nomeLimpo.matches("[a-zA-ZÀ-ÿ\\s]+");
    }

    // === FORMATAÇÃO DE NOME ===
    private String formatarNome(String nome) {
        if (nome == null) return "";

        // Remove espaços extras e formata cada palavra
        String nomeLimpo = nome.trim().replaceAll("\\s+", " ");
        String[] palavras = nomeLimpo.split(" ");
        StringBuilder nomeFormatado = new StringBuilder();

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                // Capitaliza: maria → Maria
                String capitalizada = palavra.substring(0, 1).toUpperCase() +
                        palavra.substring(1).toLowerCase();
                nomeFormatado.append(capitalizada).append(" ");
            }
        }

        return nomeFormatado.toString().trim();
    }

    public String getNomeFormatadoParaBanco() {
        return formatarNome(fieldNome.getText());
    }

    // === MÉTODO DE FORMATAÇÃO DE TELEFONE ===
    private String formatarTelefone(String telefone) {
        // Remove TUDO que não é número (parênteses, traços, espaços, etc)
        String numeros = telefone.replaceAll("[^0-9]", "");

        // Verifica quantos dígitos tem e formata adequadamente
        if (numeros.length() == 10) {
            // Formato para telefone fixo: (11) 9999-9999
            return "(" + numeros.substring(0, 2) + ") " +
                    numeros.substring(2, 6) + "-" +
                    numeros.substring(6);
        } else if (numeros.length() == 11) {
            // Formato para celular: (11) 99999-9999
            return "(" + numeros.substring(0, 2) + ") " +
                    numeros.substring(2, 7) + "-" +
                    numeros.substring(7);
        }

        // Se não tiver 10 ou 11 dígitos, retorna os números limpos
        return numeros;
    }

    // === MÉTODO DE VALIDAÇÃO DE TELEFONE ===
    private boolean validarTelefone(String telefone) {
        // Remove tudo que não é número e conta os dígitos
        String numeros = telefone.replaceAll("[^0-9]", "");

        // Telefone válido deve ter 10 ou 11 dígitos (com DDD)
        return numeros.length() == 10 || numeros.length() == 11;
    }

    // === MÉTODOS PARA PEGAR DADOS FORMATADOS PARA O BANCO ===
    public String getCelularFormatadoParaBanco() {
        return formatarTelefone(fieldCelular.getText());
    }

    public String getOutroContatoFormatadoParaBanco() {
        String outroContato = fieldOutroContato.getText().trim();
        return outroContato.isEmpty() ? null : formatarTelefone(outroContato);
    }

    // === MÉTODO PARA APLICAR MÁSCARA EM TEMPO REAL ===
    private void aplicarMascaraTelefone() {
        // Ouve as mudanças no campo celular
        fieldCelular.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.equals(oldValue)) {
                String formatado = formatarTelefone(newValue);
                if (!formatado.equals(newValue)) {
                    fieldCelular.setText(formatado);
                    // Coloca o cursor no final do texto
                    fieldCelular.positionCaret(formatado.length());
                }
            }
        });

        // Ouve as mudanças no campo outro contato
        fieldOutroContato.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.equals(oldValue)) {
                String formatado = formatarTelefone(newValue);
                if (!formatado.equals(newValue)) {
                    fieldOutroContato.setText(formatado);
                    fieldOutroContato.positionCaret(formatado.length());
                }
            }
        });
    }

    private void aplicarMascaraCPF() {
        fieldCpf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.equals(oldValue)) {
                String formatado = formatarCPF(newValue);
                if (!formatado.equals(newValue)) {
                    fieldCpf.setText(formatado);
                    fieldCpf.positionCaret(formatado.length());
                }
            }
        });
    }

    private void aplicarMascaraNome() {
        fieldNome.textProperty().addListener((observable, oldValue, newValue) -> {
            // Apenas permite letras e espaços
            if (!newValue.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                fieldNome.setText(oldValue);
            }
        });
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Getters para os dados (opcionais - mantidos para compatibilidade)
    public String getNome() { return fieldNome.getText(); }
    public String getCpf() { return fieldCpf.getText(); }
    public String getRg() { return fieldRg.getText(); }
    public String getCelular() { return fieldCelular.getText(); }
    public String getOutroContato() { return fieldOutroContato.getText(); }
    public String getTrabalho() { return fieldTrabalho.getText(); }
    public String getTipoResponsavel() { return comboTipoResponsavel.getValue(); }

    // Método para obter todos os dados como objeto
    public DadosResponsavel getDadosResponsavel() {
        return new DadosResponsavel(
                getNome(),
                getCpf(),
                getRg(),
                getCelular(),
                getOutroContato(),
                getTrabalho(),
                getTipoResponsavel()
        );
    }

    // Classe interna para transportar dados
    public static class DadosResponsavel {
        private String nome;
        private String cpf;
        private String rg;
        private String celular;
        private String outroContato;
        private String trabalho;
        private String tipoResponsavel;

        public DadosResponsavel(String nome, String cpf, String rg, String celular,
                                String outroContato, String trabalho, String tipoResponsavel) {
            this.nome = nome;
            this.cpf = cpf;
            this.rg = rg;
            this.celular = celular;
            this.outroContato = outroContato;
            this.trabalho = trabalho;
            this.tipoResponsavel = tipoResponsavel;
        }

        // Getters
        public String getNome() { return nome; }
        public String getCpf() { return cpf; }
        public String getRg() { return rg; }
        public String getCelular() { return celular; }
        public String getOutroContato() { return outroContato; }
        public String getTrabalho() { return trabalho; }
        public String getTipoResponsavel() { return tipoResponsavel; }
    }
}