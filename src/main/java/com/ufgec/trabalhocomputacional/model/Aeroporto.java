package com.ufgec.trabalhocomputacional.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name="TB_AEROPORTO")
public class Aeroporto {

    @Id
    private String nome;

    private String cidade;

    private String Estado;

    private double latitude;

    private double longitude;

    private List<Voo> voos;
}
