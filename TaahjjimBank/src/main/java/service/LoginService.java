package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.LoginModel;
import model.UsuarioModel;
import org.springframework.stereotype.Service;
import service.interfaces.iCrudService;
import util.JwtUtil;
import util.MensagensErro;
import util.ValidationUtil;

@Service
public class LoginService implements iCrudService<LoginModel> {
    private final DriverS3<LoginModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/login/";
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
    public LoginModel obter(String username) {
        String key = PATH + username + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public LoginModel criar() {
        ValidationUtil.validar(this.model);

        UsuarioService usuarioService = new UsuarioService("zupbankdatabase", null);
        UsuarioModel usuario = usuarioService.obter(this.model.getUsername());

        if (usuario == null || !usuario.getSenha().equals(this.model.getSenha())) {
            throw new IllegalArgumentException(MensagensErro.USUARIO_OU_SENHA_INVALIDOS);
        }

        String token = JwtUtil.generateToken(usuario.getUsername());
        this.model.setToken(token);

        return this.model;
    }
}
