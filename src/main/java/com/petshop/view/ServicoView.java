package com.petshop.view;

import com.petshop.controller.ServicoController;
import com.petshop.model.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ServicoView extends JDialog {

    private final ServicoController controller = new ServicoController();

    private JTextField txtNome, txtPreco;
    private JTextArea txtDescricao;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private Servico selecionado = null;

    public ServicoView(Frame parent) {
        super(parent, "Cadastro de Serviços", true);
        setSize(650, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        iniciarComponentes();
        carregarTabela();
    }

    private void iniciarComponentes() {
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Serviço"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome      = new JTextField(20);
        txtPreco     = new JTextField(10);
        txtDescricao = new JTextArea(3, 20);
        txtDescricao.setLineWrap(true);

        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Nome*:"), gbc);
        gbc.gridx = 1; painelForm.add(txtNome, gbc);
        gbc.gridx = 2; painelForm.add(new JLabel("Preço*:"), gbc);
        gbc.gridx = 3; painelForm.add(txtPreco, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; painelForm.add(new JScrollPane(txtDescricao), gbc);

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

        String[] colunas = {"ID", "Nome", "Preço", "Descrição"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherFormulario());
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void salvar() {
        try {
            Servico s = selecionado != null ? selecionado : new Servico();
            s.setNome(txtNome.getText().trim());
            s.setDescricao(txtDescricao.getText().trim());
            s.setPreco(Double.parseDouble(txtPreco.getText().trim().replace(",", ".")));
            controller.salvar(s);
            JOptionPane.showMessageDialog(this, "Serviço salvo com sucesso!");
            limpar();
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletar() {
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela.");
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

    private void carregarTabela() {
        try {
            List<Servico> lista = controller.listarTodos();
            modeloTabela.setRowCount(0);
            for (Servico s : lista) {
                modeloTabela.addRow(new Object[]{s.getId(), s.getNome(),
                        String.format("R$ %.2f", s.getPreco()), s.getDescricao()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        try {
            int id = (int) modeloTabela.getValueAt(row, 0);
            List<Servico> lista = controller.listarTodos();
            selecionado = lista.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
            if (selecionado != null) {
                txtNome.setText(selecionado.getNome());
                txtPreco.setText(String.valueOf(selecionado.getPreco()));
                txtDescricao.setText(selecionado.getDescricao());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        selecionado = null;
        txtNome.setText("");
        txtPreco.setText("");
        txtDescricao.setText("");
        tabela.clearSelection();
    }
}