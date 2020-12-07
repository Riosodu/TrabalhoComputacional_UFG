package com.ufgec.trabalhocomputacional.utils;

import com.ufgec.trabalhocomputacional.model.Aeroporto;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

class GeradorTest {

    void gerarHorarioValidoTeste() {
        LocalTime[] intervalo = {LocalTime.of(0,0), LocalTime.of(12,30)};
        LocalTime horario;

        for(int i=0; i < 20; i++) {
            horario = Aeroporto.Gerador.gerarHorarioValido(intervalo);
            System.out.println(horario);
        }
    }

    @Test
    void gerarVoosTest() {
        List<Aeroporto.Voo> voos = Aeroporto.Gerador.gerarVoos(365);

        for(Aeroporto.Voo voo: voos) {
            System.out.println(voo + "\n");
        }

        System.out.println(voos.size());
    }
}