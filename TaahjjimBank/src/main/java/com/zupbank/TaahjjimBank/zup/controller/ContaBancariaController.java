package com.zupbank.TaahjjimBank.zup.controller;

import com.zupbank.TaahjjimBank.zup.model.ContaBancaria;
import com.zupbank.TaahjjimBank.zup.service.ValidacaoCpf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/contas")
public class ContaBancariaController {

//    @Autowired
//    private S3Service s3Service;

    private Set<String> uniqueAccounts = new HashSet<>();

    @PostMapping
    public ResponseEntity<?> criarConta(@RequestBody ContaBancaria conta) {
        // Validação de CPF
        if (!ValidacaoCpf.isValid(conta.getCpfProprietario())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CPF inválido.");
        }

        // Verificar unicidade de agência e número da conta
        String accountKey = conta.getAgencia() + "-" + conta.getNumeroCC();
        if (uniqueAccounts.contains(accountKey)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Agência e número da conta já existem.");
        }

        // Adicionar a conta ao conjunto de contas únicas
        uniqueAccounts.add(accountKey);

        // Salvar a conta no S3
        //    s3Service.saveContaBancaria(conta);

        return new ResponseEntity<>(conta, HttpStatus.CREATED);
    }}