import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import model.*;
import java.io.File;
import java.util.Set;

public class TaahjjimBankApplication {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);

        // Lista das classes
        Set<Class<?>> dtos = Set.of(
                AberturaContaModel.class,
                CartaoModel.class,
                UsuarioModel.class,
                TransacaoPix.class,
                TransacaoPagamentoDebito.class,
                TransacaoModel.class,
                ProdutoModel.class,
                LoginModel.class,
                ContaBancariaModel.class
        );

        // Cria pasta "schemas" na raiz
        File outputDir = new File(System.getProperty("user.dir"), "schemas");
        if (!outputDir.exists()) outputDir.mkdirs();

        // Gera um schema JSON para cada model
        for (Class<?> dto : dtos) {
            try {
                JsonSchema schema = schemaGen.generateSchema(dto);
                File outputFile = new File(outputDir, dto.getSimpleName() + ".json");
                mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, schema);
                System.out.println("chema gerado: " + dto.getSimpleName() + ".json");
            } catch (Exception e) {
                System.err.println("Erro ao gerar schema de " + dto.getSimpleName());
                e.printStackTrace();
            }
        }

        System.out.println("Todos os schemas foram gerados " + outputDir.getAbsolutePath());
    }
}

/* npx swagger-cli bundle openapi.yaml -o openapi-merged.yaml --type yaml */