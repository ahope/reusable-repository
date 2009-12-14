<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
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
<%@ page import="edu.washington.cs.aha.HtmlUtil"%>
<%@ page import="edu.washington.cs.aha.ServletUtilBase"%>

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
	String docAuth = request.getParameter("docAuth"); 
	String col1name = request.getParameter("col1"); 
	String col2name = request.getParameter("col2"); 
	String col3name = request.getParameter("col3"); 
	service.setAuthSubToken(spreadAuth, null);
	
	Map<String, String> params = new HashMap<String, String>(); 
	params.put("spreadAuth", spreadAuth); 
	params.put("docAuth", docAuth); 
	params.put("key", key); 
	
	
	// Saving this for use later
	String baseUrl = HtmlUtil.createLinkWithProperties(//ServletUtilBase.getServerURL(request) + 
                            request.getRequestURI(), params);
               	
	URL feedUri = new URL("http://spreadsheets.google.com/feeds/worksheets/"+key+"/private/full");

	WorksheetFeed listFeed = service.getFeed(feedUri, WorksheetFeed.class);
	List<WorksheetEntry> worksheets = listFeed.getEntries();//spreadsheetEntry.getWorksheets();
        	
    WorksheetEntry worksheetEntry = worksheets.get(0);
       	
    URL listFeedUrl = worksheetEntry.getListFeedUrl();
    
    ListQuery query = new ListQuery(listFeedUrl);
    // Only get the filtered stuff
	
	String filterQuery = ""; 
	if (!col3name.equals("bah3")){
		filterQuery = col3name + " = true"; 
	}
	
	query.setSpreadsheetQuery(filterQuery);
	ListFeed feed = service.query(query, ListFeed.class);
	
	
    ListEntry firstRow = feed.getEntries().get(0);
	
%>


    <script type="text/javascript">  
      
      
      
     	 var data = new google.visualization.DataTable();
<%        
		
		if ( (!col1name.equals("bah1")) && 
			 (!col2name.equals("bah2"))){
		
		out.println("data.addColumn('string', '"+ col1name +"');");
		  out.println("data.addColumn('number', '"+ col2name +"');");

		  out.println("data.addRows("+feed.getEntries().size()+");");//worksheetEntry.getColCount()-1+");");
		  
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
		 }
		out.println("\n\nvar baseUrl = \"" + baseUrl +"\";" );
		 out.println("\n\nvar col1Name = \"" + col1name +"\";" );
		 out.println("var col2Name = \"" + col2name +"\";" );
		 out.println("var col3Name = \"" + col3name +"\";" ); 
		 
		 boolean drawChart = (!col1name.equals("bah1")) && (!col2name.equals("bah2")); 
		 
		 out.println("var drawChart = "+ drawChart + ";"); 
		 
%>		  
      function drawChart() {
      	  if (drawChart)
      	  {
      	  alert("updating chart"); 
        var chart = new google.visualization.BarChart(document.getElementById('chart_div'));
        chart.draw(data, {width: 400, height: 240, min: 0});
        }
         
      }
      
      function setCol1Name(dropDown){
        col1Name = dropDown.options[dropDown.selectedIndex].value; 
      }
      
      function setCol2Name(dropDown){
      	col2Name = dropDown.options[dropDown.selectedIndex].value; ; 
      }
      
      function setCol3Name(dropDown){
      	col3Name = dropDown.options[dropDown.selectedIndex].value; ; 
      }

	  function reloadWithNewParams(){
	    alert("reloading. baseUrl= " + baseUrl); 
	  	newUrl = baseUrl + "&col1=" + col1Name + "&col2="+col2Name + "&col3="+col3Name;
	  	alert("newUrl= " + newUrl); 
	  	window.location = newUrl; 
	  }

    </script>
</head>




    <BODY onload="drawChart();">

<div id="container">
    

<div>
<P>
<b>Spreadsheet name: (coming)</b>
</P>
<P>
<%
		String dropDownHtml1 = HtmlUtil.createDropDownForm("test", "setCol1Name(this.blah)", "blah", 
		        			firstRow.getCustomElements().getTags(), 
		        			firstRow.getCustomElements().getTags(), 
		        			"test",
		        			"chooseCol1");
		        			
	   String dropDownHtml2 = HtmlUtil.createDropDownForm("test", "setCol2Name(this.blah)", "blah", 
		        			firstRow.getCustomElements().getTags(), 
		        			firstRow.getCustomElements().getTags(), 
		        			"test",
		        			"chooseCol2");
		        			
	   String dropDownHtml3 = HtmlUtil.createDropDownForm("test", "setCol3Name(this.blah)", "blah", 
		        			firstRow.getCustomElements().getTags(), 
		        			firstRow.getCustomElements().getTags(), 
		        			"test", 
		        			"chooseCol3");
	   		        			
%> 
	Y-axis column: (labels)
<%	   		        			
	   out.println(dropDownHtml1);
%> 
	X-axis column: (values)
<%	
	   out.println(dropDownHtml2);
%> 
	Filter column:
<%	
	   out.println(dropDownHtml3); 
	   
	   
%>
<input type="button" id="updateColButton" value="Update columns" onClick="reloadWithNewParams();" /> 
	   
</P>
</div>

<div>
<input type="button" id="hello-world2" value="Show Chart" onClick="drawChart();" /> 
</div>
<div id="chart_div">
</div>

</BODY>
</HTML>