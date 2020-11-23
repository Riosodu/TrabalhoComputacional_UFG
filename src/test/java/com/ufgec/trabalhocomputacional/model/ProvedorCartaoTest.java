package com.ufgec.trabalhocomputacional.model;

import org.junit.jupiter.api.Test;
import com.ufgec.trabalhocomputacional.model.ProvedorCartao.CartaoCompra;

class ProvedorCartaoTest {

    @Test
    void gerarCartao() {
        System.out.println(ProvedorCartao.gerarCartao());

        System.out.println(ProvedorCartao.gerarCartao(500));
    }

    @Test
    void numeroComoString(){
        CartaoCompra cartao = ProvedorCartao.gerarCartao(450);
        System.out.println("Long max value = " + Long.MAX_VALUE);
        long numeroCartao = (long) 0067000100002728.;//0067_0001_0000_2728
        System.out.println("NumeroCartao: " + numeroCartao);

        int firstQuarter, secondQuarter, thirdQuarter, fourthQuarter;
        fourthQuarter = (int) (numeroCartao % Math.pow(10, 4));
        thirdQuarter = (int) ((numeroCartao / Math.pow(10, 4)) % Math.pow(10, 4));
        secondQuarter = (int) ((numeroCartao / Math.pow(10, 8)) % Math.pow(10 , 4));
        firstQuarter = (int) (numeroCartao / Math.pow(10, 12));

        System.out.println("First quarter = " + firstQuarter);
        System.out.println("Second quarter = " + secondQuarter);
        System.out.println("Third quarter = " + thirdQuarter);
        System.out.println("Fourth quarter = " + fourthQuarter);

        System.out.printf("%04d %04d %04d %04d%n", firstQuarter, secondQuarter, thirdQuarter, fourthQuarter);
        //7940.6269.0786.7151
    }

    @Test
    void checarValidadeCartao() {
        CartaoCompra cartao;
        long numero = 0;
        for(int i = 0; i < 100; i++) {
            cartao = ProvedorCartao.gerarCartao();
            System.out.println(cartao);
            System.out.println("**************************\n\n");

            if(cartao.possuiSaldo(5_000))
                numero = cartao.getNumeroCartao();

        }
        if(numero > 0) {
            System.out.println(ProvedorCartao.checarValidadeCartao(numero));
        }

    }
}