import java.util.*;

public class PeerFile {

	private static String fileName;
	private static List<String> peersWithFile;
	private static int numPieces;
	private static List<int> completePieces;
	private static List<int> neededPieces;

	public PeerFile(String fileName, int numPieces, List<String> peersWithFile) {
		this.fileName = fileName;
		this.numPieces = numPieces;
		this.peersWithFile = new ArrayList<String>();
		this.peersWithFile.addall(peersWithFile);
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		for (int i = 0; i < numPieces; i++) {
			this.neededPieces.add(i);
		}
	}

	public List<Integer> getPiecesNeeded() { return neededPieces; }

	public List<Integer> getCompletePieces() { return completePieces; }

	public List<String> getPeerList() { return peersWithFile; }


}

