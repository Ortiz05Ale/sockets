package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import models.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CitaPanel extends JPanel {
    private JTable tableCitas;
    private DefaultTableModel tableModel;
    private JTextField txtFecha, txtMotivo;
    private JComboBox<Medico> cmbMedicos;
    private JComboBox<Paciente> cmbPacientes;
    private JComboBox<String> cmbHora;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar;
    private CitaService citaService;
    private MedicoService medicoService;
    private PacienteService pacienteService;
    private Cita citaSeleccionada;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Colores del tema moderno
    private static final Color PRIMARY_COLOR = new Color(34, 139, 230);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(52, 168, 83);
    private static final Color DANGER_COLOR = new Color(234, 67, 53);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color BORDER_COLOR = new Color(218, 220, 224);
    private static final Color HOVER_COLOR = new Color(232, 240, 254);
    
    public CitaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try {
            citaService = Cliente.getServiceLocator().getCitaService();
            medicoService = Cliente.getServiceLocator().getMedicoService();
            pacienteService = Cliente.getServiceLocator().getPacienteService();
            initComponents();
            cargarMedicos();
            cargarPacientes();
            cargarCitas();
        } catch (RemoteException e) {
            showErrorDialog("Error al conectar con el servidor: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // Panel principal del formulario con diseño card
        JPanel mainFormPanel = createCardPanel();
        mainFormPanel.setLayout(new BorderLayout(0, 20));
        
        // Título del formulario
        JLabel titleLabel = new JLabel("Gestión de Citas Médicas", JLabel.CENTER);
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
        
        // Primera fila - Fecha y Hora
        gbc.gridx = 0; gbc.gridy = 0;
        formFieldsPanel.add(createStyledLabel("Fecha:"), gbc);
        
        gbc.gridx = 1;
        txtFecha = createStyledTextField("yyyy-MM-dd");
        formFieldsPanel.add(txtFecha, gbc);
        
        gbc.gridx = 2;
        formFieldsPanel.add(createStyledLabel("Hora:"), gbc);
        
        gbc.gridx = 3;
        cmbHora = createStyledComboBox();
        cmbHora.setEditable(false);
        for(int i=7; i<=17; i++){
            String hora = String.format("%02d:00", i);
            cmbHora.addItem(hora);
        }
        formFieldsPanel.add(cmbHora, gbc);
        
        // Segunda fila - Motivo (span completo)
        gbc.gridx = 0; gbc.gridy = 1;
        formFieldsPanel.add(createStyledLabel("Motivo:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMotivo = createStyledTextField("Descripción del motivo de la cita");
        formFieldsPanel.add(txtMotivo, gbc);
        
        // Tercera fila - ComboBoxes
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formFieldsPanel.add(createStyledLabel("Médico:"), gbc);
        
        gbc.gridx = 1;
        cmbMedicos = createStyledComboBox();
        formFieldsPanel.add(cmbMedicos, gbc);
        
        gbc.gridx = 2;
        formFieldsPanel.add(createStyledLabel("Paciente:"), gbc);
        
        gbc.gridx = 3;
        cmbPacientes = createStyledComboBox();
        formFieldsPanel.add(cmbPacientes, gbc);
        
        // Panel de botones con diseño moderno
        JPanel buttonPanel = createButtonPanel();
        
        // Ensamblar el formulario
        mainFormPanel.add(titleLabel, BorderLayout.NORTH);
        mainFormPanel.add(formFieldsPanel, BorderLayout.CENTER);
        mainFormPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Configurar tabla con estilo moderno
        setupModernTable();
        JScrollPane scrollPane = new JScrollPane(tableCitas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel contenedor de la tabla
        JPanel tablePanel = createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = new JLabel("Lista de Citas", JLabel.LEFT);
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
        cargarMedicos();
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
    
    private JComboBox createStyledComboBox() {
        JComboBox comboBox = new JComboBox();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(200, 40));
        return comboBox;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(Color.WHITE);
        
        btnAgregar = createStyledButton("Agregar", ACCENT_COLOR, Color.WHITE);
        btnActualizar = createStyledButton("Actualizar", PRIMARY_COLOR, Color.WHITE);
        btnEliminar = createStyledButton("Eliminar", DANGER_COLOR, Color.WHITE);
        btnLimpiar = createStyledButton("Limpiar", new Color(108, 117, 125), Color.WHITE);
        
        panel.add(btnAgregar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
            new String[] {"ID", "Fecha", "Hora", "Motivo", "Médico", "Paciente"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableCitas = new JTable(tableModel);
        tableCitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableCitas.setRowHeight(45);
        tableCitas.setGridColor(BORDER_COLOR);
        tableCitas.setSelectionBackground(HOVER_COLOR);
        tableCitas.setSelectionForeground(TEXT_DARK);
        tableCitas.setShowVerticalLines(true);
        tableCitas.setShowHorizontalLines(true);
        
        // Estilizar header
        JTableHeader header = tableCitas.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 50));
        
        // Centrar contenido de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableCitas.getColumnCount(); i++) {
            tableCitas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Ajustar ancho de columnas
        tableCitas.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tableCitas.getColumnModel().getColumn(1).setPreferredWidth(100); // Fecha
        tableCitas.getColumnModel().getColumn(2).setPreferredWidth(80);  // Hora
        tableCitas.getColumnModel().getColumn(3).setPreferredWidth(200); // Motivo
        tableCitas.getColumnModel().getColumn(4).setPreferredWidth(150); // Médico
        tableCitas.getColumnModel().getColumn(5).setPreferredWidth(150); // Paciente
    }
    
    private void setupEventHandlers() {
        btnAgregar.addActionListener(this::agregarCita);
        btnActualizar.addActionListener(this::actualizarCita);
        btnEliminar.addActionListener(this::eliminarCita);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        tableCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableCitas.getSelectedRow();
                if (row >= 0) {
                    try {
                        int id = (Integer) tableModel.getValueAt(row, 0);
                        citaSeleccionada = citaService.obtenerCita(id);
                        
                        if (citaSeleccionada != null) {
                            txtFecha.setText(dateFormat.format(citaSeleccionada.getFecha()));
                            cmbHora.setSelectedItem(citaSeleccionada.getHora());
                            txtMotivo.setText(citaSeleccionada.getMotivo());

                            cmbMedicos.removeAllItems();
                            cargarMedicos();
                            // Seleccionar el médico en el combo
                            for (int i = 0; i < cmbMedicos.getItemCount(); i++) {
                                Medico medico = cmbMedicos.getItemAt(i);
                                if (medico.getCedula() == citaSeleccionada.getMedicoAsignado()) {
                                    cmbMedicos.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Seleccionar el paciente en el combo
                            for (int i = 0; i < cmbPacientes.getItemCount(); i++) {
                                Paciente paciente = cmbPacientes.getItemAt(i);
                                if (paciente.getCurp() == citaSeleccionada.getPacienteAsignado()) {
                                    cmbPacientes.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            btnActualizar.setEnabled(true);
                            btnEliminar.setEnabled(true);
                            btnAgregar.setEnabled(false);
                        }
                    } catch (RemoteException ex) {
                        showErrorDialog("Error al obtener la cita: " + ex.getMessage());
                    }
                }
            }
        });
    }

    public void cargarMedicos() {
        try {
            cmbMedicos.removeAllItems();
            List<Medico> medicos = medicoService.listarMedicos();
            for (Medico medico : medicos) {
                cmbMedicos.addItem(medico);
            }
            System.out.println("recarga");
        } catch (RemoteException e) {
            showErrorDialog("Error al cargar médicos: " + e.getMessage());
        }
    }
    
    private void cargarPacientes() {
        try {
            cmbPacientes.removeAllItems();
            List<Paciente> pacientes = pacienteService.listarPacientes();
            for (Paciente paciente : pacientes) {
                cmbPacientes.addItem(paciente);
            }
        } catch (RemoteException e) {
            showErrorDialog("Error al cargar pacientes: " + e.getMessage());
        }
    }
    
    private void cargarCitas() {
        try {
            tableModel.setRowCount(0);
            List<Cita> citas = citaService.listarCitas();
            for (Cita cita : citas) {
                tableModel.addRow(new Object[] {
                    cita.getId(),
                    dateFormat.format(cita.getFecha()),
                    cita.getHora(),
                    cita.getMotivo(),
                    cita.getNombreMedico(),
                    cita.getNombrePaciente()
                });
            }
        } catch (RemoteException e) {
            showErrorDialog("Error al cargar citas: " + e.getMessage());
        }
    }
    
    private void agregarCita(ActionEvent e) {
        if (!validarFormulario()) return;
        
        try {
            Cita cita = new Cita();
            cita.setFecha(dateFormat.parse(txtFecha.getText()));
            cita.setHora((String) cmbHora.getSelectedItem());
            cita.setMotivo(txtMotivo.getText());
            
            Medico medicoSeleccionado = (Medico) cmbMedicos.getSelectedItem();
            Paciente pacienteSeleccionado = (Paciente) cmbPacientes.getSelectedItem();
            
            if (medicoSeleccionado == null || pacienteSeleccionado == null) {
                showWarningDialog("Debe seleccionar un médico y un paciente");
                return;
            }
            
            cita.setMedicoAsignado(medicoSeleccionado.getCedula());
            cita.setPacienteAsignado(pacienteSeleccionado.getCurp());
            
            citaService.crearCita(cita);
            showSuccessDialog("Cita agregada correctamente");
            limpiarFormulario();
            cargarCitas();
        } catch (Exception ex) {
            showErrorDialog("Error al agregar cita: " + ex.getMessage());
        }
    }
    
    private void actualizarCita(ActionEvent e) {
        if (citaSeleccionada == null) {
            showWarningDialog("Debe seleccionar una cita para actualizar");
            return;
        }
        
        if (!validarFormulario()) return;
        
        try {
            citaSeleccionada.setFecha(dateFormat.parse(txtFecha.getText()));
            citaSeleccionada.setHora((String) cmbHora.getSelectedItem());
            citaSeleccionada.setMotivo(txtMotivo.getText());
            
            Medico medicoSeleccionado = (Medico) cmbMedicos.getSelectedItem();
            Paciente pacienteSeleccionado = (Paciente) cmbPacientes.getSelectedItem();
            
            citaSeleccionada.setMedicoAsignado(medicoSeleccionado.getCedula());
            citaSeleccionada.setPacienteAsignado(pacienteSeleccionado.getCurp());
            
            citaService.actualizarCita(citaSeleccionada);
            showSuccessDialog("Cita actualizada correctamente");
            limpiarFormulario();
            cargarCitas();
        } catch (Exception ex) {
            showErrorDialog("Error al actualizar cita: " + ex.getMessage());
        }
    }
    
    private void eliminarCita(ActionEvent e) {
        if (citaSeleccionada == null) {
            showWarningDialog("Debe seleccionar una cita para eliminar");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la cita seleccionada?", 
                                                 "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                citaService.eliminarCita(citaSeleccionada.getId());
                showSuccessDialog("Cita eliminada correctamente");
                limpiarFormulario();
                cargarCitas();
            } catch (RemoteException ex) {
                showErrorDialog("Error al eliminar cita: " + ex.getMessage());
            }
        }
    }
    
    private void limpiarFormulario() {
        txtFecha.setText("");
        cmbHora.setSelectedIndex(0);
        txtMotivo.setText("");
        if (cmbMedicos.getItemCount() > 0) cmbMedicos.setSelectedIndex(0);
        if (cmbPacientes.getItemCount() > 0) cmbPacientes.setSelectedIndex(0);
        citaSeleccionada = null;
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnAgregar.setEnabled(true);
    }
    
    private boolean validarFormulario() {
        if (txtFecha.getText().trim().isEmpty()) {
            showWarningDialog("La fecha es obligatoria");
            return false;
        }
        
        try {
            dateFormat.parse(txtFecha.getText());
        } catch (ParseException e) {
            showWarningDialog("El formato de la fecha debe ser yyyy-MM-dd");
            return false;
        }
        
        if (cmbHora.getSelectedItem() == null) {
            showWarningDialog("La hora es obligatoria");
            return false;
        }
    
        if (txtMotivo.getText().trim().isEmpty()) {
            showWarningDialog("El motivo es obligatorio");
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