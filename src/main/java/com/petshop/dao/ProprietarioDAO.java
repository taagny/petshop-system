package com.petshop.dao;

import com.petshop.model.Proprietario;
import com.petshop.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProprietarioDAO {

    public void inserir(Proprietario p) throws SQLException {
        String sql = "INSERT INTO proprietario (nome, endereco, telefone, email) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getEndereco());
            ps.setString(3, p.getTelefone());
            ps.setString(4, p.getEmail());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) p.setId(rs.getInt(1));
        }
    }

    public void atualizar(Proprietario p) throws SQLException {
        String sql = "UPDATE proprietario SET nome=?, endereco=?, telefone=?, email=? WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getEndereco());
            ps.setString(3, p.getTelefone());
            ps.setString(4, p.getEmail());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM proprietario WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Proprietario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proprietario WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public List<Proprietario> listarTodos() throws SQLException {
        List<Proprietario> lista = new ArrayList<>();
        String sql = "SELECT * FROM proprietario ORDER BY nome";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Proprietario> buscarPorNome(String nome) throws SQLException {
        List<Proprietario> lista = new ArrayList<>();
        String sql = "SELECT * FROM proprietario WHERE nome LIKE ? ORDER BY nome";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Proprietario mapear(ResultSet rs) throws SQLException {
        Proprietario p = new Proprietario();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setEndereco(rs.getString("endereco"));
        p.setTelefone(rs.getString("telefone"));
        p.setEmail(rs.getString("email"));
        return p;
    }
}