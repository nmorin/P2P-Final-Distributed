import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote {
        public String lookUp(int itemNumber) throws RemoteException;
        public String search(String topic) throws RemoteException;
        public String buy(int itemNumber) throws RemoteException;
}