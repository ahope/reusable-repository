
//load google's stuff...
google.load('visualization', '1', {'packages':['piechart','barchart','linechart']});

google.setOnLoadCallback(initViz);



//makes n selectors for n variables
function makeVariableSelects( n ){
	columnSelectorOptions = document.getElementById("columnselector").innerHTML;
	variableSelection = document.getElementById("variableselection");
	
	//clear out the variableSelection
	while (variableSelection.hasChildNodes()) 
		variableSelection.removeChild(variableSelection.firstChild);
	
	//put new selectors in...
	for ( var i = 0; i <= n + 1 ; i++){
		select = document.createElement("select");

		if(i < n) select.name = "variable" + i;
		else if (i == n) select.name = "groupby";
		else if (i == n+1) select.name = "filter";
		
		select.id = select.name;
		select.innerHTML = columnSelectorOptions;
		
		variableSelection.appendChild(select);
	}
	
	var hiddenCount = document.createElement("input");
	hiddenCount.type = "hidden";
	hiddenCount.name = "variablecount";
	hiddenCount.id = "variablecount";
	hiddenCount.value = n;
	
	variableSelection.appendChild(hiddenCount);
}

//parse the chart form and get values from selects
function doCharting(){
	//get the hidden count of variables
	var n = document.getElementById("variablecount").getAttribute("value");
	
	//get the doc uri
	var spreadsheetURI = //document.getElementById("spreadsheeturi").getAttribute("value");
		"http://spreadsheets.google.com/ccc?key=0AqQYrqEwPtYldEVreklPQ21FYXVMYmtVdFlybUlGWlE&hl=en";

	//get the columns
	var varArray = new Array();
	var groupby, filter;
	for (var i = 0; i <= n+1; i++){
		if(i < n) varArray[i] = document.getElementById("variable" + i).value;
		else if (i == n) grouby = document.getElementById("groupby").value;
		else if (i == n+1) filter = document.getElementById("filter").value;
	}
	
	// Replace the data source URL on next line with your data source URL.
	var query = new google.visualization.Query(spreadsheetURI);
  
	var qString = 'select '+varArray[0]+', sum('+varArray[1]+') group by ' + grouby;
	query.setQuery(qString);
	
	alert(qString);
		
	query.send(handleQueryResponse);
}


function handleQueryResponse(response) {

	if (response.isError()) {
		alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
		return;
	}

	var data = response.getDataTable();
	var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
	chart.draw(data, {width: 400, height: 240, is3D: true});
}


function initViz(){
	makeVariableSelects(3);
	var dochart = document.getElementById("dochart");
}
