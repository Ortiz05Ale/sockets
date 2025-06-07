package server;

import services.Paciente;
import services.PacienteService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteServiceImpl extends UnicastRemoteObject implements PacienteService {
    
    public PacienteServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public Paciente crearPaciente(Paciente paciente) throws RemoteException {
        String sql = "INSERT INTO paciente (nombre, curp, telefono, correo_electronico) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getCurp());
            stmt.setString(3, paciente.getTelefono());
            stmt.setString(4, paciente.getCorreoElectronico());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del paciente falló, no se insertaron filas.");
            }
            
            return paciente;
            
        } catch (SQLException e) {
            System.err.println("Error al crear paciente: " + e.getMessage());
            throw new RemoteException("Error al crear paciente", e);
        }
    }
    
    @Override
    public Paciente obtenerPaciente(int id) throws RemoteException {
        String sql = "SELECT * FROM paciente WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaciente(rs);
                } else {
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener paciente: " + e.getMessage());
            throw new RemoteException("Error al obtener paciente", e);
        }
    }
    
    @Override
    public List<Paciente> listarPacientes() throws RemoteException {
        String sql = "SELECT * FROM paciente ORDER BY nombre";
        List<Paciente> pacientes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pacientes.add(mapResultSetToPaciente(rs));
            }
            
            return pacientes;
            
        } catch (SQLException e) {
            System.err.println("Error al listar pacientes: " + e.getMessage());
            throw new RemoteException("Error al listar pacientes", e);
        }
    }
    
    @Override
    public boolean actualizarPaciente(Paciente paciente) throws RemoteException {
        String sql = "UPDATE paciente SET nombre = ?, curp = ?, telefono = ?, correo_electronico = ? WHERE curp = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getCurp());
            stmt.setString(3, paciente.getTelefono());
            stmt.setString(4, paciente.getCorreoElectronico());
            stmt.setString(5, paciente.getCurp());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
            throw new RemoteException("Error al actualizar paciente", e);
        }
    }
    
    @Override
    public boolean eliminarPaciente(String curp) throws RemoteException {
        String sql = "DELETE FROM paciente WHERE CURP = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, curp);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar paciente: " + e.getMessage());
            throw new RemoteException("Error al eliminar paciente", e);
        }
    }
    
    private Paciente mapResultSetToPaciente(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente();
        paciente.setNombre(rs.getString("nombre"));
        paciente.setCurp(rs.getString("curp"));
        paciente.setTelefono(rs.getString("telefono"));
        paciente.setCorreoElectronico(rs.getString("correo_electronico"));
        return paciente;
    }
}