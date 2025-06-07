package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import models.Paciente;
import models.PacienteService;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

public class PacientePanel extends JPanel {
    private JTable tablePacientes;
    private DefaultTableModel tableModel;
    private JTextField txtCurp, txtNombre, txtTelefono, txtCorreo;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar;
    private PacienteService pacienteService;
    private Paciente pacienteSeleccionado;
    
    // Colores del tema moderno
    private static final Color PRIMARY_COLOR = new Color(34, 139, 230);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(52, 168, 83);
    private static final Color DANGER_COLOR = new Color(234, 67, 53);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color BORDER_COLOR = new Color(218, 220, 224);
    private static final Color HOVER_COLOR = new Color(232, 240, 254);
    
    public PacientePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try {
            pacienteService = Cliente.getServiceLocator().getPacienteService();
            initComponents();
            cargarPacientes();
        } catch (RemoteException e) {
            showErrorDialog("Error al conectar con el servidor: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // Panel principal del formulario con diseño card
        JPanel mainFormPanel = createCardPanel();
        mainFormPanel.setLayout(new BorderLayout(0, 20));
        
        // Título del formulario
        JLabel titleLabel = new JLabel("Gestión de Pacientes", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de campos del formulario
        JPanel formFieldsPanel = new JPanel(new GridBagLayout());
        formFieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Configuración base para los componentes
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Primera fila - Nombre y Especialidad
        gbc.gridx = 0; gbc.gridy = 0;
        formFieldsPanel.add(createStyledLabel("Curp:      "), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth=2;
        txtCurp = createStyledTextField("Curp");
        formFieldsPanel.add(txtCurp, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formFieldsPanel.add(createStyledLabel("Nombre:    "), gbc);
        
        gbc.gridx = 2; 
        gbc.gridwidth=2;
        txtNombre = createStyledTextField("Nombre");
        formFieldsPanel.add(txtNombre, gbc);
        
        // Segunda fila - Cédula y Correo
        gbc.gridx = 0; gbc.gridy = 2;
        formFieldsPanel.add(createStyledLabel("Teléfono:  "), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth=2;
        txtTelefono = createStyledTextField("Teléfono");
        formFieldsPanel.add(txtTelefono, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formFieldsPanel.add(createStyledLabel("Correo:    "), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth=2;
        txtCorreo = createStyledTextField("correo");
        formFieldsPanel.add(txtCorreo, gbc);
        
        // Panel de botones con diseño moderno
        JPanel buttonPanel = createButtonPanel();
        
        // Ensamblar el formulario
        mainFormPanel.add(titleLabel, BorderLayout.NORTH);
        mainFormPanel.add(formFieldsPanel, BorderLayout.CENTER);
        mainFormPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Configurar tabla con estilo moderno
        setupModernTable();
        JScrollPane scrollPane = new JScrollPane(tablePacientes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel contenedor de la tabla
        JPanel tablePanel = createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = new JLabel("Lista de Pacientes", JLabel.LEFT);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_DARK);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        add(mainFormPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        
        // Configurar eventos (manteniendo la funcionalidad original)
        setupEventHandlers();
        
        // Estado inicial de los botones
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setToolTipText(placeholder);
        
        // Efecto hover
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (textField.isEnabled()) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (textField.isEnabled()) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                }
            }
        });
        
        return textField;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(Color.WHITE);
        
        btnAgregar = createStyledButton("Agregar", ACCENT_COLOR);
        btnActualizar = createStyledButton("Actualizar", PRIMARY_COLOR);
        btnEliminar = createStyledButton("Eliminar", DANGER_COLOR);
        btnLimpiar = createStyledButton("Limpiar", new Color(108, 117, 125));
        
        panel.add(btnAgregar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Efectos hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor.darker());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                }
            }
        });
        
        return button;
    }
    
