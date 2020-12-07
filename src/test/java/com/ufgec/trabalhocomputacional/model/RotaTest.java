package com.ufgec.trabalhocomputacional.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class RotaTest {
    @Test
    void testarRota(){
        Aeroporto.Gerador.gerarVoos(3);

        List<Rota> rotas =  Rota.gerarRotas(Aeroporto.getAeroportoPorSigla("SOD"), Aeroporto.getAeroportoPorSigla("GRU"),
                LocalDate.of(2020, 12, 15), LocalDate.of(2020, 12, 15));

        for(Rota rota: rotas)
            System.out.println(rota);
    }
}