package com.petshop.model;

import java.time.LocalDateTime;

public class Animal {

    public enum Sexo { M, F }

    private int id;
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private Sexo sexo;
    private double peso;
    private String fotoPath;
    private int proprietarioId;
    private Proprietario proprietario;
    private LocalDateTime dataCadastro;

    public Animal() {}

    public Animal(String nome, String especie, String raca, int idade, Sexo sexo, double peso, int proprietarioId) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.sexo = sexo;
        this.peso = peso;
        this.proprietarioId = proprietarioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }

    public int getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(int proprietarioId) { this.proprietarioId = proprietarioId; }

    public Proprietario getProprietario() { return proprietario; }
    public void setProprietario(Proprietario proprietario) { this.proprietario = proprietario; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() { return nome + " (" + especie + ")"; }
}