/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_2.podaci;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.MeteoOriginal;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 *
 * @author NWTiS_1
 */
public class MeteoDAO {

    public boolean dodajMeteoPodatak(MeteoOriginal podaci, String ident, PostavkeBazaPodataka pbp) {

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO meteo (ident, latitude, longitude, weatherId, "
                + "weatherMain, weatherDescription, weatherIcon, base, mainTemp, "
                + "mainFeelsLike, mainPressure, mainHumidity, mainTempMin, mainTempMax, "
                + "mainSeaLevel, mainGrndLevel, visibility, windSpeed, windDeg, "
                + "windGust, cloudsAll, rain1h, rain3h, snow1h, snow3h, date, "
                + "sysType, sysId, sysMessage, sysCountry, sysSunrise, sysSunset, "
                + "timezone, cityId, cityName, cod, timestamp) VALUES (?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, ident);
                s.setString(2, podaci.getCoordLat());
                s.setString(3, podaci.getCoordLon());
                s.setString(4, podaci.getWeatherId().toString());
                s.setString(5, podaci.getWeatherMain());
                s.setString(6, getWeatherDescription(podaci));
                s.setString(7, getWeatherIcon(podaci));
                s.setString(8, podaci.getBase());
                s.setString(9, podaci.getMainTemp().toString());
                s.setString(10, podaci.getMainFeels_like().toString());
                s.setString(11, podaci.getMainPressure().toString());
                s.setString(12, Integer.toString(podaci.getMainHumidity()));
                s.setString(13, podaci.getMainTemp_min().toString());
                s.setString(14, podaci.getMainTemp_max().toString());
                s.setString(15, floatToString(podaci.getMainSea_level()));
                s.setString(16, floatToString(podaci.getMainGrnd_level()));
                s.setString(17, Integer.toString(podaci.getVisibility()));
                s.setString(18, podaci.getWindSpeed().toString());
                s.setString(19, Integer.toString(podaci.getWindDeg()));
                s.setString(20, floatToString(podaci.getWindGust()));
                s.setString(21, Integer.toString(podaci.getCloudsAll()));
                s.setString(22, floatToString(podaci.getRain1h()));
                s.setString(23, floatToString(podaci.getRain3h()));
                s.setString(24, floatToString(podaci.getSnow1h()));
                s.setString(25, floatToString(podaci.getSnow3h()));
                s.setString(26, podaci.getDt().toString());
                s.setString(27, Integer.toString(podaci.getSysType()));
                s.setString(28, Integer.toString(podaci.getSysId()));
                s.setString(29, floatToString(podaci.getSysMessage()));
                s.setString(30, podaci.getSysCountry());
                s.setString(31, Integer.toString(podaci.getSysSunrise()));
                s.setString(32, Integer.toString(podaci.getSysSunset()));
                s.setString(33, Integer.toString(podaci.getTimezone()));
                s.setString(34, Integer.toString(podaci.getCityId()));
                s.setString(35, podaci.getCityName());
                s.setString(36, Integer.toString(podaci.getCod()));
                s.setString(36, Integer.toString(podaci.getCod()));
                s.setString(37, longToTimestamp(podaci.getDt()));

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return false;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return false;
        }
    }

    public List<MeteoOriginal> dajMeteoPodatke(String icao, String dan, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "select * from meteo where "
                + "(SELECT FROM_UNIXTIME(date, '%Y-%m-%d'))=? "
                + "and ident=?;";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            List<MeteoOriginal> podaci = new ArrayList<>();
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, dan);
                s.setString(2, icao);

