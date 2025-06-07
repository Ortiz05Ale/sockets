package client;

import services.Paciente;
import services.PacienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

public class PacientePanel extends JPanel {
    private JTable tablePacientes;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtCurp, txtTelefono, txtCorreo;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar;
    private PacienteService pacienteService;
    private Paciente pacienteSeleccionado;
    
    public PacientePanel() {
        try {
            pacienteService = Cliente.getServiceLocator().getPacienteService();
            initComponents();
            cargarPacientes();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + e.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Etiquetas y campos de texto
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("CURP:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        txtCurp = new JTextField(20);
        formPanel.add(txtCurp, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        txtTelefono = new JTextField(20);
        formPanel.add(txtTelefono, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Correo Electrónico:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        txtCorreo = new JTextField(20);
        formPanel.add(txtCorreo, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        btnAgregar = new JButton("Agregar");
        btnAgregar.setPreferredSize(new Dimension(100, 30));
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(100, 30));
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setPreferredSize(new Dimension(100, 30));
        
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);
        
        // Configurar tabla
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Nombre", "CURP", "Teléfono", "Correo Electrónico"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablePacientes = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablePacientes);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Agregar componentes al panel principal
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Configurar eventos
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
                    pacienteSeleccionado.setCurp((String) tableModel.getValueAt(row, 2));
                    pacienteSeleccionado.setTelefono((String) tableModel.getValueAt(row, 3));
                    pacienteSeleccionado.setCorreoElectronico((String) tableModel.getValueAt(row, 4));
                    
                    txtNombre.setText(pacienteSeleccionado.getNombre());
                    txtCurp.setText(pacienteSeleccionado.getCurp());
                    txtTelefono.setText(pacienteSeleccionado.getTelefono());
                    txtCorreo.setText(pacienteSeleccionado.getCorreoElectronico());
                    
                    btnActualizar.setEnabled(true);
                    btnEliminar.setEnabled(true);
                    btnAgregar.setEnabled(false);
                }
            }
        });
        
        // Estado inicial de los botones
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    // El resto del código permanece igual...
    
    private void cargarPacientes() {
        try {
            // Limpiar la tabla
            tableModel.setRowCount(0);
            
            // Obtener la lista de pacientes
            List<Paciente> pacientes = pacienteService.listarPacientes();
            
            // Agregar los pacientes a la tabla
            for (Paciente paciente : pacientes) {
                tableModel.addRow(new Object[] {
                    paciente.getCurp(),
                    paciente.getNombre(),
                    paciente.getCurp(),
                    paciente.getTelefono(),
                    paciente.getCorreoElectronico()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pacientes: " + e.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarPaciente(ActionEvent e) {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            Paciente paciente = new Paciente();
            paciente.setNombre(txtNombre.getText());
            paciente.setCurp(txtCurp.getText());
            paciente.setTelefono(txtTelefono.getText());
            paciente.setCorreoElectronico(txtCorreo.getText());
            
            pacienteService.crearPaciente(paciente);
            
            JOptionPane.showMessageDialog(this, "Paciente agregado correctamente", 
                                         "Información", JOptionPane.INFORMATION_MESSAGE);
            
            limpiarFormulario();
            cargarPacientes();
            
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar paciente: " + ex.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarPaciente(ActionEvent e) {
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente para actualizar", 
                                         "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarFormulario()) {
            return;
        }
        
        try {
            pacienteSeleccionado.setNombre(txtNombre.getText());
            pacienteSeleccionado.setCurp(txtCurp.getText());
            pacienteSeleccionado.setTelefono(txtTelefono.getText());
            pacienteSeleccionado.setCorreoElectronico(txtCorreo.getText());
            
            pacienteService.actualizarPaciente(pacienteSeleccionado);
            
            JOptionPane.showMessageDialog(this, "Paciente actualizado correctamente", 
                                         "Información", JOptionPane.INFORMATION_MESSAGE);
            
            limpiarFormulario();
            cargarPacientes();
            
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar paciente: " + ex.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarPaciente(ActionEvent e) {
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente para eliminar", 
                                         "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                                                 "¿Está seguro de eliminar el paciente " + pacienteSeleccionado.getNombre() + "?", 
                                                 "Confirmar eliminación", 
                                                 JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                pacienteService.eliminarPaciente(pacienteSeleccionado.getCurp());
                
                JOptionPane.showMessageDialog(this, "Paciente eliminado correctamente", 
                                             "Información", JOptionPane.INFORMATION_MESSAGE);
                
                limpiarFormulario();
                cargarPacientes();
                
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar paciente: " + ex.getMessage(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtCurp.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        pacienteSeleccionado = null;
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnAgregar.setEnabled(true);
    }
    
    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtCurp.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El CURP es obligatorio", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validar formato de CURP (18 caracteres alfanuméricos)
        if (!txtCurp.getText().matches("^[A-Z0-9]{18}$")) {
            JOptionPane.showMessageDialog(this, "El CURP debe tener 18 caracteres alfanuméricos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El teléfono es obligatorio",  "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtTelefono.getText().trim().length() < 10) {
            JOptionPane.showMessageDialog(this, "El teléfono debe de tener al menos 10 caracteres", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtCorreo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El correo electrónico es obligatorio", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validar formato de correo electrónico
        if (!txtCorreo.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "El formato del correo electrónico no es válido", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
}