package handler;

import com.amazonaws.services.lambda.runtime.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import service.UploadDocumentoService;

import java.io.*;
import java.util.*;


public class LambdaUploadDocumentoHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        try {
            Map<String, String> headers = (Map<String, String>) event.get("headers");
            String contentType = headers.getOrDefault("content-type", headers.get("Content-Type"));
            String body = (String) event.get("body");
            boolean isBase64Encoded = Boolean.TRUE.equals(event.get("isBase64Encoded"));

            byte[] bodyBytes = isBase64Encoded ? Base64.getDecoder().decode(body) : body.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bodyBytes);

            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            org.apache.commons.fileupload.RequestContext requestContext = new org.apache.commons.fileupload.RequestContext() {
                @Override public String getCharacterEncoding() { return "UTF-8"; }
                @Override public String getContentType() { return contentType; }
                @Override public int getContentLength() { return bodyBytes.length; }
                @Override public InputStream getInputStream() { return inputStream; }
            };

            List<FileItem> items = upload.parseRequest(requestContext);

            // Variáveis para os campos
            String metadataJson = null;
            InputStream rgFrenteStream = null, rgVersoStream = null, comprovanteStream = null;
            long rgFrenteSize = 0, rgVersoSize = 0, comprovanteSize = 0;
            String rgFrenteContentType = null, rgVersoContentType = null, comprovanteContentType = null;
            String rgFrenteExt = null, rgVersoExt = null, comprovanteExt = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    if ("metadata".equals(item.getFieldName())) {
                        metadataJson = item.getString("UTF-8");
                    }
                } else {
                    String fieldName = item.getFieldName();
                    String fileName = item.getName();
                    String ext = fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.') + 1) : "";
                    if ("rgFrente".equals(fieldName)) {
                        rgFrenteStream = item.getInputStream();
                        rgFrenteSize = item.getSize();
                        rgFrenteContentType = item.getContentType();
                        rgFrenteExt = ext;
                    } else if ("rgVerso".equals(fieldName)) {
                        rgVersoStream = item.getInputStream();
                        rgVersoSize = item.getSize();
                        rgVersoContentType = item.getContentType();
                        rgVersoExt = ext;
                    } else if ("comprovanteResidencia".equals(fieldName)) {
                        comprovanteStream = item.getInputStream();
                        comprovanteSize = item.getSize();
                        comprovanteContentType = item.getContentType();
                        comprovanteExt = ext;
                    }
                }
            }

            // Chama o service
            UploadDocumentoService service = new UploadDocumentoService("zupbankdatabase", metadataJson);
            service.criar(
                    rgFrenteStream, rgFrenteSize, rgFrenteContentType, rgFrenteExt,
                    rgVersoStream, rgVersoSize, rgVersoContentType, rgVersoExt,
                    comprovanteStream, comprovanteSize, comprovanteContentType, comprovanteExt
            );

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 201);
            response.put("body", "{\"message\": \"Upload processado com sucesso\"}");
            response.put("headers", Map.of("Content-Type", "application/json"));
            response.put("isBase64Encoded", false);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 500);
            response.put("body", "{\"error\": \"" + e.getMessage() + "\"}");
            response.put("headers", Map.of("Content-Type", "application/json"));
            response.put("isBase64Encoded", false);
            return response;
        }
    }
}
