package com.ufgec.trabalhocomputacional.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TB_AEROPORTO")
public class Aeroporto {

    @Id
    private String nome;
}
