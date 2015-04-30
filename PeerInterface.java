import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PeerInterface extends Remote {
        public byte[] requestFile(String fileName) throws RemoteException;
        public ArrayList<Integer> requestPieceInfo(String fileName) throws RemoteException;
}