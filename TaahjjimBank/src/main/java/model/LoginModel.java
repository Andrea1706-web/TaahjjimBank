package model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotNull;

public class LoginModel {

    @NotNull(message = "email é obrigatório")
    private String email;
    @NotNull(message = "Senha é obrigatória")
    private String senha;
    private String token;

    @JsonCreator
    public LoginModel(
            @JsonProperty("email") String username,
            @JsonProperty("senha") String senha) {
        this.email = username;
        this.senha = senha;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}