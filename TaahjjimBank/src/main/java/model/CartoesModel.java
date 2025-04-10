package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//criando getters, setters e constructors
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

// indicando que a classe é uma entidade do banco de dados e uma tabela
@Entity
@Table(name = "TB_CARTOES")
public class CartoesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cartao", length = 16, nullable = false)
    private String numeroCartao;

    @Column(name = "validade", length = 4, nullable = false)
    private String validade;

    @Column(name = "codigo", length = 3, nullable = false)
    private String codigo;

    @Column(name = "numero_conta", length = 10, nullable = true)
    private String numeroConta;

}
