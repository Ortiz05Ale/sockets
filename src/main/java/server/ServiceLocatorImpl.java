package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import models.CitaService;
import models.MedicoService;
import models.PacienteService;
import models.ServiceLocator;

public class ServiceLocatorImpl extends UnicastRemoteObject implements ServiceLocator {
    private MedicoService medicoService;
    private PacienteService pacienteService;
    private CitaService citaService;
    
    public ServiceLocatorImpl() throws RemoteException {
        super();
        this.medicoService = new MedicoServiceImpl();
        this.pacienteService = new PacienteServiceImpl();
        this.citaService = new CitaServiceImpl();
    }
    
    @Override
    public MedicoService getMedicoService() throws RemoteException {
        return medicoService;
    }
    
    @Override
    public PacienteService getPacienteService() throws RemoteException {
        return pacienteService;
    }
    
    @Override
    public CitaService getCitaService() throws RemoteException {
        return citaService;
    }
}