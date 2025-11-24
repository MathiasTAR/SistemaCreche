package com.salo.sistemacreche.service;

import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class CadastroMatriculaService {

    /**
     * Salva toda a pré-matrícula em uma transação unificada
     */
    public PreMatricula salvarPreMatriculaCompleta(
            // Dados da Criança
            String nomeCrianca,
            LocalDate dataNascimento,
            String rgCrianca,
            String cpfCrianca,
            String certidaoNascimento,
            String municipioNascimento,
            String cartorioRegistro,
            String municipioRegistro,
            LocalDate dataEmissaoRG,
            String orgaoEmissor,
            String sexo,
            String corRaca,
            String cartSus,
            String unidadeSaude,
            String mobilidadeReduzida,
            Boolean educacaoEspecial,
            String classificacaoEspecial,
            String alergia,
            Boolean possuiIrmaoGemeo,
            String tipoAuxilio,
            String nis,
            Pessoa mae,
            Pessoa pai,
            Responsavel responsavel,

            // Dados do Endereço
            String logradouro,
            String numero,
            String bairro,
            String municipio,
            String cep,
            String uf,
            String pontoReferencia,
            String telefoneResidencial,
            String telefoneContato,

            // Dados da Situação Habitacional
            String tipoMoradia,
            String valorAluguel,
            String numeroComodos,
            String tipoPiso,
            String materialParede,
            String tipoCobertura,
            Boolean fossa,
            Boolean cifon,
            Boolean energiaEletrica,
            Boolean aguaEncanada,

            // Listas
            List<TipoBem> bensSelecionados,
            List<MembroFamilia> membrosFamiliares,
            List<PessoaAutorizada> pessoasAutorizadas
    ) {
        Objects.requireNonNull(nomeCrianca, "Nome da criança não pode ser nulo");
        Objects.requireNonNull(dataNascimento, "Data de nascimento não pode ser nula");

        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            em = DBConnection.getEntityManager();
            transaction = em.getTransaction();
            transaction.begin();

            // 1. Criar e salvar a Criança
            Crianca crianca = criarCrianca(
                    nomeCrianca, dataNascimento, rgCrianca, cpfCrianca, certidaoNascimento,
                    municipioNascimento, cartorioRegistro, municipioRegistro, dataEmissaoRG,
                    orgaoEmissor, sexo, corRaca, cartSus, unidadeSaude, mobilidadeReduzida,
                    educacaoEspecial, classificacaoEspecial, alergia, possuiIrmaoGemeo,
                    tipoAuxilio, nis, mae, pai, responsavel, em
            );
            em.persist(crianca);

            // 2. Criar e salvar Endereço
            Endereco endereco = criarEndereco(
                    logradouro, numero, bairro, municipio, cep, uf, pontoReferencia,
                    telefoneResidencial, telefoneContato
            );
            em.persist(endereco);

            // 3. Salvar Situação Habitacional
            SituacaoHabitacional situacaoHabitacional = criarSituacaoHabitacional(
                    tipoMoradia, valorAluguel, numeroComodos, tipoPiso, materialParede,
                    tipoCobertura, fossa, cifon, energiaEletrica, aguaEncanada
            );
            situacaoHabitacional.setCrianca(crianca);
            em.persist(situacaoHabitacional);

            // 4. Criar e salvar Pré-Matrícula
            PreMatricula preMatricula = criarPreMatricula(crianca, situacaoHabitacional);
            em.persist(preMatricula);

            // 5. Salvar Bens da Família
            salvarBensFamilia(em, crianca, bensSelecionados);

            // 6. Salvar Membros da Família
            salvarMembrosFamilia(em, crianca, membrosFamiliares);

            // 7. Salvar Pessoas Autorizadas
            salvarPessoasAutorizadas(em, crianca, pessoasAutorizadas);

            // 8. Salvar Composição Familiar
            salvarComposicaoFamiliar(em, crianca, membrosFamiliares);

            transaction.commit();

            System.out.println("✅ Pré-matrícula salva com ID: " + preMatricula.getId());

            return preMatricula;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Erro ao salvar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar pré-matrícula: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // ====== MÉTODOS PRIVADOS ======

    private Crianca criarCrianca(
            String nome,
            LocalDate dataNascimento,
            String rg,
            String cpf,
            String certidaoNascimento,
            String municipioNascimento,
            String cartorioRegistro,
            String municipioRegistro,
            LocalDate dataEmissaoRG,
            String orgaoEmissor,
            String sexo,
            String corRaca,
            String cartSus,
            String unidadeSaude,
            String mobilidadeReduzida,
            Boolean educacaoEspecial,
            String classificacaoEspecial,
            String alergia,
            Boolean possuiIrmaoGemeo,
            String tipoAuxilio,
            String nis,
            Pessoa mae,
            Pessoa pai,
            Responsavel responsavel,
            EntityManager em
    ) {
        Crianca crianca = new Crianca();

        // === DADOS BÁSICOS OBRIGATÓRIOS ===
        crianca.setNome(nome);
        crianca.setDataNascimento(java.sql.Date.valueOf(dataNascimento));

        // === DOCUMENTOS ===
        crianca.setRG(rg);
        crianca.setCPF(cpf);
        crianca.setCertidaoNascimentoNum(certidaoNascimento);
        crianca.setMunicipioNascimento(municipioNascimento);
        crianca.setCartorioRegistro(cartorioRegistro);
        crianca.setMunicipioRegistro(municipioRegistro);

        if (dataEmissaoRG != null) {
            crianca.setDataEmissaoCertidao(java.sql.Date.valueOf(dataEmissaoRG));
        }
        crianca.setOrgEmissorCertidao(orgaoEmissor);

        // === SEXO ===
        if (sexo != null) {
            switch (sexo.toLowerCase()) {
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
        }

        // === COR/RAÇA ===
        if (corRaca != null) {
            switch (corRaca.toLowerCase()) {
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
        crianca.setCartSus(cartSus);
        crianca.setUnidadeSaude(unidadeSaude);

        // === MOBILIDADE REDUZIDA ===
        if (mobilidadeReduzida != null) {
            if (mobilidadeReduzida.contains("temporária")) {
                crianca.setMobRed(Crianca.MobRed.TEMPORARIA);
            } else if (mobilidadeReduzida.contains("permanente")) {
                crianca.setMobRed(Crianca.MobRed.PERMANENTE);
            } else {
                crianca.setMobRed(Crianca.MobRed.NENHUMA);
            }
        } else {
            crianca.setMobRed(Crianca.MobRed.NENHUMA);
        }

        // === EDUCAÇÃO ESPECIAL ===
        crianca.setEducEspecial(Boolean.TRUE.equals(educacaoEspecial));

        // === CLASSIFICAÇÃO ESPECIAL ===
        if (classificacaoEspecial != null && !classificacaoEspecial.equals("Nenhum")) {
            try {
                ClassificacaoEspecial classificacao = em.createQuery(
                        "SELECT c FROM ClassificacaoEspecial c WHERE c.classificacaoEspecial = :nome",
                        ClassificacaoEspecial.class
                ).setParameter("nome", classificacaoEspecial).getSingleResult();
                crianca.setClassificacaoEspecial(classificacao);
                crianca.setStatusClassificacaoEspecial(true);
            } catch (Exception e) {
                System.err.println("❌ Classificação especial não encontrada: " + classificacaoEspecial);
                crianca.setStatusClassificacaoEspecial(false);
            }
        } else {
            crianca.setStatusClassificacaoEspecial(false);
        }

        // === ALERGIAS ===
        crianca.setAlergia(alergia != null && !alergia.equals("Nenhum"));

        // === IRMÃO GÊMEO ===
        crianca.setPossuiIrmaoGemeo(Boolean.TRUE.equals(possuiIrmaoGemeo));

        // === CAMPOS COM VALORES PADRÃO ===
        crianca.setDefMulti(false);
        crianca.setPossuiIrmaoCreche(false);
        crianca.setRestricaoAlimentar(false);

        // === AUXÍLIO GOVERNO ===
        boolean temAuxilio = tipoAuxilio != null && !tipoAuxilio.equals("Nenhum");
        crianca.setResponsavelBeneficiarioAuxilioGov(temAuxilio);

        if (temAuxilio) {
            try {
                TipoAuxilio tipoAuxilioObj = em.createQuery(
                        "SELECT t FROM TipoAuxilio t WHERE t.nomeAuxilio = :nome",
                        TipoAuxilio.class
                ).setParameter("nome", tipoAuxilio).getSingleResult();
                crianca.setTipoAuxilio(tipoAuxilioObj);
            } catch (Exception e) {
                System.err.println("❌ Tipo de auxílio não encontrado: " + tipoAuxilio);
            }
        }

        // === NIS (SALVAR NO RESPONSÁVEL SE EXISTIR) ===
        if (nis != null && !nis.trim().isEmpty()) {
            if (responsavel != null) {
                Responsavel responsavelManaged = em.find(Responsavel.class, responsavel.getId());
                if (responsavelManaged != null) {
                    responsavelManaged.setNumeroNis(nis);
                }
            } else if (mae != null) {
                // Buscar responsável associado à pessoa mãe
                try {
                    Responsavel maeResponsavel = em.createQuery(
                            "SELECT r FROM Responsavel r WHERE r.pessoa.id = :pessoaId",
                            Responsavel.class
                    ).setParameter("pessoaId", mae.getId()).getSingleResult();
                    maeResponsavel.setNumeroNis(nis);
                } catch (Exception e) {
                    System.err.println("❌ Responsável não encontrado para a mãe: " + e.getMessage());
                }
            }
        }

        // === ASSOCIAR RESPONSÁVEIS ===
        if (mae != null) {
            Pessoa maeManaged = em.find(Pessoa.class, mae.getId());
            crianca.setMae(maeManaged);
        }

        if (pai != null) {
            Pessoa paiManaged = em.find(Pessoa.class, pai.getId());
            crianca.setPai(paiManaged);
        }

        if (responsavel != null) {
            Responsavel responsavelManaged = em.find(Responsavel.class, responsavel.getId());
            crianca.setResponsavel(responsavelManaged);
        }

        // === VALIDAÇÃO FINAL DOS CAMPOS OBRIGATÓRIOS ===
        validarCamposCrianca(crianca);

        System.out.println("✅ Criança criada com sucesso: " + crianca.getNome());

        return crianca;
    }

    private Endereco criarEndereco(
            String logradouro,
            String numero,
            String bairro,
            String municipio,
            String cep,
            String uf,
            String pontoReferencia,
            String telefoneResidencial,
            String telefoneContato
    ) {
        Endereco endereco = new Endereco();

        endereco.setLogradouro(logradouro);
        endereco.setNumero(numero);
        endereco.setBairro(bairro);
        endereco.setMunicipio(municipio);
        endereco.setCep(cep);
        endereco.setUf(uf);
        endereco.setPontoReferencia(pontoReferencia);
        endereco.setTelefoneResidencial(telefoneResidencial);
        endereco.setNumero(telefoneContato);

        return endereco;
    }

    private SituacaoHabitacional criarSituacaoHabitacional(
            String tipoMoradia,
            String valorAluguel,
            String numeroComodos,
            String tipoPiso,
            String materialParede,
            String tipoCobertura,
            Boolean fossa,
            Boolean cifon,
            Boolean energiaEletrica,
            Boolean aguaEncanada
    ) {
        SituacaoHabitacional situacao = new SituacaoHabitacional();

        // Tipo de moradia
        if (tipoMoradia != null) {
            situacao.setCasaPropria("Casa própria".equals(tipoMoradia));
            situacao.setCasaCedida("Casa cedida".equals(tipoMoradia));
            situacao.setCasaAlugada("Casa alugada".equals(tipoMoradia));
        }

        // Valor aluguel
        if (valorAluguel != null && !valorAluguel.trim().isEmpty()) {
            try {
                situacao.setValorAluguel(new BigDecimal(valorAluguel.trim().replace(",", ".")));
            } catch (NumberFormatException e) {
                System.err.println("❌ Erro ao converter valor do aluguel");
            }
        }

        // Número de cômodos
        if (numeroComodos != null && !numeroComodos.trim().isEmpty()) {
            try {
                situacao.setNumeroComodos(Integer.parseInt(numeroComodos.trim()));
            } catch (NumberFormatException e) {
                System.err.println("❌ Erro ao converter número de cômodos");
            }
        }

        // Características da moradia
        situacao.setTipoPiso(converterParaTipoPiso(tipoPiso));
        situacao.setTipoMoradia(converterParaTipoMoradia(materialParede));
        situacao.setTipoCobertura(converterParaTipoCobertura(tipoCobertura));

        // Serviços públicos
        situacao.setFossa(Boolean.TRUE.equals(fossa));
        situacao.setCifon(Boolean.TRUE.equals(cifon));
        situacao.setEnergiaEletrica(Boolean.TRUE.equals(energiaEletrica));
        situacao.setAguaEncanada(Boolean.TRUE.equals(aguaEncanada));

        return situacao;
    }

    private PreMatricula criarPreMatricula(Crianca crianca, SituacaoHabitacional situacaoHabitacional) {
        PreMatricula preMatricula = new PreMatricula();

        preMatricula.setCrianca(crianca);
        preMatricula.setDataPreMatricula(new java.sql.Date(System.currentTimeMillis()));
        preMatricula.setSituacaoHabitacional(situacaoHabitacional);
        preMatricula.setSituacaoPreMatricula(PreMatricula.SituacaoPreMatricula.EM_ANALISE);
        preMatricula.setObservacao("Pré-matrícula cadastrada em " + new java.util.Date());

        System.out.println("✅ Pré-matrícula criada - Situação: " + preMatricula.getSituacaoPreMatricula());

        return preMatricula;
    }

    private void salvarBensFamilia(EntityManager em, Crianca crianca, List<TipoBem> bensSelecionados) {
        if (bensSelecionados == null || bensSelecionados.isEmpty()) {
            System.out.println("ℹ️ Nenhum bem selecionado para salvar");
            return;
        }

        for (TipoBem tipoBem : bensSelecionados) {
            try {
                TipoBem managedTipoBem = em.find(TipoBem.class, tipoBem.getIdTipoBem());
                if (managedTipoBem == null) {
                    managedTipoBem = em.merge(tipoBem);
                }

                TipoBem bemFamilia = new TipoBem();
                em.persist(bemFamilia);

                System.out.println("✅ Bem salvo: " + tipoBem.getNomeBem() +
                        " (ID: " + tipoBem.getIdTipoBem() + ")");

            } catch (Exception e) {
                System.err.println("❌ Erro ao salvar bem: " + tipoBem.getNomeBem() + " - " + e.getMessage());
            }
        }

        System.out.println("✅ " + bensSelecionados.size() + " bem(ns) da família salvos no banco");
    }

    private void salvarMembrosFamilia(EntityManager em, Crianca crianca, List<MembroFamilia> membrosFamiliares) {
        if (membrosFamiliares == null || membrosFamiliares.isEmpty()) {
            System.out.println("ℹ️ Nenhum membro familiar para salvar");
            return;
        }

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

    private void salvarPessoasAutorizadas(EntityManager em, Crianca crianca, List<PessoaAutorizada> pessoasAutorizadas) {
        if (pessoasAutorizadas == null || pessoasAutorizadas.isEmpty()) {
            System.out.println("ℹ️ Nenhuma pessoa autorizada para salvar");
            return;
        }

        for (PessoaAutorizada pessoa : pessoasAutorizadas) {
            pessoa.setCrianca(crianca);

            if (pessoa.getId() != null) {
                em.merge(pessoa);
            } else {
                em.persist(pessoa);
            }
        }

        System.out.println("✅ " + pessoasAutorizadas.size() + " pessoa(s) autorizada(s) salva(s)");
    }

    private void salvarComposicaoFamiliar(EntityManager em, Crianca crianca, List<MembroFamilia> membrosFamiliares) {
        ComposicaoFamiliar composicao = new ComposicaoFamiliar();
        composicao.setCrianca(crianca);

        BigDecimal rendaTotal = BigDecimal.ZERO;
        if (membrosFamiliares != null) {
            for (MembroFamilia membro : membrosFamiliares) {
                if (membro.getRenda() != null) {
                    rendaTotal = rendaTotal.add(membro.getRenda());
                }
            }
        }
        composicao.setRendaFamiliarTotal(rendaTotal);

        int totalMembros = (membrosFamiliares != null ? membrosFamiliares.size() : 0) + 1;
        if (totalMembros > 0) {
            BigDecimal rendaPerCapita = rendaTotal.divide(
                    new BigDecimal(totalMembros), 2, RoundingMode.HALF_UP
            );
            composicao.setRendaPerCapita(rendaPerCapita);
        } else {
            composicao.setRendaPerCapita(BigDecimal.ZERO);
        }

        composicao.setTotalMembros(totalMembros);

        em.persist(composicao);
        System.out.println("✅ Composição familiar salva");
    }

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

    private void validarCamposCrianca(Crianca crianca) {
        System.out.println("=== VALIDAÇÃO DOS CAMPOS DA CRIANÇA ===");

        if (crianca.getDefMulti() == null) {
            crianca.setDefMulti(false);
        }

        if (crianca.getPossuiIrmaoCreche() == null) {
            crianca.setPossuiIrmaoCreche(false);
        }

        if (crianca.getRestricaoAlimentar() == null) {
            crianca.setRestricaoAlimentar(false);
        }

        if (crianca.getAlergia() == null) {
            crianca.setAlergia(false);
        }

        if (crianca.getEducEspecial() == null) {
            crianca.setEducEspecial(false);
        }

        if (crianca.getResponsavelBeneficiarioAuxilioGov() == null) {
            crianca.setResponsavelBeneficiarioAuxilioGov(false);
        }

        System.out.println("✅ Todos os campos obrigatórios validados");
    }
}