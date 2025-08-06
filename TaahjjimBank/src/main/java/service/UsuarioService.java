package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.UsuarioModel;
import model.eTipoDocumento;
import org.springframework.stereotype.Service;
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
                throw new RuntimeException("Erro ao deserializar bodyJson", e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public UsuarioModel obter(String nome) {
        String key = PATH + nome + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public UsuarioModel criar() {
        ValidationUtil.validar(this.model);
        validarDuplicidadeDocumento(this.model);
        validarDuplicidadeEmail(this.model);
        validarCodEstabelecimentoObrigatorio(this.model);
        validarDocumentoPorTipo(this.model);
        String key = PATH + this.model.getNome() + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }

    public List<UsuarioModel> listar() {
        return driverS3.readAll(PATH);
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

    private void validarCodEstabelecimentoObrigatorio(UsuarioModel model) {
        if (model.getTipoDocumento() == eTipoDocumento.CNPJ) {
            if (model.getCodEstabelecimento() == null || model.getCodEstabelecimento().trim().isEmpty()) {
                throw new IllegalArgumentException("codEstabelecimento é obrigatório para usuários com tipoDocumento CNPJ");
            }
        }
    }

    private void validarDocumentoPorTipo(UsuarioModel model) {
        if (model.getTipoDocumento() == eTipoDocumento.CPF) {
            if (model.getDocumento() == null || model.getDocumento().length() != 11) {
                throw new IllegalArgumentException("Documento deve ter exatamente 11 caracteres para CPF");
            }
        } else if (model.getTipoDocumento() == eTipoDocumento.CNPJ) {
            if (model.getDocumento() == null || model.getDocumento().length() != 14) {
                throw new IllegalArgumentException("Documento deve ter exatamente 14 caracteres para CNPJ");
            }
        }
    }
}



