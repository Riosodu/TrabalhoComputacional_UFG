package com.ufgec.trabalhocomputacional.classes;

public enum TipoVoo {
    ECONOMICO("Econômico", 3.0, 60),
    EXECUTIVO("Executivo", ECONOMICO.preco * 3.0, ECONOMICO.quantidadeAssentos / 3),
    PRIMEIRA_CLASSE("Primeira Classe", EXECUTIVO.preco * 15, ECONOMICO.quantidadeAssentos / 10);

    private final String nome;
    private final double preco;
    private final int quantidadeAssentos;

    TipoVoo(String nome, double preco, int quantidadeAssentos) {
        this.nome = nome;
        this.preco = preco;
        this.quantidadeAssentos = quantidadeAssentos;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeAssentos() {
        return quantidadeAssentos;
    }

    public int getQuantidadeTotalAssentos() {
        int index = this.ordinal();
        int assentos = 0;
        for(int i = index; i >= 0; i--)
            assentos += TipoVoo.values()[i].quantidadeAssentos;

        return assentos;
    }

    public TipoVoo[] getClassesDisponiveis() {
        int index = this.ordinal();
        TipoVoo classesDisponiveis[] = new TipoVoo[index+1];

        for(int i = index; i >= 0; i--) {
            classesDisponiveis[i] = TipoVoo.values()[i];
        }

        return classesDisponiveis;
    }
}
