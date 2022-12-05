package share.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Statistics extends Remote {
    /**
     * Resets service statistics
     */
    void reset() throws RemoteException;

    /**
     * Retrieves amount of "getAmount(Integer id)" requests processed by server per second
     */
    Double getRVelocity() throws RemoteException;

    /**
     * Retrieves overall amount of "getAmount(Integer id)" requests received by the server
     */
    Integer getRRequestAmount() throws RemoteException;

    /**
     * Retrieves amount of "addAmount(Integer id, Long amount)" requests processed by server per second
     */
    Double getWVelocity() throws RemoteException;

    /**
     * Retrieves overall amount of "addAmount(Integer id, Long amount)" requests received by the server
     */
    Integer getWRequestAmount() throws RemoteException;
}
