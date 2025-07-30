package model;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

public class UsuarioModel {

    private final UUID id;
    @NotNull(message = "Username é obrigatório")
    @Pattern(regexp = ".{4,}", message = "Username deve ter no mínimo 4 caracteres")
    private String username;
    @NotNull(message = "Senha é obrigatória")
    @Pattern(
            regexp = "^(?=.*[A-Z]).{6,}$",
            message = "A senha deve ter no mínimo 6 caracteres e pelo menos uma letra maiúscula"
    )
    private String senha;

    @JsonCreator
    public UsuarioModel(
            @JsonProperty("username") String username,
            @JsonProperty("senha") String senha) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.senha = senha;
    }

    public UUID getId() {
        return id;
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

}

