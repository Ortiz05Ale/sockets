/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package models;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Asus
 */
public interface CitaService extends Remote {
    Cita crearCita(Cita cita) throws RemoteException;
    Cita obtenerCita(int id) throws RemoteException;
    List<Cita> listarCitas() throws RemoteException;
    List<Cita> listarCitasPorMedico(String medicoId) throws RemoteException;
    List<Cita> listarCitasPorPaciente(String pacienteId) throws RemoteException;
    boolean actualizarCita(Cita cita) throws RemoteException;
    boolean eliminarCita(int id) throws RemoteException;
}