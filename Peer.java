import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Peer implements PeerInterface {

	private ServerSocket serverSocket;
    private final int ID_MAX = 9999999999;
    private final int ID_MIN = 1000000000;
    private List<String> filesAlreadySplit;


	public Peer() {
        filesAlreadySplit = new ArrayList<String>(); 
		super();
	}

    public byte[] requestFile(String fileName, String peerId, List<Integer> piecesLeftToDownload) {
        if (!filesAlreadySplit.contains(fileName)) {
            splitFile(fileName);
        }
        FileInputStream fileInput = new FileInputStream();
        byte[] fileBytes = new byte[(int) file.length()];
        bytesRead = fileInput.read(fileBytes, 0, (int) file.length());
        assert(bytesRead == fileBytes.length);
        assert(bytesRead == (int) file.length());




    }

    private String createRandomID() {
        Random rand = new Random();
        int id = rand.nextInt((ID_MAX - ID_MIN) + 1) + min;
        String idString = Integer.toString(id);
        return idString;
    }

    private void createAndBindSelf(int portNum) {
         try {
            String name = createRandomID();
            Peer peer = new Peer();
            PeerInterface peerStub = (Peer) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.createRegistry(portNumOut);
            registry.bind(name, peerStub);
            System.out.println("Binding complete");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public static void main(String[] argv) {
        String host = (argv.length < 1) ? "localhost" : argv[0];
        int portNumReceive = (argv.length < 2) ? 5000 : argv[1];
        int portNumOut = (argv.length < 3) ? 5001 : argv[2];
        String fileName = "TestFileRabbit.txt";

        createAndBindSelf(portNumOut);


        

        
    }


    private static void sendFile(String fileName) {

    	File sendingFile = new File(fileName);

    	while (true) {
    		try {
    			System.out.println("Waiting for client on port " + serverSocket.getLocalPort());
    			
    			Socket incomingClient = serverSocket.accept();
    			System.out.println("Connected to " + incomingClient.getRemoteSocketAddress());

    			byte[] byteArray = new byte[(int) myFile.length()];
    			BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(sendingFile));
    			fileInput.read(byteArray, 0, byteArray.length);
    			OutputStream output = incomingClient.getOutputStream();
    			output.write(byteArray, 0, byteArray.length);
    			output.flush();
    			incomingClient.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}


    }

    private static void receiveFile(Socket socket) {
    	String fileName = "Received_TestFileRabbit.txt";
    	byte[] byteArray = new byte[1024];
    	BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	BufferedReader output = new BufferedReader(new OutputStreamReader(socket.getOutputStream()));

    	FileOutputStream fileOut = new FileOutputStream(fileName);
    	BufferedOutputStream bufferedFileOut = new BufferedOutputStream(fileOut);
    	int bytesRead = input.read(byteArray, 0, byteArray.length);
    	bufferedFileOut.write(byteArray, 0, bytesRead);

    	bufferedFileOut.close();
    }

    private static void splitFile(String fileName) {
    	File inputFile = new File(fileName);
    	FileInputStream inputStream;
    	String newFileName;
    	FileOutputStream filePiece;
    	int fileSize = (int) inputFile.length();
    	byte pieceSize = 50;
    	int numPieces = 0, read = 0, readLength = pieceSize;
    	byte[] bytePiecePart;

    	try {
    		inputStream = new FileInputStream(inputFile);
    		while (fileSize > 0) {
    			if (fileSize <= pieceSize) {
    				readLength = fileSize;
    			}

    			bytePiecePart = new byte[readLength];
    			read = inputStream.read(bytePiecePart, 0, readLength);
    			fileSize -= read;

    			assert (read == bytePiecePart.length);
    			numPieces++;
    			newFileName = fileName + ".part" + Integer.toString(numPieces - 1);
    			filePiece = new FileOutputStream(new File(newFileName));
    			filePiece.write(bytePiecePart);
    			filePiece.flush();
    			filePiece.close();
    			bytePiecePart = null;
    			filePiece = null;
    		}
    		inputStream.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

        filesAlreadySplit.add(fileName);

    }

    private static void mergeFile(String fileName, int numPieces) {
    	File outFile = new File(fileName);
    	FileOutputStream fileOutput;
    	FileInputStream fileInput;
    	byte[] fileBytes;
    	int bytesRead = 0;
    	List<File> list = new ArrayList<File>();
    	for (int i = 0; i < numPieces; i++) {
    		list.add(new File(fileName + ".part" + Integer.toString(i)));
    	}

    	try {
    		fileOutput = new FileOutputStream(outFile, true);
    		for (File file : list) {
    			fileInput = new FileInputStream();
    			fileBytes = new byte[(int) file.length()];
    			bytesRead = fileInput.read(fileBytes, 0, (int) file.length());
    			assert(bytesRead == fileBytes.length);
    			assert(bytesRead == (int) file.length());
    			fileOutput.write(fileBytes);
    			fileOutput.flush();
    			fileBytes = null;
    			fileInput.close();
    			fileInput = null;
    		}
    		fileOutput.close();
    		fileOutput = null;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	


}







