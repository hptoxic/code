// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import OT.OTExtSender;
import OT.Sender;
import Utils.StopWatch;
import YaoGC.Circuit;
import YaoGC.Wire;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ProgServer extends Program {

    private ServerSocket sock = null;              // original server socket
    private Socket clientSocket = null;              // socket created by accept

    protected Sender snder;
    protected int otNumOfPairs;
    protected int otMsgBitLength = Wire.labelBitLength;

    public void run() throws Exception {
    	//step 1 in this classs
        create_socket_and_listen();
        //init()  createCircuit  initializeOT()
        //runonLine
        super.run();

        cleanup();
    }

    public void runOffline() throws Exception {
        create_socket_and_listen();
        init();
    }

    protected void init() throws Exception {
        Program.iterCount = ProgCommon.ois.readInt();
        System.out.println("Iterations: " + Program.iterCount);
        //in Program class
        super.init();
    }

    /**
     * Establish socket connection with client
     */
    private void create_socket_and_listen() throws Exception {
        sock = new ServerSocket(EstimateNConfig.socketPort);            // create socket and bind to port
        System.out.println("waiting for client to connect");
        clientSocket = sock.accept();                   // wait for client to connect
        System.out.println("client has connected");

        CountingOutputStream cos = new CountingOutputStream(clientSocket.getOutputStream());
        CountingInputStream cis = new CountingInputStream(clientSocket.getInputStream());

        ProgCommon.oos = new ObjectOutputStream(cos);
        ProgCommon.ois = new ObjectInputStream(cis);

        StopWatch.cos = cos;
        StopWatch.cis = cis;
    }

    public void cleanup() throws Exception {
        ProgCommon.oos.close();                          // close everything
        ProgCommon.ois.close();
        clientSocket.close();
        sock.close();
    }

    protected void initializeOT() throws Exception {
        otNumOfPairs = ProgCommon.ois.readInt();

        snder = new OTExtSender(otNumOfPairs, otMsgBitLength, ProgCommon.ois, ProgCommon.oos);
        StopWatch.taskTimeStamp("OT preparation");
    }

    /**
     * Create circuit (implement abstract method for parent)
     */
    protected void createCircuits() throws Exception {
        Circuit.isForGarbling = true;
        Circuit.setIOStream(ProgCommon.ois, ProgCommon.oos);
        for (int i = 0; i < ProgCommon.ccs.length; i++) {
            ProgCommon.ccs[i].build();
        }

        StopWatch.taskTimeStamp("circuit preparation");
    }
}