package com.ufgec.trabalhocomputacional.utils;


import com.ufgec.trabalhocomputacional.classes.TipoVoo;
import com.ufgec.trabalhocomputacional.model.Aeroporto;
import com.ufgec.trabalhocomputacional.model.Aeroporto.Voo;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeradorVoos {
    private static final LocalTime[] MANHA_EARLY = {LocalTime.of(6, 0), LocalTime.of(7,59)};
    private static final LocalTime[] MANHA_MID = {LocalTime.of(8, 0), LocalTime.of(9,59)};
    private static final LocalTime[] MANHA_LATE = {LocalTime.of(10, 0), LocalTime.of(11, 59)};


    private static final LocalTime[] TARDE_EARLY = {LocalTime.of(12, 0), LocalTime.of(13, 59)};
    private static final LocalTime[] TARDE_MID = {LocalTime.of(14, 0), LocalTime.of(15, 59)};
    private static final LocalTime[] TARDE_LATE = {LocalTime.of(16, 0), LocalTime.of(17,59)};

    private static final LocalTime[] NOITE_EARLY = {LocalTime.of(18, 0), LocalTime.of(19,59)};
    private static final LocalTime[] NOITE_MID = {LocalTime.of(20, 0), LocalTime.of(21, 59)};
    private static final LocalTime[] NOITE_LATE = {LocalTime.of(22, 0), LocalTime.of(23,59)};

    private static final LocalTime[] MADRUGADA = {LocalTime.of(0, 0), LocalTime.of(5, 59)};

    private static final int VOOS_DOMESTICOS_DIARIOS = 5;

    public static List<Voo> gerarVoos(int dias) {
        ListaAleatoriaPonderada<LocalTime[]> probabilidadeHorario = new ListaAleatoriaPonderada<>();
        probabilidadeHorario.adicionar(MANHA_EARLY, 1);
        probabilidadeHorario.adicionar(MANHA_MID, 2.5);
        probabilidadeHorario.adicionar(MANHA_LATE, 2);

        probabilidadeHorario.adicionar(TARDE_EARLY, 1.5);
        probabilidadeHorario.adicionar(TARDE_MID, 1.5);
        probabilidadeHorario.adicionar(TARDE_LATE, 1);

        probabilidadeHorario.adicionar(NOITE_EARLY, 1);
        probabilidadeHorario.adicionar(NOITE_MID, 0.5);
        probabilidadeHorario.adicionar(NOITE_LATE, 0.25);

        probabilidadeHorario.adicionar(MADRUGADA, 0.5);

        //------------------------------------------------------------------------------//
        ListaAleatoriaPonderada<TipoVoo> probabilidadeTipoVoo = new ListaAleatoriaPonderada<>();

        probabilidadeTipoVoo.adicionar(TipoVoo.ECONOMICO, 7.5);
        probabilidadeTipoVoo.adicionar(TipoVoo.EXECUTIVO, 2);
        probabilidadeTipoVoo.adicionar(TipoVoo.PRIMEIRA_CLASSE, 0.5);
        //------------------------------------------------------------------------------//
        Aeroporto[] aeroportos = gerarAeroportos();
        TipoVoo[] tiposVoo = TipoVoo.values();
        Aeroporto[] aeroportoOrigemDestino;
        LocalDate now = LocalDate.now();
        LocalDate dataVoo;
        LocalTime horaVoo;
        TipoVoo classeVoo;
        List<Voo> voos = new ArrayList<>();
        //-------------------------------------------------------------------------------------//


        for(int i = 0;i < dias;i++) {
            for(int j = 0; j < VOOS_DOMESTICOS_DIARIOS; j++) {
                aeroportoOrigemDestino = gerarAeroportoOrigemDestino(aeroportos);
                dataVoo = now.plusDays(i);
                horaVoo = gerarHorarioValido(probabilidadeHorario.obter());
                classeVoo = probabilidadeTipoVoo.obter();

                try {
                    Voo voo = new Voo(classeVoo,
                            aeroportoOrigemDestino[0], aeroportoOrigemDestino[1],
                            LocalDateTime.of(dataVoo, horaVoo));

                    voos.add(voo);
                } catch(IllegalArgumentException e) {
                    j--;
                }
            }
        }

        return voos;
    }

    public static LocalTime gerarHorarioValido(LocalTime[] intervaloPossivel) {
        int horarioMaximo = (intervaloPossivel[1].getHour() * 60) + intervaloPossivel[1].getMinute();
        int horarioMinimo = (intervaloPossivel[0].getHour() * 60) + intervaloPossivel[0].getMinute();

        int intervaloPossivelMinutos = horarioMaximo - horarioMinimo;
        int minutos = (int) (Math.random() * intervaloPossivelMinutos);
        minutos = ((minutos + 4) / 5) * 5;

        int horarioHora = (int) Math.floor((double) minutos / 60);
        int horarioMinutos = (int) ((((double) minutos / 60) - horarioHora) * 60);
        horarioMinutos = ((horarioMinutos + 4) / 5) * 5;

        return LocalTime.of(horarioHora, horarioMinutos);
    }

    private static Aeroporto[] gerarAeroportos() {
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

        return Aeroporto.novosAeroportosViaJSON(aeroportoJson.toString());
    }

    private static Aeroporto[] gerarAeroportoOrigemDestino(Aeroporto[] aeroportos) {
        Aeroporto[] aeroportoOrigemDestino = new Aeroporto[2];

        do {
            aeroportoOrigemDestino[0] = aeroportos[(int) (Math.random() * aeroportos.length)];
            aeroportoOrigemDestino[1] = aeroportos[(int) (Math.random() * aeroportos.length)];
        } while(aeroportoOrigemDestino[0].equals(aeroportoOrigemDestino[1]));

        return aeroportoOrigemDestino;
    }
}
