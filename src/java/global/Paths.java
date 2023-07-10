package global;

public enum Paths {
	INPUT_CONFIG_FILE("inputs/config/simulator_config.json"),
	INPUT_XML_DSNET_DEFAULT("inputs/nets/DSNet.xml"),
	INPUT_XML_DSNET_STREAMING("inputs/nets"),
	
	OUTPUT_JSON_AGENTS("outputs/agents.json"),
	OUTPUT_JSON_SERIES("outputs/series.json"),
	OUTPUT_JSON_LOG("outputs/log.json"),
	OUTPUT_TXT_LOG("outputs/log.txt"),
	OUTPUT_DEBUG("outputs/debug.txt"),
	OUTPUT_ITR("outputs/itr.txt");
	
	private String path;
	
	private Paths(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name();
	}
}