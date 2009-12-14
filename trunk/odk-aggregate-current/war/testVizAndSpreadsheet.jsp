<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URL" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.gdata.client.spreadsheet.SpreadsheetService"%>
<%@ page import="com.google.gdata.data.spreadsheet.SpreadsheetEntry"%>
<%@ page import="com.google.gdata.data.spreadsheet.SpreadsheetFeed"%>
<%@ page import="com.google.gdata.util.AuthenticationException"%>
<%@ page import="com.google.gdata.util.ServiceException"%>
<%@ page import="com.google.gdata.data.spreadsheet.ListEntry"%>
<%@ page import="com.google.gdata.client.spreadsheet.ListQuery"%>
<%@ page import="com.google.gdata.data.spreadsheet.ListFeed"%>
<%@ page import="com.google.gdata.data.spreadsheet.WorksheetEntry"%>
<%@ page import="com.google.gdata.data.spreadsheet.WorksheetFeed"%>

<html>
<head>
<title>Testing Spreadsheet Feed</title>
<link rel="stylesheet" href="styles.css" type="text/css" />

	<script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["barchart"]});
    </script>

<%
	SpreadsheetService service = new SpreadsheetService("");
	String key = request.getParameter("key");
	String spreadAuth = request.getParameter("spreadAuth");
	String col1name = request.getParameter("col1"); 
	String col2name = request.getParameter("col2"); 
	String col3name = request.getParameter("col3"); 
	service.setAuthSubToken(spreadAuth, null);
		
	URL feedUri = new URL("http://spreadsheets.google.com/feeds/worksheets/"+key+"/private/full");

	WorksheetFeed listFeed = service.getFeed(feedUri, WorksheetFeed.class);
	List<WorksheetEntry> worksheets = listFeed.getEntries();//spreadsheetEntry.getWorksheets();
        	
    WorksheetEntry worksheetEntry = worksheets.get(0);
        	
    URL listFeedUrl = worksheetEntry.getListFeedUrl();
    
    ListQuery query = new ListQuery(listFeedUrl);
	query.setSpreadsheetQuery(col3name + " = true");
	ListFeed feed = service.query(query, ListFeed.class);
%>


    <script type="text/javascript">  
      
      
      
     	 var data = new google.visualization.DataTable();
<%        out.println("data.addColumn('string', '"+ col1name +"');");
		  out.println("data.addColumn('number', '"+ col2name +"');");
		//  out.println("data.addColumn('number', '"+ col3name +"')");

		  out.println("data.addRows("+worksheetEntry.getColCount()+");");
		  
		  // label = col1, value=col2
		  int curRow = 0; 
		  for (ListEntry entry : feed.getEntries()) {

			String entryLabel = ""; 
			String entryValue = ""; 
			
			for (String tag : entry.getCustomElements().getTags()) {
			
				if (tag.equals(col1name)){
		        	entryLabel = entry.getCustomElements().getValue(tag);	    
		        }
		        if (tag.equals(col2name)){
		        	entryValue = entry.getCustomElements().getValue(tag); 
		        }
		 	}
		 	
		 	out.println("data.setValue("+curRow+", 0, '" + entryLabel + "');");
		 	out.println("data.setValue("+curRow+", 1, " + entryValue+ ");");  
		 	
		 	curRow++;
		 }
%>		  
      function drawChart() {
      	  
        var chart = new google.visualization.BarChart(document.getElementById('chart_div'));
        chart.draw(data, {width: 400, height: 240, min: 0});
        alert('done drawing chart'); 
      }

    </script>
</head>




    <BODY onload="drawChart();">

<div id="container">
    
<table border=5>
<% 
	for (ListEntry entry : feed.getEntries()) {
%>
<tr>

<% 
	for (String tag : entry.getCustomElements().getTags()) {
		String value = "-"; 
        if ((tag.equals(col1name)) ||
            (tag.equals(col2name))){
        	value = entry.getCustomElements().getValue(tag);	    
        }
  %>
<td valign="top">
<%= value%>
</td>
<%
 }
%>
</tr>  
<%
	}
%>  

</table>


</div>
<input type="button" id="hello-world2" value="Show Chart" onClick="drawChart();" /> 

<div id="chart_div">
</div>

</BODY>
</HTML>