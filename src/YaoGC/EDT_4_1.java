// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

class EDT_4_1 extends CompositeCircuit {
    private final static int XOR0 = 0;
    private final static int XOR1 = 1;
    private final static int OR = 2;

    public EDT_4_1() {
        super(4, 1, 3, "EDT_4_1");
    }

    protected void createSubCircuits() throws Exception {
        subCircuits[XOR0] = new XOR_2_1();
        subCircuits[XOR1] = new XOR_2_1();
        //has  a execYao function for the newInstance
        subCircuits[OR] = OR_2_1.newInstance();

        super.createSubCircuits();
    }
    //for input wire connects
    //inputWire.connectTo(subCircuits[0].inputWires, 0)
    //it looks like to tell the system that oth subCircuit give up its own input 
    //and specify 0th inputWire of the Circuit as its 0th input 
    protected void connectWires() {
        inputWires[0].connectTo(subCircuits[XOR0].inputWires, 0);
        inputWires[2].connectTo(subCircuits[XOR0].inputWires, 1);

        inputWires[1].connectTo(subCircuits[XOR1].inputWires, 0);
        inputWires[3].connectTo(subCircuits[XOR1].inputWires, 1);
        //similarly subCircuit gives up its own input and specify subCircuits[XOR] output as its input
        subCircuits[XOR0].outputWires[0].connectTo(subCircuits[OR].inputWires, 0);
        subCircuits[XOR1].outputWires[0].connectTo(subCircuits[OR].inputWires, 1);
    }
    //the output connections is the on the opposite direction
    //tell the system that the circuit will specify the subCircuit's output as its own output
    protected void defineOutputWires() {
        outputWires[0] = subCircuits[OR].outputWires[0];
    }
}
