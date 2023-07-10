var myData = JSON.parse(data);
var xData = [];
var ySRateActives = [];
var ySatisActives = [];
var yRegretActives = [];
var ySRateAgents = [];
var ySatisAgents = [];
var yRegretAgents = [];
var tracesActives = [];
var tracesAgents = [];

for (let i = 0; i < myData.length; i++){    
    xData.push(myData[i].iteration);
    ySRateActives.push(myData[i].series.activesSuccessRateAvg);
    ySatisActives.push(myData[i].series.activesSatisfactionAvg);
    yRegretActives.push(myData[i].series.activesRegretAvg);

    ySRateAgents.push(myData[i].series.agentsSuccessRateAvg);
    ySatisAgents.push(myData[i].series.agentsSatisfactionAvg);
    yRegretAgents.push(myData[i].series.agentsRegretAvg);
}

tracesActives.push(getLineTrace("Success rate" , xData, ySRateActives, 'rgb(44, 160, 44)'));
tracesActives.push(getLineTrace("Satisfaction" , xData, ySatisActives, 'rgb(255, 127, 14)'));
tracesActives.push(getLineTrace("Regret" , xData, yRegretActives, 'rgb(100, 100, 100)'));

tracesAgents.push(getLineTrace("Success rate" , xData, ySRateAgents, 'rgb(44, 160, 44)'));
tracesAgents.push(getLineTrace("Satisfaction" , xData, ySatisAgents, 'rgb(255, 127, 14)'));
tracesAgents.push(getLineTrace("Regret" , xData, yRegretAgents, 'rgb(100, 100, 100)'));

Plotly.newPlot('activesAverages', tracesActives, getLineLayout(''));
Plotly.newPlot('agentsAverages', tracesAgents, getLineLayout(''));

function getLineTrace(traceName, xSeries, ySeries, Color){
    let trace = {
        name: traceName,        
        x: xSeries,
        y: ySeries,
        type: 'scatter',
        line: {
            color: Color,
        }
    };    
    return trace;
}

function getLineLayout(layoutTitle){
    let layout = {
        title: layoutTitle,
        paper_bgcolor: 'rgba(0,0,0,0)',
        plot_bgcolor: 'rgba(0,0,0,0)',
        xaxis: {
            title: 'Iterations',
            autorange: true
        },
        yaxis: {
            range: [-0.02,1.02]
        },
        legend: {
            orientation: 'h',
            x : 0,
            y : 1.24 
        }
    };
    return layout;
}

function getDataFromWindow(wStart, wEnd){
    let myData = JSON.parse(data);

    let successValues = [];
    let satisfactionValues = [];
    let regretValues = [];

    for (let i = wStart; i < wEnd; i++){
        successValues.push(myData[i].series.activesSuccessRateAvg);
        satisfactionValues.push(myData[i].series.activesSatisfactionAvg);
        regretValues.push(myData[i].series.activesRegretAvg);
    }

    console.log("success mean: " + calculateMean(successValues));
    console.log("satisfaction mean: " + calculateMean(satisfactionValues));
    console.log("regret mean: " + calculateMean(regretValues));

    console.log("success sd: " + calculateSD(successValues));
    console.log("satisfaction sd: " + calculateSD(satisfactionValues));
    console.log("regret sd: " + calculateSD(regretValues));    
}

function calculateMean(values){
    return (values.reduce((sum, current) => sum + current)) / values.length;
}

function calculateSD(values){
    const average = calculateMean(values);
    const squareDiffs = values.map((value) => {
        const diff = value - average;
        return diff * diff;
    });
    return Math.sqrt(calculateMean(squareDiffs));
};
