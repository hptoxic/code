// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package OT;

import Cipher.Cipher;
import Utils.Utils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Random;

public class OTExtReceiver extends Receiver {
    private static Random rnd = new Random();

    private int k1;
    private int k2;
    private int msgBitLength;

    private Sender snder;
    private BitMatrix T;
    private BigInteger[][] keyPairs;

    public OTExtReceiver(int numOfChoices,
                         ObjectInputStream in, ObjectOutputStream out) throws Exception {
        super(numOfChoices, in, out);

        initialize();
    }

    public void execProtocol(BigInteger choices) throws Exception {
        super.execProtocol(choices);

        BigInteger[][] msgPairs = new BigInteger[k1][2];
        BigInteger[][] cphPairs = new BigInteger[k1][2];

        for (int i = 0; i < k1; i++) {
            msgPairs[i][0] = T.data[i];
            msgPairs[i][1] = T.data[i].xor(choices);

            cphPairs[i][0] = Cipher.encrypt(keyPairs[i][0], msgPairs[i][0], numOfChoices);
            cphPairs[i][1] = Cipher.encrypt(keyPairs[i][1], msgPairs[i][1], numOfChoices);
        }

        oos.writeObject(cphPairs);
        oos.flush();
        int bytelength;

        BitMatrix tT = T.transpose();

        BigInteger[][] y = new BigInteger[numOfChoices][2];
        bytelength = (msgBitLength - 1) / 8 + 1;
        for (int i = 0; i < numOfChoices; i++) {
            y[i][0] = Utils.readBigInteger(bytelength, ois);
            y[i][1] = Utils.readBigInteger(bytelength, ois);
        }

        data = new BigInteger[numOfChoices];
        for (int i = 0; i < numOfChoices; i++) {
            int sigma = choices.testBit(i) ? 1 : 0;
            //tT.data store key?
            data[i] = Cipher.decrypt(i, tT.data[i], y[i][sigma], msgBitLength);
        }
    }

    private void initialize() throws Exception {
        //number of keypairs
    	k1 = ois.readInt();
        //k2 number of bit
        k2 = ois.readInt();
        msgBitLength = ois.readInt();

        snder = new NPOTSender(k1, k2, ois, oos);
        //k1 is the column num#, this matrix has k1 bigInt, each bigInt is numofChoices bit
        T = new BitMatrix(numOfChoices, k1);
        T.initialize(rnd);

        keyPairs = new BigInteger[k1][2];
        for (int i = 0; i < k1; i++) {
            keyPairs[i][0] = new BigInteger(k2, rnd);
            keyPairs[i][1] = new BigInteger(k2, rnd);
        }

        snder.execProtocol(keyPairs);
    }
}