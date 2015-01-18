package YaoGC;

public class COUNTER_2toK_K_WEIJIE extends CompositeCircuit {
	private final int K;
	COUNTER_2toK_K_WEIJIE(int k)
	{
		super(1 << (k - 1), k, k-1, "COUNTER_" + (1 << (k - 1)) + "_" + k+"WEIJIE");
		K = k;
	}
	protected void createSubCircuits() throws Exception {
        // parallelized COUNTER: from l (e.g., 2^k) inputs to one output
        for(int i=0; i<K-1; i++)
        	subCircuits[i] = new ADD_2L_Lplus1_2toK_K(i, K-i-1);
        super.createSubCircuits();
    }
	@Override
	protected void connectWires() throws Exception {
		// TODO Auto-generated method stub
		//for level i, its input is (1<<i)*(K-i-1)*2
		//its output should be (1<<i)*(K-i)
		for(int i=0; i<K-2; i++)
		{
			int size = (K-i-1);
			int len = 2*(K-i-1)*(1<<i);
			for(int j=0; j<len; j++)
			{
				int box = j/size;
				int index = j%size;
				if(box%2==0)
					subCircuits[i+1].outputWires[j].connectTo(subCircuits[i].inputWires, box*size+2*index+1);
				else
					subCircuits[i+1].outputWires[j].connectTo(subCircuits[i].inputWires, (box/2)*2*size+2*index);
			}
			
		}
		//make the input connection
		for(int i=0; i<(1<<(K-1));i+=2)
		{
			inputWires[i].connectTo(subCircuits[K-2].inputWires, i+1);
			inputWires[i+1].connectTo(subCircuits[K-2].inputWires, i);
		}
	}
	@Override
	protected void defineOutputWires() {
		// TODO Auto-generated method stub
		System.arraycopy(subCircuits[0].outputWires, 0, outputWires, 0, K);
	}

}
