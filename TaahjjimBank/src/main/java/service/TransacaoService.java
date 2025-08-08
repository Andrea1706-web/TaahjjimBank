package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            List<TransacaoModel> lista = mapper.readValue(body,
                    mapper.getTypeFactory().constructCollectionType(List.class, TransacaoModel.class));
            return lista.isEmpty() ? null : lista.get(0);
        } catch (Exception e) {
            throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
        }
    }

    @Override
    public List<TransacaoModel> obter(String contaOrigem) {
        String pathNormal = Consts.PATH_TRANSACAO + contaOrigem + ".json";
        String pathAgendada = Consts.PATH_TRANSACAO_AGENDADA + contaOrigem + ".json";

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
}
