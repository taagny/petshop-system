package com.petshop.controller;

import com.petshop.dao.AnimalDAO;
import com.petshop.model.Animal;

import java.sql.SQLException;
import java.util.List;

public class AnimalController {

    private final AnimalDAO dao = new AnimalDAO();

    public void salvar(Animal a) throws SQLException {
        validar(a);
        if (a.getId() == 0) {
            dao.inserir(a);
        } else {
            dao.atualizar(a);
        }
    }

    public void deletar(int id) throws SQLException {
        dao.deletar(id);
    }

    public List<Animal> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public List<Animal> listarPorProprietario(int proprietarioId) throws SQLException {
        return dao.listarPorProprietario(proprietarioId);
    }

    private void validar(Animal a) {
        if (a.getNome() == null || a.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do animal é obrigatório.");
        }
        if (a.getEspecie() == null || a.getEspecie().trim().isEmpty()) {
            throw new IllegalArgumentException("Espécie do animal é obrigatória.");
        }
        if (a.getSexo() == null) {
            throw new IllegalArgumentException("Sexo do animal é obrigatório.");
        }
        if (a.getProprietarioId() == 0) {
            throw new IllegalArgumentException("Proprietário do animal é obrigatório.");
        }
    }
}