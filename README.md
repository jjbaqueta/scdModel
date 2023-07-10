# scdModel
The social-Cognitive Dependence Model (scdModel) is a model for task delegation simulation, supporting sub-delegations and dependence chain formation. It operates based on a Dependence Situation Network (DS-net) that specifies how the agents establish their dependencies and the order in which the tasks will be delegated or executed. A DS-net can be defined through an XML, as specified at <em>scdModel/inputs/nets</em>.

# Dependencies
<ul>
  <li>This project needs <em>Jason</em> to be executed. Jason is an interpreter for an extended version of AgentSpeak. A complete tutorial on how to download and configure Jason can be found at: https://jason.sourceforge.net/wp/documents/</li>
  
  <li>In the development case, we suggest using IDE Eclipse, for which there is a plugin to work with AgentSpeak. A tutorial about how to configure this plugin can be found at: https://jason.sourceforge.net/mini-tutorial/eclipse-plugin/</li>
  
  <li>The simulator can be directly executed through its build file, generated at: <em>scdModel/bin/build.xml</em>. However, to do it, you need to install Apache Ant. For more information: https://ant.apache.org/ </li>
</ul>

# Input Parameters
The simulator allows the configuration of several input parameters, which are defined through a JSON file at: <em>scdModel/inputs/conf/simulator_config.json</em>. 

The following is the complete list of input parameters that can be configured:

<dl>
  <dt>numberOfIterations</dt>
  <dd> - The number of times the agents will repeat the partner selection process over time.</dd>
  
  <dt>memoryCapacity</dt>
  <dd> - The number of memory slots for agents to allocate impressions.</dd>

  <dt>imageWeight</dt>
  <dd> - The weight assigned to an agent's social image during the partner selection stage.</dd>

  <dt>reputationWeight</dt>
  <dd> - The weight assigned to an agent's reputation during the partner selection stage.</dd>

  <dt>referenceWeight</dt>
  <dd> - The weight assigned to an agent's references during the partner selection stage.</dd>

  <dt>penalizationDiscount</dt>
  <dd> - Penalization degree applied to agents in case of failure. The full penalization is applied by assigning 0 to this parameter; otherwise, the agents are partially penalized based on their position in the failure chain.
  </dd>

  <dt>failurePropagationAlg</dt>
  <dd> - The algorithm for solving a conflict of failure propagation when an agent makes part of several failure chains simultaneously.</dd>

  <dt>partnerSelectionAlg</dt>
  <dd> - The algorithm for managing the trade-off between exploitation and exploration.</dd>

  <dt>accumulationAlg</dt>
  <dd> - The algorithm for accumulates the agents' success rate along the dependence chains to produce the accumulated success rate measure.</dd>

  <dt>startupValue</dt>
  <dd> - The initial value the agents employ to assess their partner when the system is starting.</dd>

  <dt>accuracyRangeStart</dt>
  <dd> - Lower bound for the agents' estimation accuracy.</dd>

  <dt>accuracyRangeEnd</dt>
  <dd> - Upper bound for the agents' estimation accuracy.</dd>

  <dt>accuracyMaxIteration</dt>
  <dd> - The number of iterations required for an agent to achieve the upper bound of its estimation accuracy.</dd>

  <dt>debugMode</dt>
  <dd> - If true, turn on the debug mode.</dd>

  <dt>dimensionSR</dt>
  <dd> - If true, the agents consider the success rate dimension during the partner selection.</dd>

  <dt>dimensionASR</dt>
  <dd> - If true, the agents consider the accumulated success rate dimension during the partner selection.</dd>

  <dt>dimensionCompetence</dt>
  <dd> - If true, the agents consider the competencies of their partners during the partner selection.</dd>
  
  <dt>dimensionExpectation</dt>
  <dd> - If true, the agents consider their expectations during the partner selection.</dd>
</dl>

