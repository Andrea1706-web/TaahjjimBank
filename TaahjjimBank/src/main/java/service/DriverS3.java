package service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DriverS3<T> {
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final Class<T> typeParameterClass;
    private final Class<?> elementType; // Para listas
    private final ObjectMapper objectMapper;

    // Construtor para objetos simples
    public DriverS3(String bucketName, Class<T> typeParameterClass) {
        this(bucketName, typeParameterClass, null);
    }

    // Construtor para listas (ex: List.class, TransacaoModel.class)
    public DriverS3(String bucketName, Class<T> typeParameterClass, Class<?> elementType) {
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
        this.bucketName = bucketName;
        this.typeParameterClass = typeParameterClass;
        this.elementType = elementType;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    public void save(String key, T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(jsonBytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(jsonBytes.length);

            s3Client.putObject(bucketName, key, inputStream, metadata);
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error saving object to S3", e);
        }
    }

    public Optional<T> read(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            InputStream inputStream = s3Object.getObjectContent();
            T object;

            if (List.class.isAssignableFrom(typeParameterClass) && elementType != null) {
                object = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
                );
            } else {
                object = objectMapper.readValue(inputStream, typeParameterClass);
            }

            inputStream.close();
            return Optional.of(object);
        } catch (Exception e) {
            System.err.println("Error reading object from S3: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<T> readAll(String prefix) {
        try {
            ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, prefix);
            List<S3ObjectSummary> summaries = result.getObjectSummaries();
            List<T> objetos = new ArrayList<>();

            for (S3ObjectSummary summary : summaries) {
                S3Object s3Object = s3Client.getObject(bucketName, summary.getKey());
                InputStream inputStream = s3Object.getObjectContent();
                T object;

                if (List.class.isAssignableFrom(typeParameterClass) && elementType != null) {
                    object = objectMapper.readValue(
                            inputStream,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
                    );
                } else {
                    object = objectMapper.readValue(inputStream, typeParameterClass);
                }

                objetos.add(object);
                inputStream.close();
            }

            return objetos;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar objetos do S3", e);
        }
    }

    public <E> void saveList(String key, List<E> list) {
        try {
            String json = objectMapper.writeValueAsString(list);
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(jsonBytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(jsonBytes.length);

            s3Client.putObject(bucketName, key, inputStream, metadata);
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error saving list to S3", e);
        }
    }

    public <E> Optional<List<E>> readList(String key, Class<E> elementType) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            InputStream inputStream = s3Object.getObjectContent();
            List<E> list = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
            );
            inputStream.close();
            return Optional.of(list);
        } catch (Exception e) {
            System.err.println("Error reading list from S3: " + e.getMessage());
            return Optional.empty();
        }
    }
}