                ResultSet rs = s.executeQuery();
                while (rs.next()) {

                    String ident = rs.getString("ident");
                    long dt = Long.parseLong(rs.getString("date"));
                    String lon = rs.getString("longitude");
                    String lat = rs.getString("latitude");
                    int wId = Integer.parseInt(rs.getString("weatherId"));
                    String wMain = rs.getString("weatherMain");
                    String wDesc = rs.getString("weatherDescription");
                    String wIcon = rs.getString("weatherIcon");
                    String base = rs.getString("base");
                    Float mainTemp = Float.parseFloat(rs.getString("mainTemp"));
                    Float mainFeelsLike = stringToFloat(rs.getString("mainFeelsLike"));
                    Float mainPressure = stringToFloat(rs.getString("mainPressure"));
                    int mainHumidity = Integer.parseInt(rs.getString("mainHumidity"));
                    Float mainTempMin = stringToFloat(rs.getString("mainTempMin"));
                    Float mainTempMax = stringToFloat(rs.getString("mainTempMax"));
                    Float mainSeaLevel = stringToFloat(rs.getString("mainSeaLevel"));
                    Float mainGrndLevel = stringToFloat(rs.getString("mainGrndLevel"));
                    int visibility = Integer.parseInt(rs.getString("visibility"));
                    Float windSpeed = stringToFloat(rs.getString("windSpeed"));
                    int windDeg = Integer.parseInt(rs.getString("windDeg"));
                    Float windGust = stringToFloat(rs.getString("windGust"));
                    int cloudsAll = Integer.parseInt(rs.getString("cloudsAll"));
                    Float rain1h = stringToFloat(rs.getString("rain1h"));
                    Float rain3h = stringToFloat(rs.getString("rain3h"));
                    Float snow1h = stringToFloat(rs.getString("snow1h"));
                    Float snow3h = stringToFloat(rs.getString("snow3h"));
                    int sysType = Integer.parseInt(rs.getString("sysType"));
                    int sysId = Integer.parseInt(rs.getString("sysId"));
                    Float sysMessage = stringToFloat(rs.getString("sysMessage"));
                    String sysCountry = rs.getString("sysCountry");
                    int sysSunrise = Integer.parseInt(rs.getString("sysSunrise"));
                    int sysSunset = Integer.parseInt(rs.getString("sysSunset"));
                    int timezone = Integer.parseInt(rs.getString("timezone"));
                    int cityId = Integer.parseInt(rs.getString("cityId"));
                    String cityName = rs.getString("cityName");
                    int cod = Integer.parseInt(rs.getString("cod"));

                    MeteoOriginal m = new MeteoOriginal(ident, dt, lon, lat,
                            wId, wMain, wDesc, wIcon, base, mainTemp, mainFeelsLike, mainPressure,
                            mainHumidity, mainTempMin, mainTempMax, mainSeaLevel, mainGrndLevel,
                            visibility, windSpeed, windDeg, windGust, cloudsAll,
                           rain1h, rain3h, snow1h,snow3h, sysType,
                            sysId, sysMessage, sysCountry, sysSunrise, sysSunset,
                            timezone, cityId, cityName, cod, "");

                    podaci.add(m);
                }
                return podaci;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return null;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return null;
        }
    }
    
     public MeteoOriginal dajMeteoPodatakeZaVrijeme(String icao, String vrijeme, PostavkeBazaPodataka pbp) {
         String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT * FROM meteo where date >= ? and ident=? order by date limit 1;";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, vrijeme);
                s.setString(2, icao);

                ResultSet rs = s.executeQuery();
                while (rs.next()) {

                    String ident = rs.getString("ident");
                    long dt = Long.parseLong(rs.getString("date"));
                    String lon = rs.getString("longitude");
                    String lat = rs.getString("latitude");
                    int wId = Integer.parseInt(rs.getString("weatherId"));
                    String wMain = rs.getString("weatherMain");
                    String wDesc = rs.getString("weatherDescription");
                    String wIcon = rs.getString("weatherIcon");
                    String base = rs.getString("base");
                    Float mainTemp = Float.parseFloat(rs.getString("mainTemp"));
                    Float mainFeelsLike = stringToFloat(rs.getString("mainFeelsLike"));
                    Float mainPressure = stringToFloat(rs.getString("mainPressure"));
                    int mainHumidity = Integer.parseInt(rs.getString("mainHumidity"));
                    Float mainTempMin = stringToFloat(rs.getString("mainTempMin"));
                    Float mainTempMax = stringToFloat(rs.getString("mainTempMax"));
                    Float mainSeaLevel = stringToFloat(rs.getString("mainSeaLevel"));
                    Float mainGrndLevel = stringToFloat(rs.getString("mainGrndLevel"));
                    int visibility = Integer.parseInt(rs.getString("visibility"));
                    Float windSpeed = stringToFloat(rs.getString("windSpeed"));
                    int windDeg = Integer.parseInt(rs.getString("windDeg"));
                    Float windGust = stringToFloat(rs.getString("windGust"));
                    int cloudsAll = Integer.parseInt(rs.getString("cloudsAll"));
                    Float rain1h = stringToFloat(rs.getString("rain1h"));
                    Float rain3h = stringToFloat(rs.getString("rain3h"));
                    Float snow1h = stringToFloat(rs.getString("snow1h"));
                    Float snow3h = stringToFloat(rs.getString("snow3h"));
                    int sysType = Integer.parseInt(rs.getString("sysType"));
                    int sysId = Integer.parseInt(rs.getString("sysId"));
                    Float sysMessage = stringToFloat(rs.getString("sysMessage"));
                    String sysCountry = rs.getString("sysCountry");
                    int sysSunrise = Integer.parseInt(rs.getString("sysSunrise"));
                    int sysSunset = Integer.parseInt(rs.getString("sysSunset"));
                    int timezone = Integer.parseInt(rs.getString("timezone"));
                    int cityId = Integer.parseInt(rs.getString("cityId"));
                    String cityName = rs.getString("cityName");
                    int cod = Integer.parseInt(rs.getString("cod"));

                    MeteoOriginal m = new MeteoOriginal(ident, dt, lon, lat,
                            wId, wMain, wDesc, wIcon, base, mainTemp, mainFeelsLike, mainPressure,
                            mainHumidity, mainTempMin, mainTempMax, mainSeaLevel, mainGrndLevel,
                            visibility, windSpeed, windDeg, windGust, cloudsAll,
                           rain1h, rain3h, snow1h,snow3h, sysType,
                            sysId, sysMessage, sysCountry, sysSunrise, sysSunset,
                            timezone, cityId, cityName, cod, "");

                   return m;
                }

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return null;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return null;
        }
        return null;
    }


    private String vratiDatumString(Date datum) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = dateFormat.format(datum);
        return strDate;
    }

    private String floatToString(Float broj) {
        if (broj == null) {
            return null;
        }
        return broj.toString();
    }
    
    private float stringToFloat(String str) {
        if (str == null) {
            return 0;
        }
        return Float.parseFloat(str);
    }
    

    private String getWeatherIcon(MeteoOriginal podaci) {
        String json = podaci.getJsonMeteo();
        JsonElement parsedJSON = JsonParser.parseString(json);
        JsonObject fetchedJSON = parsedJSON.getAsJsonObject();
        //weather is an array so get it as array not as string
        JsonArray jarray = fetchedJSON.getAsJsonArray("weather");
        // OR you use loop if you want all main data
        JsonObject jobject = jarray.get(0).getAsJsonObject();
        String icon = jobject.get("icon").getAsString();
        return icon;
    }

    private String getWeatherDescription(MeteoOriginal podaci) {
        String json = podaci.getJsonMeteo();
        JsonElement parsedJSON = JsonParser.parseString(json);
        JsonObject fetchedJSON = parsedJSON.getAsJsonObject();
        //weather is an array so get it as array not as string
        JsonArray jarray = fetchedJSON.getAsJsonArray("weather");
        // OR you use loop if you want all main data
        JsonObject jobject = jarray.get(0).getAsJsonObject();
        String description = jobject.get("description").getAsString();
        return description;
    }

    private String longToTimestamp(long ts) {
        Timestamp timestamp = new Timestamp(ts * 1000);
        return timestamp.toString();
    }

   
}
