package edu.washington.cs.aha;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class ListSpreadsheetsServlet extends ServletUtilBase {
	public void doGet(HttpServletRequest request, HttpServletResponse resp)
	throws IOException {
		
		System.out.println("Entering ListSpreadsheetsServlet.doGet()");
		/*
		// verify user is logged in
	    if (!verifyCredentials(request, resp)) {
	      return;
	    }*/
		
		// If not authenticated/logged in, write out a URL to do the log-in. 
		// If authenticated, there will be a token in the link? 
		// When authenticating, have this be the "next" URL
	
		// Otherwise, show a list of spreadsheets to choose from (?) 
		// Have the URLs for each spreadsheet include the user auth token 
		
		resp.getWriter().print("you're logged in. That's great!");
	      
		String docSessionToken = getParameter(request, ServletConsts.DOC_AUTH);
	    String spreadSessionToken = getParameter(request, ServletConsts.SPREAD_AUTH);
	    
		
	        // Create a new Spreadsheet service
	        SpreadsheetService myService = new SpreadsheetService("UWCSE-ahaTestApp-1");
	        DocsService docListService = new DocsService("");
	        
	        docListService.setAuthSubToken(docSessionToken, null);
	        URL feedUri = new URL("http://docs.google.com/feeds/default/private/full/");

	        try{
	        DocumentListFeed listFeed = docListService.getFeed(feedUri, DocumentListFeed.class);
	        }
	        catch(Exception e){
	        	resp.getWriter().print("couldn't get docListFeed");
	        
	        }
		    try{
		      // check the scope
		      Map<String, String> tokenInfo = AuthSubUtil.getTokenInfo(spreadSessionToken, null);

		        String tokenScope = tokenInfo.get("Scope");
		        
		        resp.getWriter().print("<P>Scope:"+tokenScope+"</P>");
			    
		        String target = tokenInfo.get("Target"); 
		        resp.getWriter().print("<P>Target:"+target+"</P>");
			    
		        resp.getWriter().println(tokenInfo.keySet());
		    }
		    catch (Exception e) {
				// TODO: handle exception
			}
		      
		    try{
	       myService.setAuthSubToken(spreadSessionToken);
	        
	        // Get a list of all entries
	        URL metafeedUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
	       // System.out.println("Getting Spreadsheet entries...\n");
	        SpreadsheetFeed resultFeed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);
	        
	        List<SpreadsheetEntry> entries = resultFeed.getEntries();
	        for(int i=0; i<entries.size(); i++) {
	          SpreadsheetEntry entry = entries.get(i);
	          System.out.println("\t" + entry.getTitle().getPlainText());
	          resp.getWriter().print("<P>"+entry.getTitle().getPlainText()+ " key: "+entry.getKey());
	  	    

		    Map<String, String> params = new HashMap<String, String>();
		//    params.put(ServletConsts.SPREADSHEET_NAME_PARAM, spreadsheetName);
		//    params.put(ServletConsts.ODK_FORM_KEY, odkFormKey);
		    params.put(ServletConsts.DOC_AUTH, docSessionToken);
		    params.put(ServletConsts.SPREAD_AUTH, spreadSessionToken);
		    params.put("key", entry.getKey());
	          
	          String returnUrl =
	              "http://"
	                  + HtmlUtil.createLinkWithProperties(getServerURL(request) + "/chooseCols", params);
	     
		      
		      
		      String loginHtml =
		          HtmlUtil.wrapWithHtmlTags(HtmlConsts.P, "ack "
		              + HtmlUtil.createHref(returnUrl, entry.getTitle().getPlainText()));
		      
	         // HtmlUtil.createHref("", entry.getTitle().getPlainText()); 
	          resp.getWriter().print(loginHtml);
	          resp.getWriter().print("</P>");
	          
	          
	          
	        }
	        
	        
		      
	        
		}
	      catch(AuthenticationException e) {
	          System.err.println("Authentication exception;");
	        }
	        catch(MalformedURLException e) {
	          e.printStackTrace();
	        }
	        catch(ServiceException e) {
	          System.err.println("Service Exception");
	        }
	        catch(IOException e) {
	          e.printStackTrace();
	        }
	        

		    
		}
	}
	
