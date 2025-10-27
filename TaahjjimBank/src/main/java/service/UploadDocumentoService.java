package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.UploadDocumentoModel;
import service.interfaces.iCrudService;
import util.Consts;
import util.MensagensErro;
import util.ValidationUtil;

import java.io.InputStream;

public class UploadDocumentoService {

    private final DriverS3<UploadDocumentoModel> driverS3;
    private final ObjectMapper objectMapper;
    private final UploadDocumentoModel model;

    public UploadDocumentoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, UploadDocumentoModel.class);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        if (body != null && !body.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(body, UploadDocumentoModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
            }
        } else {
            this.model = null;
        }
    }


    public UploadDocumentoModel criar(
            InputStream rgFrenteStream, long rgFrenteSize, String rgFrenteContentType, String rgFrenteExt,
            InputStream rgVersoStream, long rgVersoSize, String rgVersoContentType, String rgVersoExt,
            InputStream comprovanteStream, long comprovanteSize, String comprovanteContentType, String comprovanteExt
    ) {
        ValidationUtil.validar(this.model);
        String cpf = this.model.getCpf();

        // Monta o prefixo do diretório
        String prefixo = Consts.PATH_ABERTURA_CONTA + cpf + "/";

        // Salva RG Frente
        String keyRgFrente = prefixo + "rgFrente." + rgFrenteExt;
        driverS3.saveFile(keyRgFrente, rgFrenteStream, rgFrenteSize, rgFrenteContentType);

        // Salva RG Verso
        String keyRgVerso = prefixo + "rgVerso." + rgVersoExt;
        driverS3.saveFile(keyRgVerso, rgVersoStream, rgVersoSize, rgVersoContentType);

        // Salva Comprovante de Residência
        String keyComprovante = prefixo + "comprovanteResidencia." + comprovanteExt;
        driverS3.saveFile(keyComprovante, comprovanteStream, comprovanteSize, comprovanteContentType);

        return this.model;
    }

}
