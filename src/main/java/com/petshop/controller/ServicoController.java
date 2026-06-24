package com.petshop.controller;

import com.petshop.dao.ServicoDAO;
import com.petshop.model.Servico;

import java.sql.SQLException;
import java.util.List;

public class ServicoController {

    private final ServicoDAO dao = new ServicoDAO();

    public void salvar(Servico s) throws SQLException {
        validar(s);
        if (s.getId() == 0) {
            dao.inserir(s);
        } else {
            dao.atualizar(s);
        }
    }

    public void deletar(int id) throws SQLException {
        dao.deletar(id);
    }

    public List<Servico> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    private void validar(Servico s) {
        if (s.getNome() == null || s.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do serviço é obrigatório.");
        }
        if (s.getPreco() <= 0) {
            throw new IllegalArgumentException("Preço do serviço deve ser maior que zero.");
        }
    }
}