package model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotNull;

public class LoginModel {

    @NotNull(message = "cpf é obrigatório")
    private String cpf;
    @NotNull(message = "Senha é obrigatória")
    private String senha;
    private String token;

    @JsonCreator
    public LoginModel(
            @JsonProperty("cpf") String cpf,
            @JsonProperty("senha") String senha) {
        this.cpf = cpf;
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}