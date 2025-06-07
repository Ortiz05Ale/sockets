package server;

import services.Cita;
import services.CitaService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaServiceImpl extends UnicastRemoteObject implements CitaService {
    
    public CitaServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public Cita crearCita(Cita cita) throws RemoteException {
        String sql = "INSERT INTO cita (fecha, hora, motivo, medico_asignado, paciente_asignado) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, new java.sql.Date(cita.getFecha().getTime()));
            stmt.setString(2, cita.getHora());
            stmt.setString(3, cita.getMotivo());
            stmt.setString(4, cita.getMedicoAsignado());
            stmt.setString(5, cita.getPacienteAsignado());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación de la cita falló, no se insertaron filas.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cita.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación de la cita falló, no se obtuvo el ID.");
                }
            }
            
            return cita;
            
        } catch (SQLException e) {
            System.err.println("Error al crear cita: " + e.getMessage());
            throw new RemoteException("Error al crear cita", e);
        }
    }
    
    @Override
    public Cita obtenerCita(int id) throws RemoteException {
        String sql = "SELECT c.*, m.nombre as nombre_medico, p.nombre as nombre_paciente " +
                     "FROM cita c " +
                     "JOIN medico m ON c.medico_asignado = m.cedula " +
                     "JOIN paciente p ON c.paciente_asignado = p.curp " +
                     "WHERE c.cita_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCita(rs);
                } else {
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cita: " + e.getMessage());
            throw new RemoteException("Error al obtener cita", e);
        }
    }
    
    @Override
    public List<Cita> listarCitas() throws RemoteException {
        String sql = "SELECT c.*, m.nombre as nombre_medico, p.nombre as nombre_paciente " +
                     "FROM cita c " +
                     "JOIN medico m ON c.medico_asignado = m.cedula " +
                     "JOIN paciente p ON c.paciente_asignado = p.curp " +
                     "ORDER BY c.fecha, c.hora";
        
        List<Cita> citas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                citas.add(mapResultSetToCita(rs));
            }
            
            return citas;
            
        } catch (SQLException e) {
            System.err.println("Error al listar citas: " + e.getMessage());
            throw new RemoteException("Error al listar citas", e);
        }
    }
    
    @Override
    public List<Cita> listarCitasPorMedico(String medicoId) throws RemoteException {
        String sql = "SELECT c.*, m.nombre as nombre_medico, p.nombre as nombre_paciente " +
                     "FROM cita c " +
                     "JOIN medico m ON c.medico_asignado = m.cedula " +
                     "JOIN paciente p ON c.paciente_asignado = p.curp " +
                     "WHERE c.medico_asignado = ? " +
                     "ORDER BY c.fecha, c.hora";
        
        List<Cita> citas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medicoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    citas.add(mapResultSetToCita(rs));
                }
            }
            
            return citas;
            
        } catch (SQLException e) {
            System.err.println("Error al listar citas por médico: " + e.getMessage());
            throw new RemoteException("Error al listar citas por médico", e);
        }
    }
    
    @Override
    public List<Cita> listarCitasPorPaciente(String pacienteId) throws RemoteException {
        String sql = "SELECT c.*, m.nombre as nombre_medico, p.nombre as nombre_paciente " +
                     "FROM cita c " +
                     "JOIN medico m ON c.medico_asignado = m.cedula " +
                     "JOIN paciente p ON c.paciente_asignado = p.curp " +
                     "WHERE c.paciente_asignado = ? " +
                     "ORDER BY c.fecha, c.hora";
        
        List<Cita> citas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pacienteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    citas.add(mapResultSetToCita(rs));
                }
            }
            
            return citas;
            
        } catch (SQLException e) {
            System.err.println("Error al listar citas por paciente: " + e.getMessage());
            throw new RemoteException("Error al listar citas por paciente", e);
        }
    }
    
    @Override
    public boolean actualizarCita(Cita cita) throws RemoteException {
        String sql = "UPDATE cita SET fecha = ?, hora = ?, motivo = ?, medico_asignado = ?, paciente_asignado = ? WHERE cita_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(cita.getFecha().getTime()));
            stmt.setString(2, cita.getHora());
            stmt.setString(3, cita.getMotivo());
            stmt.setString(4, cita.getMedicoAsignado());
            stmt.setString(5, cita.getPacienteAsignado());
            stmt.setInt(6, cita.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cita: " + e.getMessage());
            throw new RemoteException("Error al actualizar cita", e);
        }
    }
    
    @Override
    public boolean eliminarCita(int id) throws RemoteException {
        String sql = "DELETE FROM cita WHERE cita_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
            throw new RemoteException("Error al eliminar cita", e);
        }
    }
    
    private Cita mapResultSetToCita(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setId(rs.getInt("cita_id"));
        cita.setFecha(rs.getDate("fecha"));
        cita.setHora(rs.getString("hora"));
        cita.setMotivo(rs.getString("motivo"));
        cita.setMedicoAsignado(rs.getString("medico_asignado"));
        cita.setPacienteAsignado(rs.getString("paciente_asignado"));
        cita.setNombreMedico(rs.getString("nombre_medico"));
        cita.setNombrePaciente(rs.getString("nombre_paciente"));
        return cita;
    }
}