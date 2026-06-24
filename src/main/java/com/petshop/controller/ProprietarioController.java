package com.petshop.controller;

import com.petshop.dao.ProprietarioDAO;
import com.petshop.model.Proprietario;

import java.sql.SQLException;
import java.util.List;

public class ProprietarioController {

    private final ProprietarioDAO dao = new ProprietarioDAO();

    public void salvar(Proprietario p) throws SQLException {
        validar(p);
        if (p.getId() == 0) {
            dao.inserir(p);
        } else {
            dao.atualizar(p);
        }
    }

    public void deletar(int id) throws SQLException {
        dao.deletar(id);
    }

    public Proprietario buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public List<Proprietario> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public List<Proprietario> buscarPorNome(String nome) throws SQLException {
        return dao.buscarPorNome(nome);
    }

    private void validar(Proprietario p) {
        if (p.getNome() == null || p.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do proprietário é obrigatório.");
        }
        if (p.getTelefone() == null || p.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do proprietário é obrigatório.");
        }
    }
}