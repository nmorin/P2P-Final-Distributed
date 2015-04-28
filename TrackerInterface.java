import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote {
        public String query(String fileName) throws RemoteException;
        public void informOfNewUpload(String fileName) throws RemoteException;
}