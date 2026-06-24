package com.petshop.dao;

import com.petshop.model.*;
import com.petshop.util.ConexaoDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LancamentoServicoDAO {

    public void inserir(LancamentoServico ls) throws SQLException {
        String sql = "INSERT INTO lancamento_servico (animal_id, servico_id, data_lancamento, valor, observacoes) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ls.getAnimalId());
            ps.setInt(2, ls.getServicoId());
            ps.setDate(3, Date.valueOf(ls.getDataLancamento()));
            ps.setDouble(4, ls.getValor());
            ps.setString(5, ls.getObservacoes());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) ls.setId(rs.getInt(1));
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM lancamento_servico WHERE id=?";
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<LancamentoServico> listarPorAnimal(int animalId) throws SQLException {
        return listarComFiltro(animalId, null, null, null);
    }

    public List<LancamentoServico> listarPorAnimalEPeriodo(int animalId, LocalDate inicio, LocalDate fim) throws SQLException {
        return listarComFiltro(animalId, inicio, fim, null);
    }

    public List<LancamentoServico> listarPorAnimalEServico(int animalId, int servicoId) throws SQLException {
        return listarComFiltro(animalId, null, null, servicoId);
    }

    public List<LancamentoServico> listarPorProprietarioEPeriodo(int proprietarioId, LocalDate inicio, LocalDate fim) throws SQLException {
        List<LancamentoServico> lista = new ArrayList<>();
        String sql = """
                SELECT ls.*, a.nome as animal_nome, s.nome as servico_nome,
                       p.id as prop_id, p.nome as prop_nome, p.telefone, p.email, p.endereco
                FROM lancamento_servico ls
                JOIN animal a ON ls.animal_id = a.id
                JOIN servico s ON ls.servico_id = s.id
                JOIN proprietario p ON a.proprietario_id = p.id
                WHERE p.id = ? AND ls.data_lancamento BETWEEN ? AND ?
                ORDER BY ls.data_lancamento
                """;
        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, proprietarioId);
            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(fim));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapearCompleto(rs));
        }
        return lista;
    }

    private List<LancamentoServico> listarComFiltro(int animalId, LocalDate inicio, LocalDate fim, Integer servicoId) throws SQLException {
        List<LancamentoServico> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT ls.*, a.nome as animal_nome, s.nome as servico_nome
                FROM lancamento_servico ls
                JOIN animal a ON ls.animal_id = a.id
                JOIN servico s ON ls.servico_id = s.id
                WHERE ls.animal_id = ?
                """);
        if (inicio != null && fim != null) sql.append(" AND ls.data_lancamento BETWEEN ? AND ?");
        if (servicoId != null) sql.append(" AND ls.servico_id = ?");
        sql.append(" ORDER BY ls.data_lancamento DESC");

        try (Connection con = ConexaoDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, animalId);
            if (inicio != null && fim != null) {
                ps.setDate(idx++, Date.valueOf(inicio));
                ps.setDate(idx++, Date.valueOf(fim));
            }
            if (servicoId != null) ps.setInt(idx, servicoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapearCompleto(rs));
        }
        return lista;
    }

    private LancamentoServico mapearCompleto(ResultSet rs) throws SQLException {
        LancamentoServico ls = new LancamentoServico();
        ls.setId(rs.getInt("id"));
        ls.setAnimalId(rs.getInt("animal_id"));
        ls.setServicoId(rs.getInt("servico_id"));
        ls.setDataLancamento(rs.getDate("data_lancamento").toLocalDate());
        ls.setValor(rs.getDouble("valor"));
        ls.setObservacoes(rs.getString("observacoes"));

        Animal animal = new Animal();
        animal.setId(rs.getInt("animal_id"));
        animal.setNome(rs.getString("animal_nome"));
        ls.setAnimal(animal);

        Servico servico = new Servico();
        servico.setId(rs.getInt("servico_id"));
        servico.setNome(rs.getString("servico_nome"));
        ls.setServico(servico);

        return ls;
    }
}