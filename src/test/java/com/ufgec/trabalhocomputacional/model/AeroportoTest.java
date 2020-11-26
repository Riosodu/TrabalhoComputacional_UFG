package com.ufgec.trabalhocomputacional.model;

import com.google.gson.Gson;
import com.ufgec.trabalhocomputacional.classes.TipoVoo;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AeroportoTest {
    void construtorTeste() {

    }
    /*@Test
    void construtoViaJson() {
        Gson gson = new Gson();

        String localArquivoAeroportoJson = "src/main/resources/templates/statics/aeroportos.json";
        StringBuilder aeroportoJson = new StringBuilder();
        File file = new File(localArquivoAeroportoJson);

        try(Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                aeroportoJson.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Aeroporto[] aeroportos = Aeroporto.novosAeroportosViaJSON(aeroportoJson.toString());

        System.out.println(aeroportos[0].distancia(aeroportos[1]));

        for(Map.Entry<String, Aeroporto> entry: Aeroporto.AEROPORTOS.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue().getNome());
        }


    }*/

    @Test
    void compareToVooTest() {
        Aeroporto aeroporto1 = new Aeroporto("nome1", "sigla1", "cidade1", "estado",
                "siglaEstado", 1, 10, 10);
        Aeroporto aeroporto2 = new Aeroporto("nome2", "sigla2", "cidade2", "estado",
                "siglaEstado", 2, -10, 20);
        Aeroporto aeroporto3 = new Aeroporto("nome3", "sigla3", "cidade2", "estado",
                "siglaEstado", 3, -20, 30);

        Aeroporto.Voo voo1 = new Aeroporto.Voo(TipoVoo.ECONOMICO,aeroporto1, aeroporto3,
                LocalDateTime.of(2020, 1,1, 0,0));
        Aeroporto.Voo voo2 = new Aeroporto.Voo(TipoVoo.ECONOMICO,aeroporto2, aeroporto3,
                LocalDateTime.of(2020, 1,1, 1,0));
        Aeroporto.Voo voo3 = new Aeroporto.Voo(TipoVoo.ECONOMICO,aeroporto1, aeroporto3,
                LocalDateTime.of(2020, 1,1, 0,30));

        Aeroporto.Voo vooOrigem = new Aeroporto.Voo(null, aeroporto1, null,
                null);

        System.out.println("Comparando voo1 com voo2: " + voo1.compareTo(voo2));
    }
}