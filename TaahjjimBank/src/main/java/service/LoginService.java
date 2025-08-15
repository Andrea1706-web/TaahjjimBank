package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import org.springframework.stereotype.Service;
import service.interfaces.iCrudService;
import util.*;

@Service
public class LoginService implements iCrudService<LoginModel> {
    private final DriverS3<LoginModel> driverS3;
    private final ObjectMapper objectMapper;
    private final LoginModel model;

    public LoginService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, LoginModel.class);
        this.objectMapper = new ObjectMapper();

        if (body != null && !body.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(body, LoginModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public LoginModel obter(String email) {
        String key = Consts.PATH_LOGIN + email + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public LoginModel criar() {
        ValidationUtil.validar(this.model);

        UsuarioService usuarioService = new UsuarioService(Consts.BUCKET, null);
        UsuarioModel usuario = usuarioService.obter(this.model.getEmail());

        if (usuario == null || !usuario.getSenha().equals(this.model.getSenha())) {
            throw new IllegalArgumentException(MensagensErro.USUARIO_OU_SENHA_INVALIDOS);
        }

        String token = JwtUtil.generateToken(usuario.getEmail());
        this.model.setToken(token);

        return this.model;
    }
}
