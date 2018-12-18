package com.example.nadar.velibservice.model;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ListeDesStationsVelib implements ListeDesStationsVelibI, Iterable<StationVelib>{

  
  //private long time;                           // une estampille
  private  Map<Integer,StationVelib> stations;  // ad�quation num�ro -> station
  private  Map<String,Integer>       noms;      // ad�quation nom de station -> num�ro

  private ArrayList<StationVelib> velibArrayList;

  
  public ListeDesStationsVelib() throws Exception{		
	this.stations = new TreeMap<Integer,StationVelib>();
	this.velibArrayList=new ArrayList<>();
	this.noms = new TreeMap<String,Integer>();
  }

    public ArrayList<StationVelib> getVelibArrayList() {
        return velibArrayList;
    }

    public List<String> lesNomsDesStations(){
    return new ArrayList<String>(noms.keySet());
  }
    
  public StationVelib lireStation(Integer numero){
    return stations.get(numero);
  }
  
  public StationVelib lireStation(String nomDeLaStation){
	return stations.get(noms.get(nomDeLaStation));
  }
  
  public InfoStation info(StationVelib s){ // cf. facade
    return new InfoStation(s);
  }
  
  public InfoStation info(int numero){  // idem
    return info(stations.get(numero));
  }
  
  public Iterator<StationVelib> iterator(){
    return stations.values().iterator();
  }
  
  private void viderLesTables(){
    stations.clear();
    noms.clear();
  }
  
  
  
  
  public void chargerDepuisXML(URL url) throws Exception{
	try{ 
      viderLesTables();
      new ParserXML(url.openStream());
    }catch(Exception e){
	  e.printStackTrace();
	  throw e;
    }
    
  }
  
  
    public void chargerDepuisXML(InputStream in)throws Exception{
     viderLesTables();
     new ParserXML(in);
     try{ 
       in.close();
      }catch(Exception e){
    	  e.printStackTrace();
    	  throw e;
      }
  }
  
  

  
  private class ParserXML  extends DefaultHandler implements Serializable{
    
    public ParserXML(InputStream in) throws Exception{
      SAXParserFactory spf = SAXParserFactory.newInstance();
	  SAXParser sp = spf.newSAXParser();
	  XMLReader xr = sp.getXMLReader();
	  xr.setContentHandler(this);
      xr.parse(new InputSource(in));
    }
    
    public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException {
  		super.startElement(uri, localName, qName, attributes);
  		
  		if(qName.equals("marker")){
  			StationVelib station = new StationVelib();
  		
  			station.setName(attributes.getValue("name"));
  			station.setNumber(Integer.parseInt(attributes.getValue("number")));
  			station.setAddress(attributes.getValue("address"));
  			station.setFullAddress(attributes.getValue("fullAddress"));
  			station.setLatitude(Double.parseDouble(attributes.getValue("lat")));
  			station.setLongitude(Double.parseDouble(attributes.getValue("lng")));
  			
  			if(attributes.getValue("bonus").equals("1"))
  				station.setBonus(true);
  			 else
  				station.setBonus(false);
  			
  			if(attributes.getValue("open").equals("1"))
  				station.setOpen(true);
  			 else
  				station.setOpen(false);
  			
		  stations.put(station.getNumber(),station);
            velibArrayList.add(station);
		  noms.put(station.getName(),station.getNumber());
		  }
	  }
  }
  
}
