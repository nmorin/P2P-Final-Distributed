import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import java.rmi.*;
import java.util.ArrayList;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements PeerInterface {

	private ServerSocket serverSocket;
    private static PeerInterface boundPeerStub;
    private final int ID_MAX = 999999999;
    private final int ID_MIN = 100000000;
    // private List<String> filesAlreadySplit;


	public Peer() {
        // filesAlreadySplit = new ArrayList<String>(); 
		super();
	}

    // public byte[] requestFile(String fileName, String peerId, List<Integer> piecesLeftToDownload) {
    //     if (!filesAlreadySplit.contains(fileName)) {
    //         splitFile(fileName);
    //     }
    //     FileInputStream fileInput = new FileInputStream();
    //     byte[] fileBytes = new byte[(int) file.length()];
    //     bytesRead = fileInput.read(fileBytes, 0, (int) file.length());
    //     assert(bytesRead == fileBytes.length);
    //     assert(bytesRead == (int) file.length());

    // }

    public byte[] requestFile(String fileName) {
        try {
            int bytesRead;
            FileInputStream fileInput = new FileInputStream(fileName);
            byte[] fileBytes = new byte[(int)(new File(fileName).length())];
            bytesRead = fileInput.read(fileBytes);
            assert(bytesRead == fileBytes.length);
            return fileBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createRandomID() {
        Random rand = new Random();
        int id = rand.nextInt((ID_MAX - ID_MIN) + 1) + ID_MIN;
        String idString = Integer.toString(id);
        return idString;
    }

    private static void createAndBindSelf(int myPortNum, int theirPortNum) {
         try {
            // Bind hardcoded peer
            if (myPortNum != 5000) {
                String nameOfPeer = Integer.toString(theirPortNum);
                Registry theirReg = LocateRegistry.getRegistry("localhost", theirPortNum);
                boundPeerStub = (PeerInterface) theirReg.lookup(nameOfPeer);
                System.out.println("Found peer " + nameOfPeer);
            }

            // String name = createRandomID();
            String name = String.valueOf(myPortNum);
            Peer peer = new Peer();
            PeerInterface peerStub = (PeerInterface) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.createRegistry(myPortNum);
            registry.bind(name, peerStub);
            System.out.println("Binding complete");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public static void main(String[] argv) {
        String host = (argv.length < 1) ? "localhost" : argv[0];
        int myPortNum = (argv.length < 2) ? 5000 : Integer.parseInt(argv[1]);
        int theirPortNum = (argv.length < 3) ? 5001 : Integer.parseInt(argv[2]);
        String fileName = "YOLO.txt";
        System.out.println("My portnum: " + myPortNum + "\n Their port num: " + theirPortNum + "\n");

        createAndBindSelf(myPortNum, theirPortNum);

        if (myPortNum != 5000) {
            try {
                byte[] result;
                System.out.println("Looking for file YOLO.txt");
                result = boundPeerStub.requestFile("YOLO.txt");

                File outFile = new File("result.txt");
                FileOutputStream fileOutput = new FileOutputStream(outFile, true);
                fileOutput.write(result);
                fileOutput.close();
                System.out.println("Here");

            } catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();
            }
        }
        
    }


    // private static void sendFile(String fileName) {

    // 	File sendingFile = new File(fileName);

    // 	while (true) {
    // 		try {
    // 			System.out.println("Waiting for client on port " + serverSocket.getLocalPort());
    			
    // 			Socket incomingClient = serverSocket.accept();
    // 			System.out.println("Connected to " + incomingClient.getRemoteSocketAddress());

    // 			byte[] byteArray = new byte[(int) myFile.length()];
    // 			BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(sendingFile));
    // 			fileInput.read(byteArray, 0, byteArray.length);
    // 			OutputStream output = incomingClient.getOutputStream();
    // 			output.write(byteArray, 0, byteArray.length);
    // 			output.flush();
    // 			incomingClient.close();
    // 		} catch (Exception e) {
    // 			e.printStackTrace();
    // 		}
    // 	}
    // }

    // private static void receiveFile(Socket socket) {
    // 	String fileName = "Received_TestFileRabbit.txt";
    // 	byte[] byteArray = new byte[1024];
    // 	BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // 	BufferedReader output = new BufferedReader(new OutputStreamReader(socket.getOutputStream()));

    // 	FileOutputStream fileOut = new FileOutputStream(fileName);
    // 	BufferedOutputStream bufferedFileOut = new BufferedOutputStream(fileOut);
    // 	int bytesRead = input.read(byteArray, 0, byteArray.length);
    // 	bufferedFileOut.write(byteArray, 0, bytesRead);

    // 	bufferedFileOut.close();
    // }

    // private static void splitFile(String fileName) {
    // 	File inputFile = new File(fileName);
    // 	FileInputStream inputStream;
    // 	String newFileName;
    // 	FileOutputStream filePiece;
    // 	int fileSize = (int) inputFile.length();
    // 	byte pieceSize = 50;
    // 	int numPieces = 0, read = 0, readLength = pieceSize;
    // 	byte[] bytePiecePart;

    // 	try {
    // 		inputStream = new FileInputStream(inputFile);
    // 		while (fileSize > 0) {
    // 			if (fileSize <= pieceSize) {
    // 				readLength = fileSize;
    // 			}

    // 			bytePiecePart = new byte[readLength];
    // 			read = inputStream.read(bytePiecePart, 0, readLength);
    // 			fileSize -= read;

    // 			assert (read == bytePiecePart.length);
    // 			numPieces++;
    // 			newFileName = fileName + ".part" + Integer.toString(numPieces - 1);
    // 			filePiece = new FileOutputStream(new File(newFileName));
    // 			filePiece.write(bytePiecePart);
    // 			filePiece.flush();
    // 			filePiece.close();
    // 			bytePiecePart = null;
    // 			filePiece = null;
    // 		}
    // 		inputStream.close();
    // 	} catch (IOException e) {
    // 		e.printStackTrace();
    // 	}

    //     filesAlreadySplit.add(fileName);

    // }

    // private static void mergeFile(String fileName, int numPieces) {
    // 	File outFile = new File(fileName);
    // 	FileOutputStream fileOutput;
    // 	FileInputStream fileInput;
    // 	byte[] fileBytes;
    // 	int bytesRead = 0;
    // 	List<File> list = new ArrayList<File>();
    // 	for (int i = 0; i < numPieces; i++) {
    // 		list.add(new File(fileName + ".part" + Integer.toString(i)));
    // 	}

    // 	try {
    // 		fileOutput = new FileOutputStream(outFile, true);
    // 		for (File file : list) {
    // 			fileInput = new FileInputStream();
    // 			fileBytes = new byte[(int) file.length()];
    // 			bytesRead = fileInput.read(fileBytes, 0, (int) file.length());
    // 			assert(bytesRead == fileBytes.length);
    // 			assert(bytesRead == (int) file.length());
    // 			fileOutput.write(fileBytes);
    // 			fileOutput.flush();
    // 			fileBytes = null;
    // 			fileInput.close();
    // 			fileInput = null;
    // 		}
    // 		fileOutput.close();
    // 		fileOutput = null;
    // 	} catch (Exception e) {
    // 		e.printStackTrace();
    // 	}
    // }
	


}







