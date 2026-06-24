package com.petshop.controller;

import com.petshop.dao.LancamentoServicoDAO;
import com.petshop.model.LancamentoServico;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LancamentoController {

    private final LancamentoServicoDAO dao = new LancamentoServicoDAO();

    public void salvar(LancamentoServico ls) throws SQLException {
        validar(ls);
        dao.inserir(ls);
    }

    public void deletar(int id) throws SQLException {
        dao.deletar(id);
    }

    public List<LancamentoServico> listarPorAnimal(int animalId) throws SQLException {
        return dao.listarPorAnimal(animalId);
    }

    public List<LancamentoServico> listarPorAnimalEPeriodo(int animalId, LocalDate inicio, LocalDate fim) throws SQLException {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser maior que a data final.");
        }
        return dao.listarPorAnimalEPeriodo(animalId, inicio, fim);
    }

    public List<LancamentoServico> listarPorAnimalEServico(int animalId, int servicoId) throws SQLException {
        return dao.listarPorAnimalEServico(animalId, servicoId);
    }

    public List<LancamentoServico> listarPorProprietarioEPeriodo(int proprietarioId, LocalDate inicio, LocalDate fim) throws SQLException {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser maior que a data final.");
        }
        return dao.listarPorProprietarioEPeriodo(proprietarioId, inicio, fim);
    }

    private void validar(LancamentoServico ls) {
        if (ls.getAnimalId() == 0) {
            throw new IllegalArgumentException("Animal é obrigatório.");
        }
        if (ls.getServicoId() == 0) {
            throw new IllegalArgumentException("Serviço é obrigatório.");
        }
        if (ls.getDataLancamento() == null) {
            throw new IllegalArgumentException("Data do lançamento é obrigatória.");
        }
        if (ls.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero.");
        }
    }
}