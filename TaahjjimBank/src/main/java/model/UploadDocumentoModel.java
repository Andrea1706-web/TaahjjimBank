package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class UploadDocumentoModel {

    @NotNull(message = "rgFrente é obrigatório")
    private String rgFrente;
    @NotNull(message = "rgVerso é obrigatório")
    private String rgVerso;
    @NotNull(message = "comprovanteResidencia é obrigatório")
    private String comprovanteResidencia;

    public UploadDocumentoModel(
            @JsonProperty("rgFrente") String rgFrente,
            @JsonProperty("rgVerso") String rgVerso,
            @JsonProperty("comprovanteResidencia") String comprovanteResidencia
    ) {
        this.rgFrente = rgFrente;
        this.rgVerso = rgVerso;
        this.comprovanteResidencia = comprovanteResidencia;
    }

    public String getRgFrente() {
        return rgFrente;
    }

    public void setRgFrente(String rgFrente) {
        this.rgFrente = rgFrente;
    }

    public String getRgVerso() {
        return rgVerso;
    }

    public void setRgVerso(String rgVerso) {
        this.rgVerso = rgVerso;
    }

    public String getComprovanteResidencia() {
        return comprovanteResidencia;
    }

    public void setComprovanteResidencia(String comprovanteResidencia) {
        this.comprovanteResidencia = comprovanteResidencia;
    }
}
