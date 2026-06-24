package com.petshop.model;

import java.time.LocalDate;

public class LancamentoServico {

    private int id;
    private int animalId;
    private int servicoId;
    private LocalDate dataLancamento;
    private double valor;
    private String observacoes;

    // Objetos completos para exibição
    private Animal animal;
    private Servico servico;

    public LancamentoServico() {}

    public LancamentoServico(int animalId, int servicoId, LocalDate dataLancamento, double valor, String observacoes) {
        this.animalId = animalId;
        this.servicoId = servicoId;
        this.dataLancamento = dataLancamento;
        this.valor = valor;
        this.observacoes = observacoes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }

    public int getServicoId() { return servicoId; }
    public void setServicoId(int servicoId) { this.servicoId = servicoId; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }
}