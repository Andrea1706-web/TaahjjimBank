package com.zupbank.TaahjjimBank.zup.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Optional;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class DriverS3<T> {
  private final AmazonS3 s3Client;
  private final String bucketName;
  private final Class<T> typeParameterClass;
  private final ObjectMapper objectMapper;

  public DriverS3(String bucketName, Class<T> typeParameterClass) {
    this.s3Client = AmazonS3ClientBuilder.defaultClient();
    this.bucketName = bucketName;
    this.typeParameterClass = typeParameterClass;
    this.objectMapper = new ObjectMapper();
  }

  public void save(String key, T object) {
    try {
      // Serializa o objeto para uma string JSON
      String json = objectMapper.writeValueAsString(object);

      // Converte a string JSON para um InputStream
      InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

      // Envia o objeto para o S3
      s3Client.putObject(bucketName, key, inputStream, null);

      inputStream.close();
    } catch (Exception e) {
      throw new RuntimeException("Error saving object to S3", e);
    }
  }

  public Optional<T> read(String key) {
    try {
      S3Object s3Object = s3Client.getObject(bucketName, key);
      InputStream inputStream = s3Object.getObjectContent();
      T object = objectMapper.readValue(inputStream, typeParameterClass);
      inputStream.close();
      return Optional.of(object);
    } catch (Exception e) {
      System.err.println("Error reading object from S3: " + e.getMessage());
      return Optional.empty();
    }
  }

}
