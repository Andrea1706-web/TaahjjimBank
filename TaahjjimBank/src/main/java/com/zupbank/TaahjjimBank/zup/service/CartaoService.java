package com.zupbank.TaahjjimBank.zup.service;

import com.zupbank.TaahjjimBank.zup.model.CartaoModel;
import com.zupbank.TaahjjimBank.zup.repository.CartaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartaoService {

    @Autowired
    CartaoRepository cartaoRepository;

    // método para exibir todos os cartoes cadastrados no banco de dados
    public List<CartaoModel> exibirTodos(){
        return cartaoRepository.findAll();
    }

    // Método para verificar se o número do cartão já existe
    public boolean verificarSeCartaoExiste(String numeroCartao) {
        return cartaoRepository.existsByNumeroCartao(numeroCartao);
    }

    // Método para inserir um novo cartão, verificando duplicidade
    @Transactional
    public CartaoModel inserir(CartaoModel cartaoModel) {
        if (verificarSeCartaoExiste(cartaoModel.getNumeroCartao())) {
            throw new IllegalArgumentException("Número do cartão já existe!");
        }
        return cartaoRepository.save(cartaoModel);
    }
}

