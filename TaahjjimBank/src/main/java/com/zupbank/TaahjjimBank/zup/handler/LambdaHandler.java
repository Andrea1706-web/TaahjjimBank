package com.zupbank.TaahjjimBank.zup.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.zupbank.TaahjjimBank.zup.controller.CartaoController;
import com.zupbank.TaahjjimBank.zup.controller.ContaBancariaController;
import com.zupbank.TaahjjimBank.zup.model.CartaoModel;
import com.zupbank.TaahjjimBank.zup.model.ContaBancaria;
import com.zupbank.TaahjjimBank.zup.model.TipoConta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LambdaHandler {

    @Autowired
    private ContaBancariaController contaBancariaController;

    @Autowired
    private CartaoController cartaoController;

    @Override
    public ResponseEntity<?> handleRequest(Map<String, Object> event) {
        String path = (String) event.get("path");
        String httpMethod = (String) event.get("httpMethod");
        Map<String, Object> body = (Map<String, Object>) event.get("body");

        // Rotas para diferentes controllers
        if ("/contas".equals(path)) {
            if ("POST".equals(httpMethod)) {
                ContaBancaria conta = mapToContaBancaria(body);
                return contaBancariaController.criarConta(conta);
            }
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Método não suportado para /contas.");
        }


                if ("/cartoes".equals(path)) {
            if ("POST".equals(httpMethod)) {
                CartaoModel cartao = mapToCartaoModel(body);
                return ResponseEntity.status(HttpStatus.CREATED).body(cartaoController.criarCartao(cartao));
            }
            if ("GET".equals(httpMethod)) {
                return ResponseEntity.ok(cartaoController.exibirTodosOsCartoes());
            }
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Método não suportado para /cartoes.");
        }


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endpoint não encontrado.");
    }

    private ContaBancaria mapToContaBancaria(Map<String, Object> body) {
        return new ContaBancaria(
                (int) body.get("agencia"),
                (String) body.get("numeroCC"),
                body.get("saldo") != null ? new java.math.BigDecimal(body.get("saldo").toString()) : null,
                (String) body.get("cpfProprietario"),
                TipoConta.valueOf((String) body.get("tipConta"))
        );
    }
    private CartaoModel mapToCartaoModel(Map<String, Object> body) {
        return new CartaoModel(  null,
                (String) body.get("numeroCartao"),
                (String) body.get("validade"),
                (String) body.get("codigo"),
                (String) body.get("numeroConta")
        );
    }
}
