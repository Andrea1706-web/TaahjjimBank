package model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import model.enums.eTipoDocumento;

import java.time.LocalDate;
import java.util.UUID;

public class UsuarioModel {

    private final UUID id;
    @NotNull(message = "Nome é obrigatório")
    @Pattern(regexp = ".{4,}", message = "Nome deve ter no mínimo 4 caracteres")
    private String nomeCompleto;
//    @NotNull(message = "dataNascimento é obrigatória")
//    @JsonFormat(pattern = "dd-MM-yyyy")
//    private LocalDate dataNascimento;
    @NotNull(message = "endereço é obrigatório")
    private String endereco;
    @NotNull(message = "telefone é obrigatório")
    @Pattern(
            regexp = "^\\d{11,12}$",
            message = "Telefone deve conter 11 ou 12 dígitos numéricos, sem espaços e caracteres especiais"
    )
    private String telefone;
    @NotNull(message = "tipoDocumento é obrigatório")
    private eTipoDocumento tipoDocumento;
    @NotNull(message = "Documento é obrigatório")
    @Pattern(
            regexp = "^(?:(?!^(\\d)\\1{10}$)\\d{11})$|^(?:(?!^(\\d)\\2{13}$)\\d{14})$",
            message = "Documento deve ser um CPF válido"
    )
    private String documento;
    @NotNull(message = "Email é obrigatório")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email deve ser válido"
    )
    private String email;
    @NotNull(message = "Senha é obrigatória")
    @Pattern(
            regexp = "^(?=.*[A-Z]).{6,}$",
            message = "A senha deve ter no mínimo 6 caracteres e pelo menos uma letra maiúscula"
    )
    private String senha;

    @JsonCreator
    public UsuarioModel(
            @JsonProperty("nomeCompleto") String nomeCompleto,
//            @JsonProperty("dataNascimento") LocalDate dataNascimento,
            @JsonProperty("endereco") String endereco,
            @JsonProperty("telefone") String telefone,
            @JsonProperty("tipoDocumento") eTipoDocumento tipoDocumento,
            @JsonProperty("documento") String documento,
            @JsonProperty("email") String email,
            @JsonProperty("senha") String senha) {
        this.id = UUID.randomUUID();
        this.nomeCompleto = nomeCompleto;
//        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.telefone = telefone;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.email = email;
        this.senha = senha;
    }

    public UUID getId() {
        return id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public eTipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(eTipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

//    public LocalDate getDataNascimento() {
//        return dataNascimento;
//    }
//
//    public void setDataNascimento(LocalDate dataNascimento) {
//        this.dataNascimento = dataNascimento;
//    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}