package com.petshop.dao;

import com.petshop.model.Animal;
import com.petshop.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    public void inserir(Animal a) throws SQLException {
        String sql = "INSERT INTO animal (nome, especie, raca, idade, sexo, peso, foto_path, proprietario_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getEspecie());
            ps.setString(3, a.getRaca());
            ps.setInt(4, a.getIdade());
            ps.setString(5, a.getSexo().name());
            ps.setDouble(6, a.getPeso());
            ps.setString(7, a.getFotoPath());
            ps.setInt(8, a.getProprietarioId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) a.setId(rs.getInt(1));
        }
    }

    public void atualizar(Animal a) throws SQLException {
        String sql = "UPDATE animal SET nome=?, especie=?, raca=?, idade=?, sexo=?, peso=?, foto_path=?, proprietario_id=? WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getEspecie());
            ps.setString(3, a.getRaca());
            ps.setInt(4, a.getIdade());
            ps.setString(5, a.getSexo().name());
            ps.setDouble(6, a.getPeso());
            ps.setString(7, a.getFotoPath());
            ps.setInt(8, a.getProprietarioId());
            ps.setInt(9, a.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM animal WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Animal> listarTodos() throws SQLException {
        List<Animal> lista = new ArrayList<>();
        String sql = """
                SELECT a.*, p.nome as prop_nome FROM animal a
                JOIN proprietario p ON a.proprietario_id = p.id
                ORDER BY a.nome
                """;
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Animal> listarPorProprietario(int proprietarioId) throws SQLException {
        List<Animal> lista = new ArrayList<>();
        String sql = """
                SELECT a.*, p.nome as prop_nome FROM animal a
                JOIN proprietario p ON a.proprietario_id = p.id
                WHERE a.proprietario_id = ?
                ORDER BY a.nome
                """;
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, proprietarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Animal mapear(ResultSet rs) throws SQLException {
        Animal a = new Animal();
        a.setId(rs.getInt("id"));
        a.setNome(rs.getString("nome"));
        a.setEspecie(rs.getString("especie"));
        a.setRaca(rs.getString("raca"));
        a.setIdade(rs.getInt("idade"));
        a.setSexo(Animal.Sexo.valueOf(rs.getString("sexo")));
        a.setPeso(rs.getDouble("peso"));
        a.setFotoPath(rs.getString("foto_path"));
        a.setProprietarioId(rs.getInt("proprietario_id"));
        // Proprietário resumido
        com.petshop.model.Proprietario prop = new com.petshop.model.Proprietario();
        prop.setId(rs.getInt("proprietario_id"));
        prop.setNome(rs.getString("prop_nome"));
        a.setProprietario(prop);
        return a;
    }
}