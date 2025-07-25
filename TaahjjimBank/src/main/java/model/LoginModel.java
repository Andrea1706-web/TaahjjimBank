package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class LoginModel {

    @NotNull(message = "Username é obrigatório")
    private String username;
    @NotNull(message = "Senha é obrigatória")
    private String senha;
    private String token;

    @JsonCreator
    public LoginModel(
            @JsonProperty("username") String username,
            @JsonProperty("senha") String senha) {
        this.username = username;
        this.senha = senha;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
