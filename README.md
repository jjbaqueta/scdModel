# SCD-Model
The social-Cognitive Dependence Model (SCD-Model) is a model for task delegation simulation, supporting sub-delegations and dependence chain formation. It operates based on a Dependence Situation Network (DS-net) that specifies how the agents establish their dependencies and the order in which the tasks will be delegated or executed. A DS-net can be defined through an XML (<em>e</em>.<em>g</em>., https://github.com/jjbaqueta/scdModel/blob/main/inputs/nets/DSNet_1.xml).

# Dependencies
<ul>
  <li>This project needs <em>Jason</em> to be executed. Jason is an interpreter for an extended version of AgentSpeak. A complete tutorial on how to download and configure Jason can be found at: https://jason.sourceforge.net/wp/documents/</li>
  
  <li>In the development case, we suggest using IDE Eclipse, for which there is a plugin to work with AgentSpeak. A tutorial about how to configure this plugin can be found at: https://jason.sourceforge.net/mini-tutorial/eclipse-plugin/</li>
  
  <li>The simulator can be directly executed through its build file, generated at: <em>scdModel/bin/build.xml</em>. However, to do it, you need to install Apache Ant. For more information: https://ant.apache.org/ </li>
</ul>

# How to use
The simulations are run based on input parameters that can be configured through an external file. This file is located at: https://github.com/jjbaqueta/scdModel/blob/main/inputs/config/simulator_config.json

A detailed specification about the meaning of each parameter can be found at: https://github.com/jjbaqueta/scdModel/wiki/SCD%E2%80%90Model-Parameters