    private void setupModernTable() {
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Curp", "Nombre", "Teléfono", "Correo Electrónico"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablePacientes = new JTable(tableModel);
        tablePacientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablePacientes.setRowHeight(45);
        tablePacientes.setGridColor(BORDER_COLOR);
        tablePacientes.setSelectionBackground(HOVER_COLOR);
        tablePacientes.setSelectionForeground(TEXT_DARK);
        tablePacientes.setShowVerticalLines(true);
        tablePacientes.setShowHorizontalLines(true);
        
        // Estilizar header
        JTableHeader header = tablePacientes.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 50));
        
        // Centrar contenido de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablePacientes.getColumnCount(); i++) {
            tablePacientes.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Ajustar ancho de columnas
        tablePacientes.getColumnModel().getColumn(0).setPreferredWidth(120); // Cédula
        tablePacientes.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tablePacientes.getColumnModel().getColumn(2).setPreferredWidth(150); // Especialidad
        tablePacientes.getColumnModel().getColumn(3).setPreferredWidth(250); // Correo
    }
    
    private void setupEventHandlers() {
        btnAgregar.addActionListener(this::agregarPaciente);
        btnActualizar.addActionListener(this::actualizarPaciente);
        btnEliminar.addActionListener(this::eliminarPaciente);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        tablePacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePacientes.getSelectedRow();
                if (row >= 0) {
                    pacienteSeleccionado = new Paciente();
                    pacienteSeleccionado.setCurp((String) tableModel.getValueAt(row, 0));
                    pacienteSeleccionado.setNombre((String) tableModel.getValueAt(row, 1));
                    pacienteSeleccionado.setTelefono((String) tableModel.getValueAt(row, 2));
                    pacienteSeleccionado.setCorreoElectronico((String) tableModel.getValueAt(row, 3));
                    
                    txtTelefono.setText(pacienteSeleccionado.getCurp());
                    txtCurp.setText(pacienteSeleccionado.getNombre());
                    txtNombre.setText(pacienteSeleccionado.getTelefono());
                    txtCorreo.setText(pacienteSeleccionado.getCorreoElectronico());
                    
                    btnActualizar.setEnabled(true);
                    btnEliminar.setEnabled(true);
                    btnAgregar.setEnabled(false);
                }
            }
        });
    }
    
    private void cargarPacientes() {
        try {
            // Limpiar la tabla
            tableModel.setRowCount(0);
            
            // Obtener la lista de pacientes
            List<Paciente> pacientes = pacienteService.listarPacientes();
            
            // Agregar los pacientes a la tabla
            for (Paciente medico : pacientes) {
                tableModel.addRow(new Object[] {
                    medico.getCurp(),
                    medico.getNombre(),
                    medico.getTelefono(),
                    medico.getCorreoElectronico()
                });
            }
        } catch (RemoteException e) {
            showErrorDialog("Error al cargar pacientes: " + e.getMessage());
        }
    }
    
    private void agregarPaciente(ActionEvent e) {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            Paciente paciente = new Paciente();
            paciente.setCurp(txtCurp.getText());
            paciente.setNombre(txtNombre.getText());
            paciente.setTelefono(txtTelefono.getText());
            paciente.setCorreoElectronico(txtCorreo.getText());
            
            pacienteService.crearPaciente(paciente);
            
            showSuccessDialog("Paciente agregado correctamente");
            
            limpiarFormulario();
            cargarPacientes();
            
        } catch (RemoteException ex) {
            showErrorDialog("Error al agregar paciente: " + ex.getMessage());
        }
    }
    
    private void actualizarPaciente(ActionEvent e) {
        if (pacienteSeleccionado == null) {
            showWarningDialog("Debe seleccionar un paciente para actualizar");
            return;
        }
        
        if (!validarFormulario()) {
            return;
        }
        
        try {
            pacienteSeleccionado.setCurp(txtCurp.getText());
            pacienteSeleccionado.setNombre(txtNombre.getText());
            pacienteSeleccionado.setTelefono(txtTelefono.getText());
            pacienteSeleccionado.setCorreoElectronico(txtCorreo.getText());
            
            pacienteService.actualizarPaciente(pacienteSeleccionado);
            
            showSuccessDialog("Paciente actualizado correctamente");
            
            limpiarFormulario();
            cargarPacientes();
            
        } catch (RemoteException ex) {
            showErrorDialog("Error al actualizar paciente: " + ex.getMessage());
        }
    }
    
    private void eliminarPaciente(ActionEvent e) {
        if (pacienteSeleccionado == null) {
            showWarningDialog("Debe seleccionar un paciente para eliminar");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                                                 "¿Está seguro de eliminar el paciente " + pacienteSeleccionado.getNombre() + "?", 
                                                 "Confirmar eliminación", 
                                                 JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                pacienteService.eliminarPaciente(pacienteSeleccionado.getCurp());
                
                showSuccessDialog("Paciente eliminado correctamente");
                
                limpiarFormulario();
                cargarPacientes();
                
            } catch (RemoteException ex) {
                showErrorDialog("Error al eliminar paciente: " + ex.getMessage());
            }
        }
    }
    
    private void limpiarFormulario() {
        txtCurp.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        pacienteSeleccionado = null;
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnAgregar.setEnabled(true);
    }
    
    private boolean validarFormulario() {
        if (txtCurp.getText().trim().isEmpty()) {
            showWarningDialog("La curp es obligatoria");
            return false;
        }
        
        if (txtNombre.getText().trim().isEmpty()) {
            showWarningDialog("El nombre es obligatoria");
            return false;
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            showWarningDialog("El teléfono es obligatoria");
            return false;
        }
        
        if (txtCorreo.getText().trim().isEmpty()) {
            showWarningDialog("El correo electrónico es obligatorio");
            return false;
        }
        
        // Validar formato de correo electrónico
        if (!txtCorreo.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showWarningDialog("El formato del correo electrónico no es válido");
            return false;
        }
        
        return true;
    }
    
    // Métodos helper para mostrar diálogos con estilo
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}