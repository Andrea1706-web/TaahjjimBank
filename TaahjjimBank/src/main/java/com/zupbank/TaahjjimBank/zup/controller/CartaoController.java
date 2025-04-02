package com.zupbank.TaahjjimBank.zup.controller;

import com.zupbank.TaahjjimBank.zup.model.CartaoModel;
import com.zupbank.TaahjjimBank.zup.service.CartaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping(path = "/cartoes")

public class CartaoController {

    @Autowired
    CartaoService cartaoService;

    @Operation(summary = "Permite cadastrar novo cartão", description = "Cadastrar Cartões")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")

    })

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartaoModel inserir(@RequestBody CartaoModel cartaoModel){
        return cartaoService.inserir(cartaoModel);
    }

    @Operation(summary = "Permite listar todos os cartões cadastrados", description = "Listar Cartões")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso!"),
            @ApiResponse(responseCode = "201", description = "Busca realizada com sucesso!"),
            @ApiResponse(responseCode = "405", description = "Nenhum cartão encontrado!")
    })

    @GetMapping
    public List<CartaoModel> exibirTodosOsCartoes(){
        return cartaoService.exibirTodos();
    }

    @GetMapping("/existe/{numeroCartao}")
    public ResponseEntity<Boolean> verificarSeCartaoExiste(@PathVariable String numeroCartao) {
        boolean existe = cartaoService.verificarSeCartaoExiste(numeroCartao);
        return ResponseEntity.ok(existe);
    }
}
