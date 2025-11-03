package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UploadDocumentoModel {

    @NotNull(message = "cpf é obrigatório")
    @Pattern(
            regexp = "^(?:(?!^(\\d)\\1{10}$)\\d{11})$|^(?:(?!^(\\d)\\2{13}$)\\d{14})$",
            message = "Documento deve ser um CPF válido"
    )
    private String cpf;
    @NotNull(message = "rgFrente é obrigatório")
    private String rgFrente;
    @NotNull(message = "rgVerso é obrigatório")
    private String rgVerso;
    @NotNull(message = "comprovanteResidencia é obrigatório")
    private String comprovanteResidencia;

    public UploadDocumentoModel(
            @JsonProperty("cpf") String cpf,
            @JsonProperty("rgFrente") String rgFrente,
            @JsonProperty("rgVerso") String rgVerso,
            @JsonProperty("comprovanteResidencia") String comprovanteResidencia
    ) {
        this.cpf = cpf;
        this.rgFrente = rgFrente;
        this.rgVerso = rgVerso;
        this.comprovanteResidencia = comprovanteResidencia;
    }


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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
