// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class COUNTER_2toK_K extends CompositeCircuit {

    public COUNTER_2toK_K(int k) {
        super(1 << (k - 1), k, (1 << (k - 1)) - 1, "COUNTER_" + (1 << (k - 1)) + "_" + k);
    }

    protected void createSubCircuits() throws Exception {
        // parallelized COUNTER: from l (e.g., 2^k) inputs to one output
        int i = 0;
        for (int level = 0; level < outDegree - 1; level++) {
            int l = outDegree - level - 1;
            for (int x = 0; x < (1 << level); x++) {
                subCircuits[i] = new ADD_2L_Lplus1(l);
                i++;
            }
        }

        super.createSubCircuits();
    }
    /*
     * Connect xWires[xStartPos...xStartPos+L] to the wires representing bits of X;
     * yWires[yStartPos...yStartPos+L] to the wires representing bits of Y;
     */
    /*
    public void connectWiresToXY(Wire[] xWires, int xStartPos, Wire[] yWires, int yStartPos) throws Exception {
        if (xStartPos + L > xWires.length || yStartPos + L > yWires.length)
            throw new Exception("Unmatched number of wires.");

        for (int i = 0; i < L; i++) {
            xWires[xStartPos + i].connectTo(inputWires, X(i));
            yWires[yStartPos + i].connectTo(inputWires, Y(i));
        }
    }
    */
    protected void connectWires() throws Exception {
        int i = 0, j = 0;
        for (int level = 0; level < outDegree - 1; level++)
        {
            if (level == outDegree - 2) 
            {
            	//this is the last level
            	//PLEASE PAY ATTENTION AT THIS LEVEL
            	//for the ADD_2L_Lplus1, L is 1
            	//and it seems that the connection is wrong
            	//should be conncetWiresToXY(inputWires, j+1, inputWires, j);
                for (int x = 0; x < (1 << level); x++) 
                {
                	//inputWires[j.+k].connectTo(subCircuits[i].inputWires, X(k));
                	//inputWires[j+1.+k].connectTo(subCircuits[i].inputWires, Y(k));
                    ((ADD_2L_Lplus1) subCircuits[i]).
                            connectWiresToXY(inputWires, j, inputWires, j + 1);
                    i++;
                    j += 2;
                }
            }
            else 
            {
                for (int x = 0; x < (1 << level); x++) 
                {
                    ((ADD_2L_Lplus1) subCircuits[i]).
                            connectWiresToXY(subCircuits[2 * i + 1].outputWires, 0,
                                    subCircuits[2 * i + 2].outputWires, 0);
                    i++;
                }
            }
        }
    }

    protected void defineOutputWires() {
    	/*
    	 * public static void arraycopy(Object src,
                             int srcPos,
                             Object dest,
                             int destPos,
                             int length)
    	 */
        System.arraycopy(subCircuits[0].outputWires, 0, outputWires, 0, outDegree);
    }
}