import java.util.*;

public class PeerFile {

	private static String fileName;
	private static ArrayList<String> peersWithFile;
	private static int numPieces;
	private static ArrayList<Integer> completePieces;
	private static ArrayList<Integer> neededPieces;

	public PeerFile(String fileName, int numPieces, ArrayList<String> peersWithFile) {
		this.fileName = fileName;
		this.numPieces = numPieces;
		this.peersWithFile = new ArrayList<String>();
		this.peersWithFile.addAll(peersWithFile);
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		for (int i = 0; i < numPieces; i++) {
			this.neededPieces.add(i);
		}
	}

	public ArrayList<Integer> getPiecesNeeded() { return neededPieces; }

	public ArrayList<Integer> getCompletePieces() { return completePieces; }

	public ArrayList<String> getPeerList() { return peersWithFile; }


}

