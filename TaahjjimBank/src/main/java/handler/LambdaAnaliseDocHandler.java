package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;
import java.util.List;


public class LambdaAnaliseDocHandler implements RequestHandler <S3Event, String> {

    public String handleRequest(S3Event s3Event, Context context) {

        TextractClient textractClient = TextractClient.create();

        try {
            // Obter informações do evento S3
            S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
            String bucketName = record.getS3().getBucket().getName();
            String documentoKey = record.getS3().getObject().getKey();

            context.getLogger().log("Processando documento: " + documentoKey + " do bucket " + bucketName);

            // Configurar o objeto S3 para análise
            software.amazon.awssdk.services.textract.model.S3Object s3Object = software.amazon.awssdk.services.textract.model.S3Object.builder()
                    .bucket(bucketName)
                    .name(documentoKey)
                    .build();

            Document document = Document.builder()
                    .s3Object(s3Object)
                    .build();

            // Chamar o Textract para analisar o documento
            AnalyzeDocumentRequest analyzeDocumentRequest = AnalyzeDocumentRequest.builder()
                    .document(document)
                    .featureTypes(FeatureType.TABLES, FeatureType.FORMS)
                    .build();

            AnalyzeDocumentResponse response = textractClient.analyzeDocument(analyzeDocumentRequest);

            // Processar os resultados
            List<Block> blocks = response.blocks();
            for (Block block : blocks) {
                context.getLogger().log("Tipo de bloco: " + block.blockTypeAsString());
                if (block.text() != null) {
                    context.getLogger().log("Texto: " + block.text());
                }
            }

            return "Analise concluída com sucesso para o documento: " + documentoKey;
        } catch (Exception e) {
            context.getLogger().log("Erro ao processar documento: " + e.getMessage());
            return "Erro ao processar documento";
        } finally {
            textractClient.close();
        }
    }


}
