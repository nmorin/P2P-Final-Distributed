import java.util.*;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Tracker implements TrackerInterface {

	private static Map<String, TrackerFile> trackerFiles;

	public Tracker() {
        super();
        this.trackerFiles = new HashMap<String, TrackerFile>();

	}

	public List<String> query(String fileName) {
		List<String> response = new ArrayList<String>();
		if (trackerFiles.containsKey(fileName)) {
			TrackerFile temp = trackerFiles.get(fileName);
			response = temp.getPeerList();

		} else {
			response = null;
		}
		return response;
	}

	public void seedFile(String fileName, String peerName, int peerPort, int fileSize, int numPieces) {
		TrackerFile temp = new TrackerFile(fileName, peerName, peerPort, fileSize, numPieces);
		trackerFiles.put(fileName, temp);
	}

	private static void createAndBindSelf(int myPortNum, String myName) {
         try {
            Tracker tracker = new Tracker();
            TrackerInterface trackerStub = (TrackerInterface) UnicastRemoteObject.exportObject(tracker, 0);
            Registry registry = LocateRegistry.createRegistry(myPortNum);
            registry.bind(myName, trackerStub);
            System.out.println("Tracker binding complete");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public static void main(String[] argv) {
		// Constant, public name and port number for tracker
		int myPortNum = 6666; 
		String myName = "Tracker";
		createAndBindSelf(myPortNum, myName);

	}

	
	
}