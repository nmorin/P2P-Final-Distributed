


public class PeerStruct {

	private static String name;
	private static int portNum;

	public PeerStruct(String name_, int portNum_) {
		name = name_;
		portNum = portNum_;
	}

	public String getName() { return name; }

	public int getPortNum() { return portNum; }

}