package bel.dm.server.RMI;

import bel.dm.server.services.MyService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Component
@AllArgsConstructor
public class RMIServer {
    private final MyService myService;
    private final Logger LOG = LoggerFactory.getLogger(RMIServer.class);
    @EventListener(ApplicationReadyEvent.class)
    public void startRMI(){
        try {
            final String BINDING_NAME = "rmi.testtask";
            final Registry registry = LocateRegistry.createRegistry(5335);
            Remote stub = UnicastRemoteObject.exportObject(myService, 0);
            registry.bind(BINDING_NAME, stub);
        } catch (Exception e){
            LOG.error("RMI has failed to start: " + e.getMessage());
        }
    }
}
