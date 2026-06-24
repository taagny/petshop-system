package com.petshop.view;

import com.petshop.controller.AnimalController;
import com.petshop.controller.ProprietarioController;
import com.petshop.model.Animal;
import com.petshop.model.Proprietario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AnimalView extends JDialog {

    private final AnimalController animalController         = new AnimalController();
    private final ProprietarioController proprietarioController = new ProprietarioController();

    private JTextField txtNome, txtEspecie, txtRaca, txtIdade, txtPeso;
    private JComboBox<String> cbSexo;
    private JComboBox<Proprietario> cbProprietario;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private Animal selecionado = null;

    public AnimalView(Frame parent) {
        super(parent, "Cadastro de Animais", true);
        setSize(800, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        iniciarComponentes();
        carregarProprietarios();
        carregarTabela();
    }

    private void iniciarComponentes() {
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Animal"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome    = new JTextField(15);
        txtEspecie = new JTextField(15);
        txtRaca    = new JTextField(15);
        txtIdade   = new JTextField(5);
        txtPeso    = new JTextField(5);
        cbSexo     = new JComboBox<>(new String[]{"M", "F"});
        cbProprietario = new JComboBox<>();

        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Nome*:"), gbc);
        gbc.gridx = 1; painelForm.add(txtNome, gbc);
        gbc.gridx = 2; painelForm.add(new JLabel("Espécie*:"), gbc);
        gbc.gridx = 3; painelForm.add(txtEspecie, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Raça:"), gbc);
        gbc.gridx = 1; painelForm.add(txtRaca, gbc);
        gbc.gridx = 2; painelForm.add(new JLabel("Sexo*:"), gbc);
        gbc.gridx = 3; painelForm.add(cbSexo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painelForm.add(new JLabel("Idade:"), gbc);
        gbc.gridx = 1; painelForm.add(txtIdade, gbc);
        gbc.gridx = 2; painelForm.add(new JLabel("Peso (kg):"), gbc);
        gbc.gridx = 3; painelForm.add(txtPeso, gbc);

        gbc.gridx = 0; gbc.gridy = 3; painelForm.add(new JLabel("Proprietário*:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; painelForm.add(cbProprietario, gbc);

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

        String[] colunas = {"ID", "Nome", "Espécie", "Raça", "Sexo", "Peso", "Proprietário"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherFormulario());
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarProprietarios() {
        try {
            cbProprietario.removeAllItems();
            for (Proprietario p : proprietarioController.listarTodos()) {
                cbProprietario.addItem(p);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar proprietários: " + ex.getMessage());
        }
    }

    private void salvar() {
        try {
            Proprietario prop = (Proprietario) cbProprietario.getSelectedItem();
            if (prop == null) {
                JOptionPane.showMessageDialog(this, "Selecione um proprietário.");
                return;
            }
            Animal a = selecionado != null ? selecionado : new Animal();
            a.setNome(txtNome.getText().trim());
            a.setEspecie(txtEspecie.getText().trim());
            a.setRaca(txtRaca.getText().trim());
            a.setIdade(txtIdade.getText().isEmpty() ? 0 : Integer.parseInt(txtIdade.getText().trim()));
            a.setPeso(txtPeso.getText().isEmpty() ? 0 : Double.parseDouble(txtPeso.getText().trim().replace(",", ".")));
            a.setSexo(Animal.Sexo.valueOf((String) cbSexo.getSelectedItem()));
            a.setProprietarioId(prop.getId());
            animalController.salvar(a);
            JOptionPane.showMessageDialog(this, "Animal salvo com sucesso!");
            limpar();
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Idade ou peso inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletar() {
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um animal na tabela.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente deletar " + selecionado.getNome() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                animalController.deletar(selecionado.getId());
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
            List<Animal> lista = animalController.listarTodos();
            modeloTabela.setRowCount(0);
            for (Animal a : lista) {
                modeloTabela.addRow(new Object[]{
                        a.getId(), a.getNome(), a.getEspecie(), a.getRaca(),
                        a.getSexo(), a.getPeso(),
                        a.getProprietario() != null ? a.getProprietario().getNome() : ""
                });
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
            List<Animal> lista = animalController.listarTodos();
            selecionado = lista.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
            if (selecionado != null) {
                txtNome.setText(selecionado.getNome());
                txtEspecie.setText(selecionado.getEspecie());
                txtRaca.setText(selecionado.getRaca());
                txtIdade.setText(String.valueOf(selecionado.getIdade()));
                txtPeso.setText(String.valueOf(selecionado.getPeso()));
                cbSexo.setSelectedItem(selecionado.getSexo().name());
                for (int i = 0; i < cbProprietario.getItemCount(); i++) {
                    if (cbProprietario.getItemAt(i).getId() == selecionado.getProprietarioId()) {
                        cbProprietario.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        selecionado = null;
        txtNome.setText("");
        txtEspecie.setText("");
        txtRaca.setText("");
        txtIdade.setText("");
        txtPeso.setText("");
        cbSexo.setSelectedIndex(0);
        if (cbProprietario.getItemCount() > 0) cbProprietario.setSelectedIndex(0);
        tabela.clearSelection();
    }
}