package com.ufgec.trabalhocomputacional.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AeroportoTest {
    void construtorTeste() {

    }
    @Test
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


    }
}