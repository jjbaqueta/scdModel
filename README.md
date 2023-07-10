# SCD-Model
The social-Cognitive Dependence Model (SCD-Model) is a model for task delegation simulation, supporting sub-delegations and dependence chain formation. It operates based on a Dependence Situation Network (DS-net) that specifies how the agents establish their dependencies and the order in which the tasks will be delegated or executed. A DS-net can be defined through an XML, as specified at <em>scdModel/inputs/nets</em>.

The simulator stored in this repository was developed aiming to produce the results of the work sent to the journal of the International Foundation for Autonomous Agents and Multi-Agent Systems (JAAMAS).

# Dependencies
<ul>
  <li>This project needs <em>Jason</em> to be executed. Jason is an interpreter for an extended version of AgentSpeak. A complete tutorial on how to download and configure Jason can be found at: https://jason.sourceforge.net/wp/documents/</li>
  
  <li>In the development case, we suggest using IDE Eclipse, for which there is a plugin to work with AgentSpeak. A tutorial about how to configure this plugin can be found at: https://jason.sourceforge.net/mini-tutorial/eclipse-plugin/</li>
  
  <li>The simulator can be directly executed through its build file, generated at: <em>scdModel/bin/build.xml</em>. However, to do it, you need to install Apache Ant. For more information: https://ant.apache.org/ </li>
</ul>
