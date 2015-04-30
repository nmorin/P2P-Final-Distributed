import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.*;

import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.util.ArrayList;

import java.net.ServerSocket;
import java.net.Socket;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements PeerInterface {

    private static final int PIECE_SIZE = 6400;

	private ServerSocket serverSocket;
    private static PeerInterface boundPeerStub;
    private static TrackerInterface leadTrackerStub;
    private final int ID_MAX = 999999999;
    private final int ID_MIN = 100000000;
    private static List<String> peerList;
    private static Map<String, PeerInterface> peerStubs;
    private List<String> filesAlreadySplit;


	public Peer() {
        super();

        peerList = new ArrayList<String>(); 
        peerStubs = new HashMap<String, PeerInterface>();
	}

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

    public ArrayList<Integer> requestPieceInfo(String fileName) {
        return null;
    }

    private String createRandomID() {
        Random rand = new Random();
        int id = rand.nextInt((ID_MAX - ID_MIN) + 1) + ID_MIN;
        String idString = Integer.toString(id);
        return idString;
    }

    private static void createAndBindSelf(int myPortNum, String myName) {
         try {
            // Commented out names to perform hardcoded testing
            // String name = createRandomID();
            // String name = String.valueOf(myPortNum);
            Peer peer = new Peer();
            PeerInterface peerStub = (PeerInterface) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.createRegistry(myPortNum);
            registry.bind(myName, peerStub);
            System.out.println("Binding complete");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void connectToPeer(String peerName, int peerPort) {
        try {
            // Fix below statment
            // if (peerStubs.contains(peerName)) { return; }

            Registry theirReg = LocateRegistry.getRegistry("localhost", peerPort);
            boundPeerStub = (PeerInterface) theirReg.lookup(peerName);
            peerStubs.put(peerName, boundPeerStub);
            System.out.println("Found peer " + peerName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int divideFile(int fileSize) {

        return 1;
    }

    /*
     * Connects to head tracker and asks for peers that have a certain file.
     * Tracker returns a list of peerInfo strings with "peerName,portNo"
     * I parse this list, and establish connections to peers with whom I have
     *     not already established a connection, then ask them what pieces
     *     they have that I still need.
     * Based on what 
     */
    private static void makeRequest(String trackerID, String fileName) {
        try {
            // connect to tracker
            ArrayList<String> peersWithFile = new ArrayList<String>();
            peersWithFile.addAll(leadTrackerStub.query(fileName));


            int fileSize = 0; //bytes
            if (!peersWithFile.isEmpty()) {
                fileSize = Integer.parseInt(peersWithFile.get(0));
                peersWithFile.remove(0);
            }

            if (peersWithFile.isEmpty()) {
                return;
            }

            // returns num totalpieces
            int numFilePieces = divideFile(fileSize);

            ArrayList<ArrayList<String>> pieceBreakdown = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < numFilePieces; i++) {
                ArrayList<String> temp = new ArrayList<String>();
                temp.add("PLACEHOLDER");
                pieceBreakdown.add(temp);
            }

            for (String peerInfo : peersWithFile) {
                int commaIndex = peerInfo.indexOf(",");
                if (commaIndex == -1) { return; } //error

                String peerName = peerInfo.substring(commaIndex);
                String portNo = peerInfo.substring(commaIndex+1, peerInfo.length());
                connectToPeer(peerName, Integer.parseInt(portNo)); //establishes connections

                ArrayList<Integer> peerHasMe = new ArrayList<Integer>();
                peerHasMe.addAll(peerStubs.get(peerName).requestPieceInfo(fileName));

                for (Integer piece : peerHasMe) {
                    pieceBreakdown.get((int)piece).add(peerName);
                }
            }

            RandomAccessFile outFile = new RandomAccessFile("OUTPUT"+fileName, "rw");

            int counter = -1;
            for (ArrayList<String> peersWhoHavePiece : pieceBreakdown) {
                counter++;

                // always should have the "placeholder" in position 0, so start with 1
                if (peersWhoHavePiece.size() <= 1) {
                    System.out.println("NO PERSON HAS PIECE FOR FILE "+fileName);
                    continue;
                }

                // temp testing just go with the first peer in the list:
                byte[] answer = askForFilePiece(peersWhoHavePiece.get(1), fileName, counter);
                writeBytes(answer, outFile, counter);
            }

            outFile.close();

        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
        
    }

    private static void writeBytes(byte[] data, RandomAccessFile fileName, int piece) {
        try {
            // first get offset with piece:
            int offset = piece * PIECE_SIZE;
            fileName.seek(offset);
            fileName.write(data);
        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
    }


    /*
     * Asks a peer for a specific piece of the file. We have already determined
     * that this peer has the file, but we will use a timeout and check on the 
     * peer end to make sure this information is correct.
     */
    private static byte[] askForFilePiece(String peerName, String fileName, int piece) {
        try {

            // I NEED TO FILL IN SORRY -MEGZ


            // byte[] result;
            // System.out.println("Looking for file " + fileName);
            // result = peerStubs.get(peerName).requestFile(fileName);

            // File outFile = new File("result.txt");
            // FileOutputStream fileOutput = new FileOutputStream(outFile, true);
            // fileOutput.write(result);
            // fileOutput.close();
            // System.out.println("Got file");

        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
        return null;
    }

    /* to run: 
    java Peer localhost <your name> <your port>
    to connect to more peers: first start them on their ports, then enter command:
    connect <their name> <their port>
    to request file, enter command:
    request <their name>
    */
	public static void main(String[] argv) {
        String host = (argv.length < 1) ? "localhost" : argv[0];
        int myPortNum = (argv.length < 2) ? 5000 : Integer.parseInt(argv[1]);
        String myName = (argv.length < 3) ? "Howard" : (argv[2]);

        String fileName = "YOLO.txt";

        System.out.println("My portnum: " + myPortNum + "\n My name: " + myName + "\n");

        createAndBindSelf(myPortNum, myName);

        Scanner keyboard = new Scanner(System.in);
        /* Infinite loop that will query the user to input commands. To
        exit, type 'exit'. */
        while (true) {
            System.out.println("Enter a command");
            String command = keyboard.nextLine();
            String parsedRequest[] = command.split(" ");
            String secondPartOfRequest = "";

            // Add a peer
            if (parsedRequest[0].equals("connect")) {
                String name = parsedRequest[1];
                int port = Integer.parseInt(parsedRequest[2]);
                connectToPeer(name, port);
            }

            // Request a file
            else if (parsedRequest[0].equals("request")) {
                String peerName = parsedRequest[1];
                makeRequest(peerName, fileName);
            }

            else if (parsedRequest[0].equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
        }
        
    }

    private void splitFile(String fileName) {
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

    private void mergeFile(String fileName, int numPieces) {
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
    			fileInput = new FileInputStream(file);
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







