/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_1.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.psikac.aplikacija_1.modeli.Korisnik;
import org.foi.nwtis.psikac.aplikacija_1.modeli.Podrucje;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author NWTiS_1
 */
public class OvlastiDAO {
    public int postojiPodrucje(String korisnikTrazi, String podrucjeRada, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT status FROM ovlasti WHERE korisnickoIme = ? and podrucjeRada = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnikTrazi);
                s.setString(2, podrucjeRada);
                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String st = rs.getString("status");
                    
                    if(st!=null){
                        boolean status = Boolean.parseBoolean(rs.getString("status"));
                        if(status)
                            return 1;
                        return 2;
                    }
                    System.out.println("ne postoji");
                    return 0;
                }

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return -1;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return -1;
        }
        return 0;
    }

    public boolean dodajPodrucje(String korisnikTrazi, String podrucjeRada, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
         String upit = "INSERT INTO ovlasti (korisnickoIme, podrucjeRada, status) "
                + "VALUES (?, ?, TRUE)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnikTrazi);
                s.setString(2, podrucjeRada);
             

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

    public boolean aktivirajPodrucje(String korisnikTrazi, String podrucjeRada, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE ovlasti SET status = TRUE WHERE korisnickoIme = ? AND "
                + "podrucjeRada = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnikTrazi);
                s.setString(2, podrucjeRada);

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

    public boolean deaktivirajPodrucje(String korisnikTrazi, String podrucjeRada, PostavkeBazaPodataka pbp) {
         String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE ovlasti SET status = FALSE WHERE korisnickoIme = ? AND "
                + "podrucjeRada = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnikTrazi);
                s.setString(2, podrucjeRada);

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

    public List<Podrucje> dohvatiPodrucja(String korisnikTrazi, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT korisnickoIme, podrucjeRada, status"
                + " FROM OVLASTI WHERE korisnickoIme = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Podrucje> podrucja = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnikTrazi);

                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String korisnickoIme = rs.getString("korisnickoIme");
                    String podrucjeRada = rs.getString("podrucjeRada");
                    Boolean status = Boolean.parseBoolean(rs.getString("status"));
                    Podrucje p = new Podrucje(korisnickoIme, podrucjeRada, status);

                    podrucja.add(p);
                }
                return podrucja;

            } catch (SQLException ex) {
                 System.out.println("SQL ERROR:" + ex.getMessage());
                 return new ArrayList<>();
            }
        } catch (ClassNotFoundException ex) {
             System.out.println("SQL ERROR:" + ex.getMessage());
             return new ArrayList<>();
        }
    }

    public boolean postojiAktivnoPodrucje(String korisnickoIme, String podrucjeRada, PostavkeBazaPodataka pbp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
