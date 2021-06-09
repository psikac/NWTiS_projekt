/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author NWTiS_1
 */
public class AirplaneDAO {

    /**
     *
     * @param al avion koji se zeli dodati
     * @param pbp
     * @return je li avion uspjesno dodan
     */
    public boolean dodajLet(AvionLeti al, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO airplanes (icao24, firstseen, estDepartureAirport, lastSeen, estArrivalAirport,"
                + " callsign, estDepartureAirportHorizDistance, estDepartureAirportVertDistance,"
                + "estArrivalAirportHorizDistance, estArrivalAirportVertDistance, departureAirportCandidatesCount,"
                + "arrivalAirportCandidatesCount, timeStored) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, al.getIcao24());
                s.setString(2, Integer.toString(al.getFirstSeen()));
                s.setString(3, al.getEstDepartureAirport());
                s.setString(4, Integer.toString(al.getLastSeen()));
                s.setString(5, al.getEstArrivalAirport());
                s.setString(6, al.getCallsign());
                s.setString(7, Integer.toString(al.getEstDepartureAirportHorizDistance()));
                s.setString(8, Integer.toString(al.getEstDepartureAirportVertDistance()));
                s.setString(9, Integer.toString(al.getEstArrivalAirportHorizDistance()));
                s.setString(10, Integer.toString(al.getEstArrivalAirportVertDistance()));
                s.setString(11, Integer.toString(al.getDepartureAirportCandidatesCount()));
                s.setString(12, Integer.toString(al.getArrivalAirportCandidatesCount()));

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

    public List<AvionLeti> dajLetove(String icao, String datum, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "select * from airplanes where "
                + "(SELECT FROM_UNIXTIME(firstseen, '%Y-%m-%d'))=? "
                + "and estDepartureAirport=?;";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            List<AvionLeti> letovi = new ArrayList<>();
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, datum);
                s.setString(2, icao);

                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String icao24 = rs.getString("icao24");
                    int firstSeen = Integer.parseInt(rs.getString("firstSeen"));
                    String estDepartureAirport = rs.getString("estDepartureAirport");
                    int lastSeen = Integer.parseInt(rs.getString("lastSeen"));
                    String estArrivalAirport = rs.getString("estArrivalAirport");
                    String callsign = rs.getString("callsign");
                    int depHorizDist = Integer.parseInt(rs.getString("estDepartureAirportHorizDistance"));
                    int depVertDist = Integer.parseInt(rs.getString("estDepartureAirportVertDistance"));
                    int arrHorizDist = Integer.parseInt(rs.getString("estArrivalAirportHorizDistance"));
                    int arrVertDist = Integer.parseInt(rs.getString("estArrivalAirportVertDistance"));
                    int depCandCount = Integer.parseInt(rs.getString("departureAirportCandidatesCount"));
                    int arrCandCount = Integer.parseInt(rs.getString("arrivalAirportCandidatesCount"));
                    AvionLeti al = new AvionLeti(icao24, firstSeen, estDepartureAirport, lastSeen, 
                            estArrivalAirport, callsign, depHorizDist, depVertDist, arrHorizDist, 
                            arrVertDist, depCandCount, arrCandCount);

                    letovi.add(al);
                }
                return letovi;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return null;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return null;
        }
    }

}
