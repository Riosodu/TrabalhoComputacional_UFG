package com.ufgec.trabalhocomputacional.model;

import java.text.NumberFormat;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Esta classe representa um provedor de cartões que gera e remove cartões e
 * analisa a sua validade.
 */
public class ProvedorCartao {
    private static final NavigableSet<CartaoCompra> CARTOES_VALIDOS = new TreeSet<>();

    /**
     * Esta classe interna representa cartões de compra, a partir dela é possível
     * debitar valores,
     */
    public static class CartaoCompra implements Comparable<CartaoCompra> {
        private double saldo;
        private final long numeroCartao;
        private final YearMonth expiracao;


        private CartaoCompra(double saldo, long numeroCartao, YearMonth expiracao) throws IllegalArgumentException {
            this.saldo = saldo;
            this.numeroCartao = numeroCartao;
            this.expiracao = expiracao;

            if(!CARTOES_VALIDOS.add(this))
                throw new IllegalArgumentException("Cartão já existe!");
        }

        public long getNumeroCartao() {
            return numeroCartao;
        }

        public boolean possuiSaldo(double valor) {
            return saldo >= valor;
        }

        public boolean descontarValor(double valor, long numero, YearMonth expiracao) {
            if((numero==this.numeroCartao) && (expiracao==this.expiracao))
                if(valor <= this.saldo) {
                    saldo -= valor;
                    return true;
                }

            return false;
        }

        /**
         * Este método retorna o número do cartão em forma de uma String usando
         * a formatação padrão de cartões
         */
        private String numeroComoString() {
            int quadrantes[] = numeroComoQuadrantes();

            return String.format("%04d %04d %04d %04d", quadrantes[0], quadrantes[1], quadrantes[2], quadrantes[3]);
        }

        /**
         * Este método separa o número do cartão em grupos de 4 dígitos
         */
        private int[] numeroComoQuadrantes() {
            int quadrante[] = new int[4];

            int firstQuarter, secondQuarter, thirdQuarter, fourthQuarter;
            fourthQuarter = (int) (numeroCartao % Math.pow(10, 4));
            thirdQuarter = (int) ((numeroCartao / Math.pow(10, 4)) % Math.pow(10, 4));
            secondQuarter = (int) ((numeroCartao / Math.pow(10, 8)) % Math.pow(10 , 4));
            firstQuarter = (int) (numeroCartao / Math.pow(10, 12));

            quadrante[0] = firstQuarter;
            quadrante[1] = secondQuarter;
            quadrante[2] = thirdQuarter;
            quadrante[3] = fourthQuarter;

            return quadrante;
        }

        @Override
        public int compareTo(CartaoCompra o) {
            int[] quadrantesComparado = o.numeroComoQuadrantes();
            int[] quadrantesDeste = this.numeroComoQuadrantes();

            for(int i = 0; i < 4; i++) {
                if (quadrantesComparado[i] == quadrantesDeste[i])
                    continue;
                else
                    return quadrantesComparado[i] - quadrantesDeste[i];
            }

            return 0;
        }

        @Override
        public String toString() {
            String toString = "";
            toString += "Número do cartão: " + numeroComoString();

            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("MM/yy");
            toString += "\nData de validade: " + expiracao.format(formatador);

            NumberFormat dinheiro = NumberFormat.getCurrencyInstance(new Locale("pt", "Brazil"));
            toString += "\nSaldo: R$" + dinheiro.format(saldo).replaceAll("¤", "");

            return toString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CartaoCompra)) return false;
            CartaoCompra that = (CartaoCompra) o;
            return Double.compare(that.saldo, saldo) == 0 &&
                    getNumeroCartao() == that.getNumeroCartao() &&
                    expiracao.equals(that.expiracao);
        }

        @Override
        public int hashCode() {
            return Objects.hash(saldo, getNumeroCartao(), expiracao);
        }
    }

    public static boolean checarValidadeCartao(long numeroCartao) {
        CartaoCompra cartao = buscaCartaoPorNumero(numeroCartao);
        return cartao != null;
    }

    public static CartaoCompra gerarCartao() {
        double saldo = gerarSaldo();
        long numeroCartao = gerarNumeroCartao();
        YearMonth expiracao = gerarExpiracao();

        return new CartaoCompra(saldo, numeroCartao, expiracao);
    }

    public static CartaoCompra gerarCartao(double saldo) {
        long numeroCartao = gerarNumeroCartao();
        YearMonth expiracao = gerarExpiracao();

        return new CartaoCompra(saldo, numeroCartao, expiracao);
    }

    private static double gerarSaldo() {
        return Math.random() * Math.pow(10, 4);
    }

    private static long gerarNumeroCartao() {
        return (long) (Math.random() * 9999_9999_9999_9999L) + 1;
    }

    /**
     * Gera uma data de expiração aleatória de um intervalo a partir de 2010
     */
    private static YearMonth gerarExpiracao() {
        int intervaloPossivelAno = Year.now().getValue() - 2010 + 10;
        int anoValidade = (int) (Math.random() * intervaloPossivelAno + 2010);
        int mesValidade = (int) (Math.random() * 12) + 1;
        return YearMonth.of(anoValidade, mesValidade);
    }

    public static boolean removerCartao(long numeroCartao) {
        CartaoCompra cartao = buscaCartaoPorNumero(numeroCartao);

        if(cartao == null)
            return false;
        else {
            CARTOES_VALIDOS.remove(cartao);
            return true;
        }
    }

    /**
     * Este método busca um cartão a partir de seu número
     */
    private static CartaoCompra buscaCartaoPorNumero(long numero) {
        CartaoCompra dummy = new CartaoCompra(0, numero, null);
        CartaoCompra cartaoBuscado = CARTOES_VALIDOS.floor(dummy);

        if(cartaoBuscado != null)
            if(cartaoBuscado.numeroCartao == numero) {
                CARTOES_VALIDOS.remove(dummy);
                return cartaoBuscado;
            }

        CARTOES_VALIDOS.remove(dummy);
        return null;
    }
}
