package com.zupbank.TaahjjimBank.zup.repository;


import com.zupbank.TaahjjimBank.zup.model.CartaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaoRepository extends JpaRepository<CartaoModel, Long> {
    boolean existsByNumeroCartao(String numeroCartao);
}
