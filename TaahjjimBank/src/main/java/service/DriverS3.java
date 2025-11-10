package service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.interfaces.iDriverS3;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DriverS3<T> implements iDriverS3<T> {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final Class<T> typeParameterClass;
    private final ObjectMapper objectMapper;

    public DriverS3(AmazonS3 s3Client, String bucketName, Class<T> typeParameterClass, ObjectMapper objectMapper) {
        this.s3Client = Objects.requireNonNull(s3Client);
        this.bucketName = Objects.requireNonNull(bucketName);
        this.typeParameterClass = Objects.requireNonNull(typeParameterClass);
        this.objectMapper = (objectMapper != null) ? objectMapper : new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    //mantido para nao quebrar o codigo antigo
    public DriverS3(String bucketName, Class<T> typeParameterClass) {
        this(AmazonS3ClientBuilder.defaultClient(), bucketName, typeParameterClass, new ObjectMapper());
    }

    @Override
    public void save(String key, T object) {
        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(object);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(jsonBytes.length);
            try (InputStream inputStream = new ByteArrayInputStream(jsonBytes)) {
                s3Client.putObject(bucketName, key, inputStream, metadata);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving object to S3", e);
        }
    }

    @Override
    public void saveFile(String key, InputStream inputStream, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);
        try {
            s3Client.putObject(bucketName, key, inputStream, metadata);
        } catch (Exception e) {
            throw new RuntimeException("Error saving object to S3", e);
        }
    }

    @Override
    public Optional<T> read(String key) {
        try (S3Object s3Object = s3Client.getObject(bucketName, key)) {
            try (InputStream inputStream = s3Object.getObjectContent()) {
                T object = objectMapper.readValue(inputStream, typeParameterClass);
                return Optional.ofNullable(object);
            }
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) return Optional.empty();
            throw new RuntimeException("S3 error reading key " + key, e);
        } catch (Exception e) {
            throw new RuntimeException("Error reading object from S3: " + key, e);
        }
    }

    @Override
    public List<T> readAll(String prefix) {
        try {
            ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, prefix);
            List<String> keys = result.getObjectSummaries()
                    .stream()
                    .map(S3ObjectSummary::getKey)
                    .collect(Collectors.toList());

            List<T> objetos = new ArrayList<>();
            for (String k : keys) {
                read(k).ifPresent(objetos::add);
            }
            return objetos;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar objetos do S3 com prefixo " + prefix, e);
        }
    }

    @Override
    public <E> void saveList(String key, List<E> list) {
        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(list);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(jsonBytes.length);
            try (InputStream inputStream = new ByteArrayInputStream(jsonBytes)) {
                s3Client.putObject(bucketName, key, inputStream, metadata);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving list to S3: " + key, e);
        }
    }

    @Override
    public <E> Optional<List<E>> readList(String key, Class<E> elementType) {
        try (S3Object s3Object = s3Client.getObject(bucketName, key)) {
            try (InputStream inputStream = s3Object.getObjectContent()) {
                List<E> list = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
                );
                return Optional.ofNullable(list);
            }
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) return Optional.empty();
            throw new RuntimeException("S3 error reading list key " + key, e);
        } catch (Exception e) {
            throw new RuntimeException("Error reading list from S3: " + key, e);
        }
    }

    @Override
    public List<String> listObjectsNames(String prefix) {
        try {
            ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, prefix);
            return result.getObjectSummaries()
                    .stream()
                    .map(S3ObjectSummary::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error listing objects with prefix " + prefix, e);
        }
    }

    @Override
    public void deleteObject(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting object " + key, e);
        }
    }
}
