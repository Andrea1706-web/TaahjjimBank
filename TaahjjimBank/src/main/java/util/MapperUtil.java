package util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MapperUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T toModel(Object obj, Class<T> clazz) {
        // Converte um objeto genérico para uma instância do modelo especificado
        return mapper.convertValue(obj, clazz);
    }
}
