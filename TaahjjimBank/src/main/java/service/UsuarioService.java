package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.UsuarioModel;
import org.springframework.stereotype.Service;
import service.interfaces.iCrudService;
import util.*;

import java.util.List;

@Service
public class UsuarioService implements iCrudService<UsuarioModel> {
    private final DriverS3<UsuarioModel> driverS3;
    private final ObjectMapper objectMapper;
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
    public UsuarioModel obter(String nome) {
        String key = Consts.PATH_USUARIO + nome + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public UsuarioModel criar() {
        ValidationUtil.validar(this.model);
        validarDuplicidadeDocumento(this.model);
        validarDuplicidadeEmail(this.model);
        String key = Consts.PATH_USUARIO + this.model.getNome() + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }

    public List<UsuarioModel> listar() {
        return driverS3.readAll(Consts.PATH_USUARIO);
    }

    private void validarDuplicidadeDocumento(UsuarioModel model) {
        List<UsuarioModel> usuarios = listar();
        if (usuarios.stream().anyMatch(c -> c.getDocumento().equalsIgnoreCase(model.getDocumento()))) {
            throw new IllegalArgumentException("Documento já associado a um usuário: " + model.getDocumento());
        }
    }

    private void validarDuplicidadeEmail(UsuarioModel model) {
        List<UsuarioModel> usuarios = listar();
        if (usuarios.stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(model.getEmail()))) {
            throw new IllegalArgumentException("Email já associado a um usuário: " + model.getEmail());
        }
    }

}

