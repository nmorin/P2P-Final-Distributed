import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote {
        public byte[] requestFile(String fileName) throws RemoteException;
}