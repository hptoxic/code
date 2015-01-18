package YaoGC;

public class ADD_2L_Lplus1_2toK_K extends CompositeCircuit{
	private final int L;
	private final int K;
	//level K from 0, when K is 0, there is one ADD_2l_lplus1, 
	//atuall the l should 
	
	//very interesting, the super() seems must be called first.
	public ADD_2L_Lplus1_2toK_K(int k, int l)
	{
		super((1<<k)*2*l, (1<<k)*(l+1), 1<<(k), "ADD_" + (2 * l) + "_" + (l + 1)+'_'+(1<<k)+"_"+k);
		this.K = k;
		this.L = l;
	}
	
	protected void createSubCircuits() throws Exception {
        for(int i=0; i<(1<<K); i++)
        	subCircuits[i] = new ADD_2L_Lplus1(L);
        super.createSubCircuits();
    }
	
	@Override
	protected void connectWires() throws Exception {
		// TODO Auto-generated method stub
		for(int i=0; i<(1<<K); i++)
			for(int j=0; j<2*L; j++)
				inputWires[i*2*L+j].connectTo(subCircuits[i].inputWires, j);
	}

	@Override
	protected void defineOutputWires() {
		// TODO Auto-generated method stub
		for(int i=0; i<(1<<K); i++)
		{
			System.arraycopy(subCircuits[i].outputWires, 0, outputWires, i*(L+1), L+1);
		}
	}

}
