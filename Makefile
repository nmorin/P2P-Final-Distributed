GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Peer.java\
	PeerFile.java\
	PeerInterface.java\
	Tracker.java\
	TrackerFile.java\
	TrackerInterface.java
 
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *~
