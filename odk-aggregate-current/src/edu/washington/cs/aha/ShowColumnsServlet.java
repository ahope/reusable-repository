package edu.washington.cs.aha;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed; 

public class ShowColumnsServlet extends ServletUtilBase {

	
	
	public void doGet(HttpServletRequest request, HttpServletResponse resp)
	throws IOException {
		
		// Params I need: 
		/*
		 *  - Auth token 
		 *  - Spreadsheet key?
		 *  
		 */
	    
		SpreadsheetService service = new SpreadsheetService("");
		String key = getParameter(request, "key");//"tyRP7fssdswi82a9eJSwM_g" ;
		String spreadAuth = getParameter(request, "spreadAuth");
		service.setAuthSubToken(spreadAuth, null);
		
		URL feedUri = new URL("http://spreadsheets.google.com/feeds/worksheets/"+key+"/private/full");

        try{
        	WorksheetFeed listFeed = service.getFeed(feedUri, WorksheetFeed.class);
        	
        	List<WorksheetEntry> worksheets = listFeed.getEntries();
        	for (int i = 0; i < worksheets.size(); i++) {
        	  WorksheetEntry worksheet = worksheets.get(i);
        	  String title = worksheet.getTitle().getPlainText();
        	  
        	}
        	
        	String htmlHead = "<html><head><title>ODK Viz</title>" +
  			"<script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script>" +
  			"<script type=\"text/javascript\" src=\"http://reusable-app-id.appspot.com/javascripts/viz.js\"></script>" +
  			"</head>";
        	
        	resp.getWriter().print(htmlHead);
        	
        	String htmlBody = "<body \">";
        	
        	resp.getWriter().print(htmlBody);
        	        	
        	WorksheetEntry worksheetEntry = worksheets.get(0);
        	
        	URL listFeedUrl = worksheetEntry.getListFeedUrl();
        	ListFeed feed = service.getFeed(listFeedUrl, ListFeed.class);
        	        	
        	ListEntry firstRow = feed.getEntries().get(0);
        	
//        	String dropDownHtml = HtmlUtil.createDropDownForm("#", "post", "gsscolumns", 
//        			firstRow.getCustomElements().getTags(), 
//        			firstRow.getCustomElements().getTags(), 
//        			"Select"); 
        	
        	String chartingForm = "<form action=\"javascript:doCharting();\">"
        		+ "<input id=\"spreadsheeturi\" type=\"hidden\" name=\"spreadsheeturi\" value=\""+ feedUri.toString() +"\" >"
        		+ "<div id=\"variableselection\">"
        		+ "<select id=\"columnselector\" >";
        	
        	int charCounter = (int)'A';
        	
        	for( String tag : firstRow.getCustomElements().getTags()){
        		chartingForm += "<option value=\""+((char)charCounter++)+"\" >" + tag + "</option>";
        	}
        	
        	chartingForm += "</select></div><input type=\"submit\" id=\"dochart\" name=\"dochart\" />" +
        			"</form><div id=\"chart_div\" ></div>";
        	
        	resp.getWriter().print(chartingForm);
        	//resp.getWriter().print(dropDownHtml);
        	
        	String docSessionToken = getParameter(request, ServletConsts.DOC_AUTH);
    	    String spreadSessionToken = getParameter(request, ServletConsts.SPREAD_AUTH);
    	    
        	Map<String, String> params = new HashMap<String, String>();
    		//    params.put(ServletConsts.SPREADSHEET_NAME_PARAM, spreadsheetName);
    		//    params.put(ServletConsts.ODK_FORM_KEY, odkFormKey);
    		    params.put(ServletConsts.DOC_AUTH, docSessionToken);
    		    params.put(ServletConsts.SPREAD_AUTH, spreadSessionToken);
    		    params.put("key", key);
    		    params.put("col1", "name");
    		    params.put("col2", "location");
    		    params.put("col3", "going");
    		    
        	String testVizUrl = 
		    	  "http://" + HtmlUtil.createLinkWithProperties(getServerURL(request) + "/testVizAndSpreadsheet.jsp", params);
		      
		      
		      String vizHtml =
		          HtmlUtil.wrapWithHtmlTags(HtmlConsts.P, "Or, try the viz stuff  "
		              + HtmlUtil.createHref(testVizUrl, "Viz Spreadsheet"));
		      resp.getWriter().print(vizHtml);
        	
        	
//        	for (ListEntry entry : feed.getEntries()) {
//        	  resp.getWriter().print(entry.getTitle().getPlainText() + "| ");
//        	  
//        	  for (String tag : entry.getCustomElements().getTags()) {
//        	    resp.getWriter().print(" ____ tag: " + tag + " value: "+ entry.getCustomElements().getValue(tag) );
//        		  /*System.out.println("  <gsx:" + tag + ">" +
//        				  	entry.getCustomElements().getValue(tag) + "</gsx:" + tag + ">"); */
//        	  }
//        	  
//        	}
        	
		      
		  String htmlEnd = "</body></html>";
		  
		  resp.getWriter().print(htmlEnd);
        	
        	

        }
        catch(Exception e){
        	resp.getWriter().print("Something isn't working right. ");
        	resp.getWriter().print(e.getMessage());
        }

		
		//WorksheetFeed worksheets_feed = service.//key, //visibility="public', projection='values');  
		
		
	}

	
}
