package com.example.nadar.velibservice.model;


import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;



public class InfoStation implements Serializable{
  private static final long serialVersionUID = 5263116456578091144L;
//   <station>
//   <available>21</available>
//   <free>10</free>
//   <total>31</total>
//   <ticket>1</ticket>
//   </station>
  private long time;
  private int available;
  private int free;
  private int total;
  private boolean ticket;
  

  
  public InfoStation(StationVelib st){
    this(st.getNumber());
  }
  
  public InfoStation(int numeroDeStation){
    this.time = System.currentTimeMillis();
    try{
    	Log.i("InfoStation","" + numeroDeStation);
      URL url = new	 URL("http://www.velib.paris.fr/service/stationdetails/"+numeroDeStation);
      new ParserXML(url.openStream());
      //copierDansUnFichier(numeroDeStation,url.openStream()); // sauvegarde au cas o�
    }catch(Exception e){
    	// en mode d�grad�
        setAvailable(20);
        setFree(10);
        setTotal(30);
     
    }
     
  }
  

  
  public int getAvailable(){
    return available;
  }
  public int getFree(){
    return free;
  }
  public int getTotal(){
    return total;
  }
  public boolean getTicket(){
    return ticket;
  }
  
  public void setAvailable(int available){
    this.available = available;
  }
  
  public void setFree(int free){
    this.free = free;
  }
  public void setTotal(int total){
    this.total = total;
  }
  
  public void setTicket(boolean ticket){
    this.ticket = ticket;
  }
  
  public String toString(){
	  return "<" + getAvailable() + "," + getFree() + ">";
	 }
  
  private class ParserXML extends DefaultHandler implements Serializable {
    private StringBuffer current;
    	
    public ParserXML(InputStream in){
    	try {
    		SAXParserFactory spf = SAXParserFactory.newInstance();
    		SAXParser sp = spf.newSAXParser();
    		XMLReader xr = sp.getXMLReader();
    		xr.setContentHandler(this);
    		xr.parse(new InputSource(in));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		  super.startElement(uri, localName, qName, attributes);
		  current = new StringBuffer();
	  }
	

	  public void characters(char[] ch, int start, int length) throws SAXException {
		  super.characters(ch, start, length);
		  current.append(new String(ch, start, length));
	  }

    public void endElement(String uri, String localName, String qName)
    		throws SAXException {
    	super.endElement(uri, localName, qName);
    	Log.i("qname",qName);
    	if(qName.equals("available")){
    		available = Integer.parseInt(current.toString());
    	}
    	if(qName.equals("free")){
    		free = Integer.parseInt(current.toString());
    	}
    	if(qName.equals("total")){
    		total = Integer.parseInt(current.toString());
    	}
    	if(qName.equals("ticket")){
    		ticket = Boolean.parseBoolean(current.toString());
    	}
    }
  }
 private static void copierDansUnFichier(int numeroDeStation, InputStream in){
  try{
  
  //For Append the file.
//  OutputStream out = new FileOutputStream(f2,true);

  //For Overwrite the file.
    OutputStream out = new FileOutputStream(new File("stationdetails/"+numeroDeStation + ".xml"));

    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0){
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
    //System.out.println("File copied.");
    }
    catch(FileNotFoundException ex){
      //System.out.println(ex.getMessage() + " in the specified directory.");
  }
  catch(IOException e){
    //System.out.println(e.getMessage());  
  }
  }
  
}
