package com.petshop.view;

import com.petshop.controller.ProprietarioController;
import com.petshop.model.Proprietario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProprietarioView extends JDialog {

    private final ProprietarioController controller = new ProprietarioController();

    private JTextField txtNome, txtEndereco, txtTelefone, txtEmail, txtBusca;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private Proprietario selecionado = null;

    public ProprietarioView(Frame parent) {
        super(parent, "Cadastro de Proprietários", true);
        setSize(750, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        iniciarComponentes();
        carregarTabela();
    }

    private void iniciarComponentes() {
        // Painel formulário
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Proprietário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome     = new JTextField(20);
        txtEndereco = new JTextField(20);
        txtTelefone = new JTextField(15);
        txtEmail    = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Nome*:"), gbc);
        gbc.gridx = 1; painelForm.add(txtNome, gbc);
        gbc.gridx = 2; painelForm.add(new JLabel("Telefone*:"), gbc);
        gbc.gridx = 3; painelForm.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1; painelForm.add(txtEndereco, gbc);
        gbc.gridx = 2; painelForm.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; painelForm.add(txtEmail, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnSalvar  = new JButton("💾 Salvar");
        JButton btnLimpar  = new JButton("🧹 Limpar");
        JButton btnDeletar = new JButton("🗑️ Deletar");

        btnSalvar.addActionListener(e  -> salvar());
        btnLimpar.addActionListener(e  -> limpar());
        btnDeletar.addActionListener(e -> deletar());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnDeletar);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(painelForm, BorderLayout.CENTER);
        painelTopo.add(painelBotoes, BorderLayout.SOUTH);
        add(painelTopo, BorderLayout.NORTH);

        // Busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.addActionListener(e -> buscar());
        painelBusca.add(new JLabel("Buscar por nome:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);

        // Tabela
        String[] colunas = {"ID", "Nome", "Telefone", "Email", "Endereço"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherFormulario());
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.add(painelBusca, BorderLayout.NORTH);
        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(painelTabela, BorderLayout.CENTER);
    }

    private void salvar() {
        try {
            Proprietario p = selecionado != null ? selecionado : new Proprietario();
            p.setNome(txtNome.getText().trim());
            p.setEndereco(txtEndereco.getText().trim());
            p.setTelefone(txtTelefone.getText().trim());
            p.setEmail(txtEmail.getText().trim());
            controller.salvar(p);
            JOptionPane.showMessageDialog(this, "Proprietário salvo com sucesso!");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletar() {
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um proprietário na tabela.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente deletar " + selecionado.getNome() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deletar(selecionado.getId());
                JOptionPane.showMessageDialog(this, "Deletado com sucesso!");
                limpar();
                carregarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscar() {
        try {
            List<Proprietario> lista = controller.buscarPorNome(txtBusca.getText().trim());
            preencherTabela(lista);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        try {
            preencherTabela(controller.listarTodos());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherTabela(List<Proprietario> lista) {
        modeloTabela.setRowCount(0);
        for (Proprietario p : lista) {
            modeloTabela.addRow(new Object[]{p.getId(), p.getNome(), p.getTelefone(), p.getEmail(), p.getEndereco()});
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        try {
            int id = (int) modeloTabela.getValueAt(row, 0);
            selecionado = controller.buscarPorId(id);
            txtNome.setText(selecionado.getNome());
            txtEndereco.setText(selecionado.getEndereco());
            txtTelefone.setText(selecionado.getTelefone());
            txtEmail.setText(selecionado.getEmail());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        selecionado = null;
        txtNome.setText("");
        txtEndereco.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        tabela.clearSelection();
    }
}