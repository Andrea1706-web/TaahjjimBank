package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransacaoModel;
import org.springframework.stereotype.Service;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransacaoService implements iCrudService<List<TransacaoModel>> {
    private final DriverS3<TransacaoModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/transacao/";
    private final TransacaoModel model;

    public TransacaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, TransacaoModel.class);
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        if (body != null && !body.trim().isEmpty()) {
            try {
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
        // Aqui você precisa ler uma lista de transações
        return driverS3.readList(key, TransacaoModel.class).orElse(null);
    }

    @Override
    public List<TransacaoModel> criar() {
        ValidationUtil.validar(this.model);

        ContaBancariaService contaService = new ContaBancariaService("zupbankdatabase", null);

        // Validação de existência das contas
        if (!contaService.contaExiste(this.model.getNumeroContaOrigem())) {
            throw new IllegalArgumentException("Conta origem não existe: " + this.model.getNumeroContaOrigem());
        }
        if (!contaService.contaExiste(this.model.getNumeroContaDestino())) {
            throw new IllegalArgumentException("Conta destino não existe: " + this.model.getNumeroContaDestino());
        }

        String key1 = PATH + this.model.getNumeroContaOrigem() + ".json";
        List<TransacaoModel> contaOrigemList = driverS3.readList(key1, TransacaoModel.class)
                .orElse(new ArrayList<>());
        this.model.setValorTransacao(this.model.getValorTransacao() * -1);
        contaOrigemList.add(this.model);
        driverS3.saveList(key1, contaOrigemList);

        String key2 = PATH + this.model.getNumeroContaDestino() + ".json";
        List<TransacaoModel> contaDestinoList = driverS3.readList(key2, TransacaoModel.class)
                .orElse(new ArrayList<>());
        this.model.setValorTransacao(this.model.getValorTransacao() * -1);
        contaDestinoList.add(this.model);
        driverS3.saveList(key2, contaDestinoList);

        List<TransacaoModel> resultado = new ArrayList<>();
        resultado.add(this.model);
        return resultado;
    }
}