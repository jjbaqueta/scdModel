var myData = JSON.parse(data);
var list = document.createElement("ul");
list.setAttribute("class", "list-group");

for (let i = 0; i < myData.length; i++){
    let item = document.createElement("li");
    item.setAttribute("class", "list-group-item");

    let content = buildDelegatorElement(myData[i].actionCode);
    let delegatees = myData[i].delegatees; 

    for(let j = 0; j < delegatees.length; j++){
        content.appendChild(buildDelegateeElement(myData[i].actionCode, delegatees[j].actionCode));
    }
    item.appendChild(content);
    list.appendChild(item);
}

document.getElementById("main").appendChild(list);

for (let i = 0; i < myData.length; i++){
    let delegatorId = myData[i].actionCode;
    let delegatees = myData[i].delegatees;
    let ySuccessHistory = myData[i].successHistory;
    let ySatisHistory = myData[i].satisfactionHistory;
    let yRegretHistory = myData[i].regretHistory;
    let xData = [];
    let traces = [];

    for (let j = 0; j < ySuccessHistory.length; j++){
        xData.push(j + 1);
    }

    let sRateMean = computeMean(ySuccessHistory);
    let satisfactionMean = computeMean(ySatisHistory);
    let regretMean = computeMean(yRegretHistory);

    let rsSpan = document.getElementById("srate_delegator_" + delegatorId);
    rsSpan.appendChild(document.createTextNode("" + sRateMean.toFixed(3)));

    let satisfactionSpan = document.getElementById("satisfaction_delegator_" + delegatorId);
    satisfactionSpan.appendChild(document.createTextNode("" + satisfactionMean.toFixed(3)));

    let regretSpan = document.getElementById("regret_delegator_" + delegatorId);
    regretSpan.appendChild(document.createTextNode("" + regretMean.toFixed(3)));

    traces.push(getLineTrace("Success rate" , xData, ySuccessHistory, 'rgb(44, 160, 44)'));
    traces.push(getLineTrace("Satisfaction" , xData, ySatisHistory, 'rgb(255, 127, 14)'));
    traces.push(getLineTrace("Regret" , xData, yRegretHistory, 'rgb(100, 100, 100)'));

    Plotly.newPlot("delegator_line_" + delegatorId, traces, getLineLayout(''));

    for(let j = 0; j < delegatees.length; j++){
        let delegateeId = delegatees[j].actionCode;        
        let ySuccessProb = delegatees[j].successProb;
        let yFailureProb = delegatees[j].failureProb;
    
        xData = [];
        traces = [];

        for (let j = 0; j < ySuccessProb.length; j++){
            xData.push(j + 1);
        }
        traces.push(getLineTrace("Success Probability" , xData, ySuccessProb, 'rgb(44, 160, 44)'));
        traces.push(getLineTrace("Failure Probability" , xData, yFailureProb, 'rgb(214, 39, 40)'));
        
        Plotly.newPlot("delegatee_line_" + delegatorId + delegateeId, traces, getLineLayout(''));

        let actionCounter = delegatees[j].actionCounter;
        let bidCounter = delegatees[j].bidCounter;
        let successCounter = delegatees[j].successCounter;
        let failureCounter = actionCounter - successCounter;
        
        traces = [];

        traces.push(getBarTrace(["#actions"], [actionCounter], 'rgb(31, 119, 180)'));
        traces.push(getBarTrace(["#bids"], [bidCounter], 'rgb(136, 0, 170)'));
        traces.push(getBarTrace(["#successes"], [successCounter], 'rgb(44, 160, 44)'));
        traces.push(getBarTrace(["#failures"], [failureCounter], 'rgb(214, 39, 40)'));
        
        Plotly.newPlot("delegatee_bar_" + delegatorId + delegateeId, traces, getBarLayout(''));
    }
}

