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
//http://docs.google.com/feeds/default/private/full/");

        try{
        	WorksheetFeed listFeed = service.getFeed(feedUri, WorksheetFeed.class);

        	List<WorksheetEntry> worksheets = listFeed.getEntries();//spreadsheetEntry.getWorksheets();
        	for (int i = 0; i < worksheets.size(); i++) {
        	  WorksheetEntry worksheet = worksheets.get(i);
        	  String title = worksheet.getTitle().getPlainText();
        	  int rowCount = worksheet.getRowCount();
        	  int colCount = worksheet.getColCount();
        	  
        	  resp.getWriter().print("<P>"+title+" - rows: "+ rowCount + " cols: " + colCount); 
        	}
        	
        	resp.getWriter().print("<P><P><B>Doing the list iteration; not sure what II'm printing</B><P><P>");
        	
        	WorksheetEntry worksheetEntry = worksheets.get(0);
        	
        	
        	URL listFeedUrl = worksheetEntry.getListFeedUrl();
        	ListFeed feed = service.getFeed(listFeedUrl, ListFeed.class);
        	
        	resp.getWriter().print("<P>Spreadsheet Headers/Column names<P>"); 
        	
        	ListEntry firstRow = feed.getEntries().get(0);
        	String dropDownHtml = HtmlUtil.createDropDownForm("test", "test", "blah", 
        			firstRow.getCustomElements().getTags(), 
        			firstRow.getCustomElements().getTags(), 
        			"test", 
        			"chooseCol"); // entry.getCustomElements().getTags(); 
        	
        	resp.getWriter().print(dropDownHtml);
        	
        	resp.getWriter().print("<P>");
        	
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
		      
		      resp.getWriter().print("<P>"); 
		      
		      String vizHtml =
		          HtmlUtil.wrapWithHtmlTags(HtmlConsts.P, "Or, try the viz stuff  "
		              + HtmlUtil.createHref(testVizUrl, "Viz Spreadsheet"));
		      resp.getWriter().print(vizHtml);
        	
        	resp.getWriter().print("<P><HR><P>"); 
        	
        	for (ListEntry entry : feed.getEntries()) {
        	  resp.getWriter().print(entry.getTitle().getPlainText() + "| ");
        	  
        	  for (String tag : entry.getCustomElements().getTags()) {
        	    resp.getWriter().print(" ____ tag: " + tag + " value: "+ entry.getCustomElements().getValue(tag) );
        		  /*System.out.println("  <gsx:" + tag + ">" +
        				  	entry.getCustomElements().getValue(tag) + "</gsx:" + tag + ">"); */
        	  }
        	  
        	  
        	  resp.getWriter().print("<P>");
        	}
        	
        	
        	

        }
        catch(Exception e){
        	resp.getWriter().print("Something isn't working right. ");
        	resp.getWriter().print(e.getMessage());
        }

		
		//WorksheetFeed worksheets_feed = service.//key, //visibility="public', projection='values');  
		
		
	}

	
}
