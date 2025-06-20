package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.TransacaoModel;
import org.springframework.stereotype.Service;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransacaoService implements iCrudService<List<TransacaoModel>> {
    private final DriverS3<List<TransacaoModel>> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/transacao/";
    private final TransacaoModel model;

    public TransacaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, (Class<List<TransacaoModel>>) (Class<?>) List.class);
        this.objectMapper = new ObjectMapper();

        if (body != null && !body.trim().isEmpty()) {
            try {
                // Deserializar como uma lista de TransacaoModel
                List<TransacaoModel> models = objectMapper.readValue(
                        body,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, TransacaoModel.class)
                );
                this.model = models.isEmpty() ? null : models.get(0);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao deserializar bodyJson", e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public List<TransacaoModel> obter(String contaOrigem) {
        String key = PATH + contaOrigem + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public List<TransacaoModel> criar() {
        ValidationUtil.validar(this);
        String key1 = PATH + this.model.getIdContaOrigem() + ".json";
        List<TransacaoModel> contaOrigemList = driverS3.read(key1).orElse(new ArrayList<>());
        this.model.setValorTransacao(this.model.getValorTransacao() * -1);
        contaOrigemList.add(this.model);
        driverS3.save(key1, contaOrigemList);

        String key2 = PATH + this.model.getIdContaDestino() + ".json";
        List<TransacaoModel> contaDestinoList = driverS3.read(key2).orElse(new ArrayList<>());
        this.model.setValorTransacao(this.model.getValorTransacao() * -1);
        contaDestinoList.add(this.model);
        driverS3.save(key2, contaDestinoList);

        List<TransacaoModel> resultado = new ArrayList<>();
        resultado.add(this.model);
        return resultado;
    }
}