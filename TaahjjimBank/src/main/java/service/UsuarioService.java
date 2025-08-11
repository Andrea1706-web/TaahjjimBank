package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.UsuarioModel;
import org.springframework.stereotype.Service;
import service.interfaces.iCrudService;
import util.MensagensErro;
import util.ValidationUtil;

import java.util.List;

@Service
public class UsuarioService implements iCrudService<UsuarioModel> {
    private final DriverS3<UsuarioModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/usuario/";
    private final UsuarioModel model;

    public UsuarioService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, UsuarioModel.class);
        this.objectMapper = new ObjectMapper();

        if (body != null && !body.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(body, UsuarioModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public UsuarioModel obter(String username) {
        String key = PATH + username + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public UsuarioModel criar() {
        ValidationUtil.validar(this.model);
        validarDuplicidade(this.model);
        String key = PATH + this.model.getUsername() + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }

    public List<UsuarioModel> listar() {
        return driverS3.readAll(PATH);
    }

    private void validarDuplicidade(UsuarioModel model) {
        List<UsuarioModel> usuarios = listar();
        if (usuarios.stream().anyMatch(c -> c.getUsername().equalsIgnoreCase(model.getUsername()))) {
            throw new IllegalArgumentException(MensagensErro.USUARIO_DUPLICADO + model.getUsername());
        }
    }

}

