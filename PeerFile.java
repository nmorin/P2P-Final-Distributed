import java.util.*;

public class PeerFile {

	private static String fileName;
	private static ArrayList<String> peersWithFile;
	private static int numPieces;
	private static ArrayList<Integer> completePieces;
	private static ArrayList<Integer> downloadingPieces;
	private static ArrayList<Integer> neededPieces;
	private static int size;

	// Called after initiating a file request
	public PeerFile(String fileName, int numPieces, ArrayList<String> peersWithFile, int size) {
		this.fileName = fileName;
		this.size = size;
		this.numPieces = numPieces;
		this.peersWithFile = new ArrayList<String>();
		this.peersWithFile.addAll(peersWithFile);
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		for (int i = 0; i < numPieces; i++) {
			this.neededPieces.add((Integer)i);
		}
	}

	// Called when seeding a file
	public PeerFile(String fileName, int numPieces, int size) {
		this.fileName = fileName;
		this.numPieces = numPieces;
		this.size = size;
		this.peersWithFile = new ArrayList<String>();
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		for (int i = 0; i < numPieces; i++) {
			this.completePieces.add((Integer)i);
		}
	}

	public ArrayList<Integer> getPiecesNeeded() { return neededPieces; }

	public ArrayList<Integer> getCompletePieces() { return completePieces; }

	public ArrayList<String> getPeerList() { return peersWithFile; }

	public int getSize() { return size; }

	public int getNumPieces() { return numPieces; }


}

