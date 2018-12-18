package com.example.nadar.velibservice.model;


import java.io.InputStream;
import java.net.URL;
import java.util.List;


public interface ListeDesStationsVelibI {

  public static final String URL_VELIB_ALL = "http://www.velib.paris.fr/service/carto";
  public static final String URL_VELIB_INFO= "http://www.velib.paris.fr/service/stationdetails/"; //  /number

  
  public List<String> lesNomsDesStations();
  public StationVelib lireStation(Integer numero);
  public StationVelib lireStation(String nomDeLaStation);
		  
 
  public InfoStation info(StationVelib s);	  
  public InfoStation info(int numero);
  public void chargerDepuisXML(URL url) throws Exception;
  public void chargerDepuisXML(InputStream in)throws Exception;
}
