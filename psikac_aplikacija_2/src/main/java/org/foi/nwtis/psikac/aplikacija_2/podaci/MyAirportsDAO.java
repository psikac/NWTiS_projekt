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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.podaci.MyAirport;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author NWTiS_2
 */
public class MyAirportsDAO {

    /**
     *
     * @param pbp
     * @return vraca sve aerodrome koje korisnik prati
     */
    public HashMap<String, MyAirport> dohvatiSveMojeAerodrome(PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ident, username FROM myairports";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            HashMap<String, MyAirport> mojiAerodromi = new HashMap<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String user = rs.getString("username");
                    MyAirport myA = new MyAirport(ident, user, false);

                    mojiAerodromi.put(myA.getIdent(), myA);
                }
                return mojiAerodromi;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return null;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return null;
        }
    }

    /**
     *
     * @param pbp
     * @return vraca sve aerodrome koje korisnik prati
     */
    public List<String> dohvatiAerodromeZaIcao(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT username FROM myairports WHERE ident = ?";
        ArrayList<String> response = new ArrayList<>();
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, icao);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String user = rs.getString("username");

                    System.out.println("user: " + user);
                    response.add(user);
                }
                return response;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return new ArrayList<>();
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * dodaje korisniku aerodrom koji zeli pratiti
     *
     * @param a
     * @param k
     * @param lozinka
     * @param pbp
     * @return
     */
    public boolean dodajMojAerodrom(Aerodrom a, String k, String lozinka, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO myairports (ident,username,timeStored)"
                + " VALUES(?,?,current_timestamp)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, a.getIcao());
                s.setString(2, k);

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

    public boolean provjeriZapis(Aerodrom a, String k, String lozinka, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT count(*) FROM myairports WHERE ident = ? and username = ?";
        ArrayList<String> response = new ArrayList<>();
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, a.getIcao());
                s.setString(2, k);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    int broj = rs.getInt(1);
                    return broj == 1;

                }
                return false;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return false;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return false;
        }
    }

    /**
     * brise aerodrom koji korisnik vise ne zeli pratiti
     *
     * @param a
     * @param k
     * @param lozinka
     * @param pbp
     * @return
     */
    public boolean izbrisiZadaniAerodrom(Aerodrom a, String k, String lozinka, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "DELETE FROM myairports WHERE ident = ? and username = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, a.getIcao());
                s.setString(2, k);

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
}
