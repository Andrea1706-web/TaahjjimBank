package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.TransacaoModel;
import org.springframework.stereotype.Service;

@Service
public class TransacaoService implements iCrudService<TransacaoModel> {
    private final DriverS3<TransacaoModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/transacao/";
    private final TransacaoModel model;

    public TransacaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, TransacaoModel.class);
        this.objectMapper = new ObjectMapper();

        if (body != null && !body.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(body, TransacaoModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao deserializar bodyJson", e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public TransacaoModel obter(String id) {
        String key = PATH + id + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public TransacaoModel criar() {
        String key = PATH + this.model.getId() + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }
}