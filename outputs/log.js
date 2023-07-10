var myData = JSON.parse(data);
var executionTime = myData.executionTime;

// General measures

var timeSpanSec = document.getElementById("timeSpanSec");
timeSpanSec.appendChild(document.createTextNode("" + executionTime.toFixed(3)));

var timeSpanMin = document.getElementById("timeSpanMin");
timeSpanMin.appendChild(document.createTextNode("" + (executionTime/60).toFixed(3)));

var timeSpanHor = document.getElementById("timeSpanHor");
timeSpanHor.appendChild(document.createTextNode("" + (executionTime/60/60).toFixed(3)));

var activesTraceSuccess = {
    y: myData.activesSuccessValues,
    type: 'box',
    boxpoints: 'all',
    jitter: 0.3,
    pointpos: -1.8,
    name: 'Success rate',
    marker: {color: 'rgb(44, 160, 44)'},
};

var activesTraceSatisfaction = {
    y: myData.activesSatisfactionValues,
    type: 'box',
    boxpoints: 'all',
    jitter: 0.3,
    pointpos: -1.8,
    name: 'Satisfaction',
    marker: {color: 'rgb(255, 127, 14)'},
};

var activesTraceRegret = {
    y: myData.activesRegretValues,
    type: 'box',
    boxpoints: 'all',
    jitter: 0.3,
    pointpos: -1.8,
    name: 'Regret',
    marker: {color: 'rgb(100, 100, 100)'},
};

var agentsTraceSuccess = {
    y: myData.agentsSuccessValues,
    type: 'box',
    boxpoints: 'all',
    jitter: 0.3,
    pointpos: -1.8,
    name: 'Success rate',
    marker: {color: 'rgb(44, 160, 44)'},
};

var agentsTraceSatisfaction = {
    y: myData.agentsSatisfactionValues,
    type: 'box',
    boxpoints: 'all',
    jitter: 0.3,
    pointpos: -1.8,
    name: 'Satisfaction',
    marker: {color: 'rgb(255, 127, 14)'},
};

var agentsTraceRegret = {
    y: myData.agentsRegretValues,
    type: 'box',
    boxpoints: 'all',
    jitter: 0.3,
    pointpos: -1.8,
    name: 'Regret',
    marker: {color: 'rgb(100, 100, 100)'},
};


var dataActivesSuccess = [activesTraceSuccess];
var dataActivesSatisfaction = [activesTraceSatisfaction];
var dataActivesRegret = [activesTraceRegret];

var dataAgentsSuccess = [agentsTraceSuccess];
var dataAgentsSatisfaction = [agentsTraceSatisfaction];
var dataAgentsRegret = [agentsTraceRegret];

var layoutSuccess = {    
    yaxis: {
        range: [-0.02,1.02]
    },
    xaxis: {zeroline: false}
};

var layoutSatisfaction = {
    yaxis: {
        range: [-0.02,1.02]
    },
    xaxis: {zeroline: false}
};

var layoutRegret = {
    yaxis: {
        range: [-0.02,1.02]
    },
    xaxis: {zeroline: false}
};

Plotly.newPlot('activesSuccessDiv', dataActivesSuccess, layoutSuccess);
Plotly.newPlot('activesSatisDiv', dataActivesSatisfaction, layoutSatisfaction);
Plotly.newPlot('activesRegretDiv', dataActivesRegret, layoutRegret);

Plotly.newPlot('agentsSuccessDiv', dataAgentsSuccess, layoutSuccess);
Plotly.newPlot('agentsSatisDiv', dataAgentsSatisfaction, layoutSatisfaction);
Plotly.newPlot('agentsRegretDiv', dataAgentsRegret, layoutRegret);
