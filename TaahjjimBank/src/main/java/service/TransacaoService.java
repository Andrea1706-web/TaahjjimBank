package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransacaoPagamentoDebito;
import model.TransacaoPix;
import service.command.DebitoCommand;
import service.command.PixCommand;
import model.TransacaoModel;
import service.interfaces.iTransacaoCommand;
import service.interfaces.iCrudService;
import util.Consts;
import util.MensagensErro;
import util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransacaoService implements iCrudService<List<TransacaoModel>> {
    private final List<iTransacaoCommand> commands = List.of(new PixCommand(), new DebitoCommand()); //lista de comandos disponiveis
    private final DriverS3<TransacaoModel> driverS3;
    private final TransacaoModel model;

    public TransacaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, TransacaoModel.class);
        this.model = desserializarTransacao(body);
    }

    private TransacaoModel desserializarTransacao(String body) {
        if (body == null || body.isBlank()) return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.registerSubtypes(TransacaoPix.class, TransacaoPagamentoDebito.class);

            List<TransacaoModel> lista = mapper.readValue(body,
                    mapper.getTypeFactory().constructCollectionType(List.class, TransacaoModel.class));

            return lista.isEmpty() ? null : lista.get(0);
        } catch (Exception e) {
            throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
        }
    }


    @Override
    public List<TransacaoModel> obter(String contaOrigem) {
        String pathNormal = Consts.PATH_BUCKET_TRANSACAO + contaOrigem + ".json";
        String pathAgendada = Consts.PATH_BUCKET_TRANSACAO_AGENDADA + contaOrigem + ".json";
        List<TransacaoModel> transacoesNormais = driverS3.readList(pathNormal, TransacaoModel.class).orElse(new ArrayList<>());

        List<TransacaoModel> transacoesAgendadas = driverS3.readList(pathAgendada, TransacaoModel.class).orElse(new ArrayList<>());

        List<TransacaoModel> todasTransacoes = new ArrayList<>();
        todasTransacoes.addAll(transacoesNormais);
        todasTransacoes.addAll(transacoesAgendadas);

        return todasTransacoes;
    }

    @Override
    public List<TransacaoModel> criar() {
        ValidationUtil.validar(this.model);
        ContaBancariaService contaService = new ContaBancariaService(Consts.BUCKET, null);
        boolean isAgendada = model.getDataAgendamento().toLocalDate().isAfter(LocalDate.now());

        iTransacaoCommand cmd = commands.stream()
                .filter(c -> c.aceita(model.getTipoTransacao()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MensagensErro.TIPO_TRANSACAO_NAO_SUPORTADA));

        return cmd.executar(model, isAgendada, driverS3, contaService);
    }

     //Chamada do quick command analise de transações
    public class QuickCommandTansacoes {
    public static void main(String[] args) throws Exception {
        final String clientId = "c5213a2b-5277-46ed-84e0-df741c5b60f9";
        final String clientSecret = "4P84pefz3U0T199irrUm9CZVly7vSHY4hmg84ey09A2qtLtGJeK03EPOKVYd9BY9";

        // URLs específicas do QuickCommand de análise de transações
        String postUrl = "https://genai-code-buddy-api.stackspot.com/v1/quick-commands/create-execution/analisatransacao";
        String getUrlTemplate = "https://genai-code-buddy-api.stackspot.com/v1/quick-commands/callback/%s";

        QuickCommandUtil quickCommand = new QuickCommandUtil(clientId, clientSecret, postUrl, getUrlTemplate);

        String payload = "{ \"data\": \"dados da transação\" }";

        String executionId = quickCommand.executeQuickCommand(payload);
        System.out.println("Execution ID: " + executionId);

        String resultado = quickCommand.monitorExecution(executionId, 60, 5);
        System.out.println("Resultado da análise: " + resultado);
    }
}
}
