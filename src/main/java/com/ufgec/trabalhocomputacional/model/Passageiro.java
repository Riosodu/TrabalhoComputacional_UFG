package com.ufgec.trabalhocomputacional.model;

public class Passageiro {
    private String nome;

    private String sobrenome;

    private boolean isMenorDeIdade;

    private int idade;

    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}
