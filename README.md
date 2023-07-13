# SCD-Model
The social-Cognitive Dependence Model (SCD-Model) is a model for task delegation simulation, supporting sub-delegations and dependence chain formation. It operates based on a Dependence Situation Network (DS-net) that specifies how the agents establish their dependencies and the order in which the tasks will be delegated or executed.

# How to use
The simulations are run based on two types of input files, a JSON file that specifics the model configuration parameters and the XML files used to represent the relationships among the agents through DS-nets:

<dl>
  <dt>JSON file:</dt>
  <dd>The configuration file can be found in <a href="https://github.com/jjbaqueta/scdModel/blob/main/inputs/config/simulator_config.json">simulator_config.json</a>. In turn, some detail about the configuration parameters can be found in <a href="https://github.com/jjbaqueta/scdModel/wiki/SCD%E2%80%90Model-Parameters">SCD‐Model parameters</a>.
  </dd>

  <dt>XML files:</dt>
  <dd>The simulator can open a single and multiple DS-nets at once. The last case is indicated to simulate dynamic environments since each XML file represents a network state over time. The XML files used during a simulation must be added to the folder <a href="https://github.com/jjbaqueta/scdModel/tree/main/inputs/nets">input nets</a>. Some examples of DS-nets can be found in <a href="https://github.com/jjbaqueta/scdModel/tree/main/DSNet_examples">DS-net examples</a>. These networks are used to simulate a dynamic environment where the agents' social behavior can change over time, as described in <a href="https://github.com/jjbaqueta/scdModel/wiki/Behavior-Changes">behavior changes</a>.
  </dd>
</dl>

# Dependencies
<ul>
  <li>This project needs <em>Jason</em> to be executed. Jason is an interpreter for an extended version of AgentSpeak. A complete tutorial on how to download and configure Jason can be found at: https://jason.sourceforge.net/wp/documents/</li>
  
  <li>In the development case, we suggest using IDE Eclipse, for which there is a plugin to work with AgentSpeak. A tutorial about how to configure this plugin can be found at: https://jason.sourceforge.net/mini-tutorial/eclipse-plugin/</li>
  
  <li>The simulator can be directly executed through its build file, generated at: <em>scdModel/bin/build.xml</em>. However, to do it, you need to install Apache Ant. For more information: https://ant.apache.org/ </li>
</ul>
