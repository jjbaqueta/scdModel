package openMas;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.structures.dsNet.DSNet;
import core.structures.dsNet.services.DSNetFacade;
import global.Paths;

public class DSNetStreaming {
	private Integer index;
	private List<DSNet> dsNets;
	
	public DSNetStreaming() {
		this.index = 0;
		this.dsNets = new ArrayList<DSNet>();
		
		File nets = new File(Paths.INPUT_XML_DSNET_STREAMING.getPath());
		File[] files = nets.listFiles();
		Arrays.sort(files);	
		
		for(File net : files) {
			dsNets.add(DSNetFacade.loadDSnetFromXmlFile(net.getPath()));
		}
	}
	
	public DSNet getCurrentDSNet() {
		if (!this.dsNets.isEmpty()) {
			return this.dsNets.get(this.index);			
		}
		throw new Error("The streaming is empty. There is no DS-net to be selected!");
	}
	
	public DSNet getNextDSNet() {
		if (!this.dsNets.isEmpty()) {
			if (this.index < this.dsNets.size() - 1) {
				this.index++;				
			}
			return this.dsNets.get(this.index);			
		}
		throw new Error("The streaming is empty. There is no DS-net to be selected!");
	}
	
	public int getNumberOfDSNets() {
		return this.dsNets.size();
	}
}