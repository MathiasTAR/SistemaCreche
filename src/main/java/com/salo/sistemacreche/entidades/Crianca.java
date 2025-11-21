package com.salo.sistemacreche.entidades;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "CRIANCA")
public class Crianca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CRIANCA")
    private Long id;

    @Column(name = "FOTO")
    private String foto;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "RG")
    private String RG;

    @Column(name = "CPF")
    private String CPF;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    private Date dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEXO", nullable = false)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    @Column(name = "COR_RACA")
    private CorRaca corRaca;

    @Column(name = "POSSUI_IRMAO_CRECHE")
    private Boolean possuiIrmaoCreche;

    @Column(name = "POSSUI_IRMAO_GEMEO")
    private Boolean possuiIrmaoGemeo;

    @Column(name = "CARTSUS", unique = true, length = 15)
    private String cartSus;

    @Column(name = "UNIDADE_SAUDE")
    private String unidadeSaude;

    @Column(name = "MUNICIPIO_NASCIMENTO")
    private String municipioNascimento;

    @Column(name = "MUNICIPIORESGISTRO")
    private String MunicipioRegistro;

    @Column(name = "CARTORIO_REGISTRO")
    private String cartorioRegistro;

    @Column(name = "CERTIDAO_NASCIMENTO_NUM")
    private String certidaoNascimentoNum;

    @Column(name = "DATA_EMISSAO_CERTIDAO")
    private Date dataEmissaoCertidao;

    @Column(name = "ORG_EMISSOR_CERTIDAO", length = 10)
    private String orgEmissorCertidao;

    @Column(name = "RESTRICAO_ALIMENTAR")
    private Boolean restricaoAlimentar;

    @Column(name = "DESCRICAO_RESTRICOES_ALIMENTARES")
    private String descricaoRestricoesAlimentares;

    @Column(name = "ALERGIA")
    private Boolean alergia;

    @Column(name = "PROBLEMA_SAUDE", columnDefinition = "TEXT")
    private String problemaSaude;

    @Column(name = "RESTRI_ALIMENTAR", columnDefinition = "TEXT")
    private String restriAlimentar;

    @Enumerated(EnumType.STRING)
    @Column(name = "MOB_RED")
    private MobRed mobRed;

    @Column(name = "DEF_MULTI")
    private Boolean defMulti;

    @Column(name = "EDUC_ESPECIAL")
    private Boolean educEspecial;

    @Column(name = "RESPONSAVEL_BENEFICIARIO_AUXILIO_GOV")
    private Boolean responsavelBeneficiarioAuxilioGov;

    @ManyToOne
    @JoinColumn(name = "ID_RESPONSAVEL")
    private Responsavel responsavel;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO_AUXILIO")
    private TipoAuxilio tipoAuxilio;

    @ManyToOne
    @JoinColumn(name = "ID_CLASSIFICACAO_ESPECIAL")
    private ClassificacaoEspecial classificacaoEspecial;

    @Column(name = "STATUS_CLASSIFICACAO_ESPECIAL")
    private Boolean statusClassificacaoEspecial;

    @ManyToOne
    @JoinColumn(name = "ID_MAE")
    private Pessoa mae;

    @ManyToOne
    @JoinColumn(name = "ID_PAI")
    private Pessoa pai;

    @ManyToOne
    @JoinColumn(name = "ID_IRMAO_GEMEO")
    private Crianca irmaoGemeo;

    // Enums
    public enum Sexo {
        MASCULINO, FEMININO, OUTRO
    }

    public enum CorRaca {
        BRANCA, PRETA, PARDA, AMARELA, INDIGENA, OUTRO
    }

    public enum MobRed {
        TEMPORARIA, PERMANENTE, NENHUMA
    }

    // Getters e Setters

    public String getRG() {
        return RG;
    }

    public void setRG(String RG) {
        this.RG = RG;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }

    public CorRaca getCorRaca() { return corRaca; }
    public void setCorRaca(CorRaca corRaca) { this.corRaca = corRaca; }

    public Boolean getPossuiIrmaoCreche() { return possuiIrmaoCreche; }
    public void setPossuiIrmaoCreche(Boolean possuiIrmaoCreche) { this.possuiIrmaoCreche = possuiIrmaoCreche; }

    public Boolean getPossuiIrmaoGemeo() { return possuiIrmaoGemeo; }
    public void setPossuiIrmaoGemeo(Boolean possuiIrmaoGemeo) { this.possuiIrmaoGemeo = possuiIrmaoGemeo; }

    public String getCartSus() { return cartSus; }
    public void setCartSus(String cartSus) { this.cartSus = cartSus; }

    public String getUnidadeSaude() { return unidadeSaude; }
    public void setUnidadeSaude(String unidadeSaude) { this.unidadeSaude = unidadeSaude; }

    public String getMunicipioNascimento() { return municipioNascimento; }
    public void setMunicipioNascimento(String municipioNascimento) { this.municipioNascimento = municipioNascimento; }

    public String getMunicipioRegistro() {
        return MunicipioRegistro;
    }

    public void setMunicipioRegistro(String municipioRegistro) {
        MunicipioRegistro = municipioRegistro;
    }

    public String getCartorioRegistro() { return cartorioRegistro; }
    public void setCartorioRegistro(String cartorioRegistro) { this.cartorioRegistro = cartorioRegistro; }

    public String getCertidaoNascimentoNum() { return certidaoNascimentoNum; }
    public void setCertidaoNascimentoNum(String certidaoNascimentoNum) { this.certidaoNascimentoNum = certidaoNascimentoNum; }

    public Date getDataEmissaoCertidao() { return dataEmissaoCertidao; }
    public void setDataEmissaoCertidao(Date dataEmissaoCertidao) { this.dataEmissaoCertidao = dataEmissaoCertidao; }

    public String getOrgEmissorCertidao() { return orgEmissorCertidao; }
    public void setOrgEmissorCertidao(String orgEmissorCertidao) { this.orgEmissorCertidao = orgEmissorCertidao; }

    public Boolean getRestricaoAlimentar() { return restricaoAlimentar; }
    public void setRestricaoAlimentar(Boolean restricaoAlimentar) { this.restricaoAlimentar = restricaoAlimentar; }

    public String getDescricaoRestricoesAlimentares() { return descricaoRestricoesAlimentares; }
    public void setDescricaoRestricoesAlimentares(String descricaoRestricoesAlimentares) { this.descricaoRestricoesAlimentares = descricaoRestricoesAlimentares; }

    public Boolean getAlergia() { return alergia; }
    public void setAlergia(Boolean alergia) { this.alergia = alergia; }

    public String getProblemaSaude() { return problemaSaude; }
    public void setProblemaSaude(String problemaSaude) { this.problemaSaude = problemaSaude; }

    public String getRestriAlimentar() { return restriAlimentar; }
    public void setRestriAlimentar(String restriAlimentar) { this.restriAlimentar = restriAlimentar; }

    public MobRed getMobRed() { return mobRed; }
    public void setMobRed(MobRed mobRed) { this.mobRed = mobRed; }

    public Boolean getDefMulti() { return defMulti; }
    public void setDefMulti(Boolean defMulti) { this.defMulti = defMulti; }

    public Boolean getEducEspecial() { return educEspecial; }
    public void setEducEspecial(Boolean educEspecial) { this.educEspecial = educEspecial; }

    public Boolean getResponsavelBeneficiarioAuxilioGov() { return responsavelBeneficiarioAuxilioGov; }
    public void setResponsavelBeneficiarioAuxilioGov(Boolean responsavelBeneficiarioAuxilioGov) { this.responsavelBeneficiarioAuxilioGov = responsavelBeneficiarioAuxilioGov; }

    public Responsavel getResponsavel() { return responsavel; }
    public void setResponsavel(Responsavel responsavel) { this.responsavel = responsavel; }

    public TipoAuxilio getTipoAuxilio() { return tipoAuxilio; }
    public void setTipoAuxilio(TipoAuxilio tipoAuxilio) { this.tipoAuxilio = tipoAuxilio; }

    public ClassificacaoEspecial getClassificacaoEspecial() { return classificacaoEspecial; }
    public void setClassificacaoEspecial(ClassificacaoEspecial classificacaoEspecial) { this.classificacaoEspecial = classificacaoEspecial; }

    public Boolean getStatusClassificacaoEspecial() { return statusClassificacaoEspecial; }
    public void setStatusClassificacaoEspecial(Boolean statusClassificacaoEspecial) { this.statusClassificacaoEspecial = statusClassificacaoEspecial; }

    public Pessoa getMae() { return mae; }
    public void setMae(Pessoa mae) { this.mae = mae; }

    public Pessoa getPai() { return pai; }
    public void setPai(Pessoa pai) { this.pai = pai; }

    public Crianca getIrmaoGemeo() { return irmaoGemeo; }
    public void setIrmaoGemeo(Crianca irmaoGemeo) { this.irmaoGemeo = irmaoGemeo; }
}