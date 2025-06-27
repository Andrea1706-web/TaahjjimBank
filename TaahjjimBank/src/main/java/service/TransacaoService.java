package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.ContaBancariaModel;
import model.TransacaoModel;
import org.springframework.stereotype.Service;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        validarContas(this.model.getIdContaOrigem(), this.model.getIdContaDestino());

        String key1 = PATH + this.model.getIdContaOrigem() + ".json";
        List<TransacaoModel> contaOrigemList = driverS3.readList(key1, TransacaoModel.class)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + key1));
        this.model.setValorTransacao(this.model.getValorTransacao() * -1);
        contaOrigemList.add(this.model);
        driverS3.saveList(key1, contaOrigemList);

        String key2 = PATH + this.model.getIdContaDestino() + ".json";
        List<TransacaoModel> contaDestinoList = driverS3.readList(key2, TransacaoModel.class)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + key2));
        this.model.setValorTransacao(this.model.getValorTransacao() * -1);
        contaDestinoList.add(this.model);
        driverS3.saveList(key2, contaDestinoList);

        List<TransacaoModel> resultado = new ArrayList<>();
        resultado.add(this.model);
        return resultado;
    }

    private void validarContas(UUID idContaOrigem, UUID idContaDestino) {
        // Instancia o service de conta bancária
        ContaBancariaService contaBancariaService = new ContaBancariaService("zupbankdatabase", "");
        List<ContaBancariaModel> contas = contaBancariaService.listar();

        boolean contaOrigemExiste = contas.stream().anyMatch(conta -> conta.getId().equals(idContaOrigem));
        boolean contaDestinoExiste = contas.stream().anyMatch(conta -> conta.getId().equals(idContaDestino));

        if (!contaOrigemExiste) {
            throw new IllegalArgumentException("Conta origem não existe: " + idContaOrigem);
        }
        if (!contaDestinoExiste) {
            throw new IllegalArgumentException("Conta destino não existe: " + idContaDestino);
        }
    }

}