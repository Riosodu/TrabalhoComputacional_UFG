package com.ufgec.trabalhocomputacional.classes;

public enum TipoVoo {
    ECONOMICO("Econômico", 3.0, 60, 7.5),

    EXECUTIVO("Executivo", ECONOMICO.preco * 3.0,
            ECONOMICO.quantidadeAssentos / 3, 2),

    PRIMEIRA_CLASSE("Primeira Classe", EXECUTIVO.preco * 15,
            ECONOMICO.quantidadeAssentos / 10, 0.5);

    private final String nome;
    private final double preco;
    private final int quantidadeAssentos;
    private final double proporcaoVoos;

    TipoVoo(String nome, double preco, int quantidadeAssentos, double proporcaoVoos) {
        this.nome = nome;
        this.preco = preco;
        this.quantidadeAssentos = quantidadeAssentos;
        this.proporcaoVoos = proporcaoVoos;
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

    public double getProporcaoVoos() {
        return proporcaoVoos;
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

    public static double getProporcaoTotalTiposVoos(){
        double proporcaoTotal = 0;
        for(TipoVoo tipo: TipoVoo.values())
            proporcaoTotal += tipo.proporcaoVoos;

        return proporcaoTotal;
    }

    public static int mediaPassageirosPorVoo() {
        double somaPonderada = 0;
        for(TipoVoo tipo: TipoVoo.values())
            somaPonderada += (tipo.quantidadeAssentos * tipo.proporcaoVoos);

        return (int) (somaPonderada/ getProporcaoTotalTiposVoos());
    }
}