function buildDelegatorElement(delegatorId){
    const obj = parseActionCode(delegatorId);
    
    let content = document.createElement("div");
    content.setAttribute("class", "row");
    
    let top = document.createElement("div");
    top.setAttribute("style", "margin-top: 30; margin-bottom: 30");
    top.setAttribute("class", "row");

    let topLeft = document.createElement("div");
    topLeft.setAttribute("class", "col-md-9");

    let topRight = document.createElement("div");
    topRight.setAttribute("class", "col-md-3");

    let button = document.createElement("button");
    button.setAttribute("id", ("button_" + delegatorId));
    button.setAttribute("class", "btn btn-primary");
    button.setAttribute("type", "button");
    button.setAttribute("data-bs-toggle", "collapse");
    button.setAttribute("data-bs-target", "#delegatees_for_" + delegatorId);
    button.setAttribute("aria-expanded", "false");
    button.setAttribute("aria-controls", "delegatees_for_" + delegatorId);
    button.appendChild(document.createTextNode("Show Delegatees"));
    button.addEventListener("click", ()=>
    {
        if(button.innerText === "Show Delegatees"){
            document.getElementById("delegatees_for_" + delegatorId).focus();
            button.innerText = "Hide Delegatees";
            button.classList.remove('btn-primary');
            button.classList.add('btn-danger');
        }
        else{
            button.innerText= "Show Delegatees";
            button.classList.remove('btn-danger');
            button.classList.add('btn-primary');
        }
    }, delegatorId);

    let title = document.createElement("h2");
    title.setAttribute("id", ("delegator_" + delegatorId));
    title.setAttribute("class", "text-center");
    title.appendChild(document.createTextNode("Delegator\'s Performance (" + obj.agent + ")"));
    
    let subTitle = document.createElement("div");
    subTitle.setAttribute("class", "text-center");
    subTitle.appendChild(document.createTextNode("arm: (role: " + obj.role + ", task: " + obj.task + ")"));

    let summary = document.createElement("div");
    summary.setAttribute("class", "row");
    summary.setAttribute("style", "padding-top:10px;padding-bottom:10px;padding-left:30px");

    let sRateDivLabel = document.createElement("div");
    sRateDivLabel.setAttribute("class", "col-auto");

    let sRateSpanLabel = document.createElement("span");
    sRateSpanLabel.innerHTML = "Success rate average: ".bold();

    let sRateDivContent = document.createElement("div");
    sRateDivContent.setAttribute("class", "col-auto");

    let sRateSpanContent = document.createElement("span");
    sRateSpanContent.setAttribute("id", ("srate_delegator_" + delegatorId));

    sRateDivLabel.appendChild(sRateSpanLabel);
    sRateDivContent.appendChild(sRateSpanContent);
    summary.appendChild(sRateDivLabel);
    summary.appendChild(sRateDivContent);

    let satisfactionDivLabel = document.createElement("div");
    satisfactionDivLabel.setAttribute("class", "col-auto");

    let satisfactionSpanLabel = document.createElement("span");
    satisfactionSpanLabel.innerHTML = "Satisfaction average: ".bold();

    let satisfactionDivContent = document.createElement("div");
    satisfactionDivContent.setAttribute("class", "col-auto");

    let satisfactionSpanContent = document.createElement("span");
    satisfactionSpanContent.setAttribute("id", ("satisfaction_delegator_" + delegatorId));    

    satisfactionDivLabel.appendChild(satisfactionSpanLabel);
    satisfactionDivContent.appendChild(satisfactionSpanContent);
    summary.appendChild(satisfactionDivLabel);
    summary.appendChild(satisfactionDivContent);

    let regretDivLabel = document.createElement("div");
    regretDivLabel.setAttribute("class", "col-auto");

    let regretSpanLabel = document.createElement("span");
    regretSpanLabel.innerHTML = "Regret average: ".bold();
    regretSpanLabel.appendChild(document.createTextNode("Regret average: "));

    let regretDivContent = document.createElement("div");
    regretDivContent.setAttribute("class", "col-auto");

    let regretSpanContent = document.createElement("span");
    regretSpanContent.setAttribute("id", ("regret_delegator_" + delegatorId));    

    regretDivLabel.appendChild(regretSpanLabel);
    regretDivContent.appendChild(regretSpanContent);
    summary.appendChild(regretDivLabel);
    summary.appendChild(regretDivContent);

    let delegatorLineChart = document.createElement("div");
    delegatorLineChart.setAttribute("id", ("delegator_line_" + delegatorId));
    delegatorLineChart.setAttribute("class", "row");

    topLeft.appendChild(title);
    topLeft.appendChild(subTitle);
    topRight.appendChild(button);
    top.appendChild(topLeft);
    top.appendChild(topRight);
    content.appendChild(top);
    content.appendChild(summary);
    content.appendChild(delegatorLineChart);

    return content;
}

function buildDelegateeElement(delegatorId, delegateeId){
    const obj = parseActionCode(delegateeId);
    
    let content = document.createElement("div");
    content.setAttribute("id", ("delegatees_for_" + delegatorId));
    content.setAttribute("class", "row collapse");
    content.setAttribute('tabindex', '-1');

    let top = document.createElement("div");
    top.setAttribute("class", "container");    

    let title = document.createElement("h4");
    title.setAttribute("id", ("delegatee_" + delegateeId));
    title.setAttribute("style", "margin-left: 50");
    title.appendChild(document.createTextNode("Delegatee\'s Performance (" + obj.agent + ")"));

    let subTitle = document.createElement("span");
    subTitle.setAttribute("style", "margin-left: 50");
    subTitle.appendChild(document.createTextNode("executing: (role: " + obj.role + ", task: " + obj.task + ")"));
    
    let charts = document.createElement("div");
    charts.setAttribute("class", "container");

    let delegateeLineChart = document.createElement("div");
    delegateeLineChart.setAttribute("id", ("delegatee_line_" + delegatorId + delegateeId));
    
    let delegateeBarChart = document.createElement("div");
    delegateeBarChart.setAttribute("id", ("delegatee_bar_" + delegatorId + delegateeId));
    
    top.appendChild(title);
    top.appendChild(subTitle);
    charts.appendChild(delegateeLineChart);
    charts.appendChild(delegateeBarChart);
    content.appendChild(top);
    content.appendChild(charts);

    return content;
}

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

function getBarTrace(xSeries, ySeries, Color){
    let trace = {        
        x: xSeries,
        y: ySeries,
        type: 'bar',
        text: ySeries.map(String),
        textposition: 'auto',
        hoverinfo: 'none',
        marker: {
            color: Color
        }
    };    
    return trace;
}

function getBarLayout(layoutTitle){
    let layout = {
        title: layoutTitle,
        showlegend: false,
        yaxis: {
            autorange: true
        },
        xaxis: {
            title: 'Counters',
        },
    };
    return layout;
}

function computeMean(values){
    let v = 0;
    for (let i = 0; i < values.length; i++){
        v = v + values[i];
    }
    return (v / values.length);
}

function parseActionCode(actionCode){
    let result = actionCode.split("t");
    const task = "t" + result[1];
    result = result[0].split("r");
    const role = "r" + result[1];
    const agent = result[0];
    
    return {
        agent, 
        role, 
        task
    }
}
