package model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class AberturaContaModel {

    @NotNull(message = "Nome é obrigatório")
    @Pattern(regexp = ".{4,}", message = "Nome deve ter no mínimo 4 caracteres")
    private String nomeCompleto;
    @NotNull(message = "dataNascimento é obrigatória")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataNascimento;
    @NotNull(message = "cpf é obrigatório")
    @Pattern(
            regexp = "^(?:(?!^(\\d)\\1{10}$)\\d{11})$|^(?:(?!^(\\d)\\2{13}$)\\d{14})$",
            message = "Documento deve ser um CPF válido"
    )
    private String cpf;
    @NotNull(message = "cep é obrigatório")
    @Pattern(regexp = "^\\d{8}$", message = "CEP deve conter exatamente 8 dígitos numéricos")
    private String cep;
    @NotNull(message = "endereço é obrigatório")
    private String endereco;
    @NotNull(message = "Email é obrigatório")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email deve ser válido"
    )
    private String email;
    @NotNull(message = "telefone é obrigatório")
    @Pattern(
            regexp = "^\\d{11,12}$",
            message = "Telefone deve conter 11 ou 12 dígitos numéricos, sem espaços e caracteres especiais"
    )
    private String telefone;
    private int idStepFunction;

    public AberturaContaModel(
             @JsonProperty("nomeCompleto") String nomeCompleto,
             @JsonProperty("dataNascimento") LocalDate dataNascimento,
             @JsonProperty("cpf") String cpf,
             @JsonProperty("cep") String cep,
             @JsonProperty("endereco") String endereco,
             @JsonProperty("email") String email,
             @JsonProperty("telefone") String telefone) {
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.cep = cep;
        this.endereco = endereco;
        this.email = email;
        this.telefone = telefone;
        this.idStepFunction = 0123;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getIdStepFunction() {
        return idStepFunction;
    }

    public void setIdStepFunction(int idStepFunction) {
        this.idStepFunction = idStepFunction;
    }
}
