package com.ufgec.trabalhocomputacional.classes;

public enum TipoVoo {
    ECONOMICO(3.0, 60),
    EXECUTIVO(ECONOMICO.preco * 3.0, ECONOMICO.quantidadeAssentos / 3),
    PRIMEIRA_CLASSE(EXECUTIVO.preco * 15, ECONOMICO.quantidadeAssentos / 10);

    private final double preco;
    private final int quantidadeAssentos;

    TipoVoo(double preco, int quantidadeAssentos) {
        this.preco = preco;
        this.quantidadeAssentos = quantidadeAssentos;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeAssentos() {
        return quantidadeAssentos;
    }

    public int getQuantidadeTotalAssentos() {
        int thisIndex = this.ordinal();
        int assentos = 0;
        for(int i = 0; i >= thisIndex; i--)
            assentos += TipoVoo.values()[i].quantidadeAssentos;

        return assentos;
    }
}
