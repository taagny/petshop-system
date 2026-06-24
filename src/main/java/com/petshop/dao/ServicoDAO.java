package com.petshop.dao;

import com.petshop.model.Servico;
import com.petshop.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    public void inserir(Servico s) throws SQLException {
        String sql = "INSERT INTO servico (nome, descricao, preco) VALUES (?, ?, ?)";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNome());
            ps.setString(2, s.getDescricao());
            ps.setDouble(3, s.getPreco());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) s.setId(rs.getInt(1));
        }
    }

    public void atualizar(Servico s) throws SQLException {
        String sql = "UPDATE servico SET nome=?, descricao=?, preco=? WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getNome());
            ps.setString(2, s.getDescricao());
            ps.setDouble(3, s.getPreco());
            ps.setInt(4, s.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM servico WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Servico> listarTodos() throws SQLException {
        List<Servico> lista = new ArrayList<>();
        String sql = "SELECT * FROM servico ORDER BY nome";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Servico mapear(ResultSet rs) throws SQLException {
        Servico s = new Servico();
        s.setId(rs.getInt("id"));
        s.setNome(rs.getString("nome"));
        s.setDescricao(rs.getString("descricao"));
        s.setPreco(rs.getDouble("preco"));
        return s;
    }
}