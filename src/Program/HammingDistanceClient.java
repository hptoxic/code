// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import Utils.StopWatch;
import Utils.Utils;
import YaoGC.State;
import YaoGC.Wire;

import java.math.BigInteger;


/*
 * run()
 * {
 * 	init();
 *  runOnline();//run execute() for a given time
 * }
 * 
 * init()
 * {
 * createCircuits();
 * initializeOT();
 * }
 */
/*
 *  protected void execute() throws Exception {
        execTransfer();
        execCircuit();
        interpretResult();
    }
 */
public class HammingDistanceClient extends ProgClient {
    private BigInteger cBits;
    private BigInteger[] sBitslbs, cBitslbs;

    private State outputState;

    public HammingDistanceClient(BigInteger bv, int length) {
        cBits = bv;
        HammingDistanceCommon.bitVecLen = length;
    }
    
    protected void init() throws Exception {
        HammingDistanceCommon.bitVecLen = HammingDistanceCommon.ois.readInt();
        //typtically it new the subCircuit object
        HammingDistanceCommon.initCircuits();

        otNumOfPairs = HammingDistanceCommon.bitVecLen;
        //the super init() 
        //1 build the create_socket_and_connect();
        //2 createCircuit execute .build() function for each subCircuit
        //3 prepare OT  writein otNumOfPairs  initialize the OTExtReceiver object rcver
        super.init();
    }

    protected void execTransfer() throws Exception {
    	//set a BigInteger for each bit
    	//in this example HammingDistanceCommon.bitVecLen is 900
        sBitslbs = new BigInteger[HammingDistanceCommon.bitVecLen];

        for (int i = 0; i < HammingDistanceCommon.bitVecLen; i++) {
        	//LabelBitLength is 80
            int bytelength = (Wire.labelBitLength - 1) / 8 + 1;
            sBitslbs[i] = Utils.readBigInteger(bytelength, HammingDistanceCommon.ois);
        }
        StopWatch.taskTimeStamp("receiving labels for peer's inputs");

        cBitslbs = new BigInteger[HammingDistanceCommon.bitVecLen];
        //this.choice = choice;
        rcver.execProtocol(cBits);
        cBitslbs = rcver.getData();
        StopWatch.taskTimeStamp("receiving labels for self's inputs");
    }

    protected void execCircuit() throws Exception {
        outputState = HammingDistanceCommon.execCircuit(sBitslbs, cBitslbs);
    }


    protected void interpretResult() throws Exception {
        HammingDistanceCommon.oos.writeObject(outputState.toLabels());
        HammingDistanceCommon.oos.flush();
    }

    protected void verify_result() throws Exception {
        HammingDistanceCommon.oos.writeObject(cBits);
        HammingDistanceCommon.oos.flush();
    }
}