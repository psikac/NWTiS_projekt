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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author NWTiS_2
 */
public class AerodromDAO {

    /**
     *
     * @param naziv predstavlja naziv aerodroma
     * @param drzava ime drzave
     * @param pbp
     * @return vraca listu svih aerodroma koji odgovaraju parametrima
     */
    public List<Aerodrom> dohvatiSveAerodrome(String naziv, String drzava, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        boolean postojiNaziv = (naziv != null && !naziv.trim().isEmpty());
        boolean postojiDrzava = (drzava != null && !drzava.trim().isEmpty());
        String upit = "SELECT ident, name, iso_country, coordinates FROM airports";
        if (postojiNaziv && postojiDrzava) {
            upit += " WHERE name LIKE ? AND iso_country = ?";
        } else if (postojiNaziv && !postojiDrzava) {
            upit += " WHERE name LIKE ?";
        } else if (!postojiNaziv && postojiDrzava) {
            upit += " WHERE iso_country = ?";
        }
        try {
            Class.forName(pbp.getDriverDatabase(url));
            List<Aerodrom> aerodromi = new ArrayList<>();
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {
                if (postojiNaziv && postojiDrzava) {
                    s.setString(1, naziv + "%");
                    s.setString(2, drzava);
                } else if (postojiNaziv && !postojiDrzava) {
                    s.setString(1, naziv + "%");
                } else if (!postojiNaziv && postojiDrzava) {
                    s.setString(1, drzava);
                }
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String nazivAeordroma = rs.getString("name");
                    String drzavaAerodroma = rs.getString("iso_country");
                    String[] koordinate = rs.getString("coordinates").split(",");
                    Lokacija lokacija = new Lokacija(koordinate[1], koordinate[0]);
                    Aerodrom a = new Aerodrom(ident, nazivAeordroma, drzavaAerodroma, lokacija);

                    aerodromi.add(a);
                }
                return aerodromi;

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
     * @param icao
     * @param pbp
     * @return
     */
    public Aerodrom dohvatiAerodrom(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT ident, name, iso_country, coordinates FROM airports WHERE ident = ?";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, icao);

                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String nazivAeordroma = rs.getString("name");
                    String drzavaAerodroma = rs.getString("iso_country");
                    String[] koordinate = rs.getString("coordinates").split(",");
                    Lokacija lokacija = new Lokacija(koordinate[1], koordinate[0]);
                    Aerodrom a = new Aerodrom(ident, nazivAeordroma, drzavaAerodroma, lokacija);

                    return a;
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

    /**
     *
     * @param icao
     * @param pbp
     * @return
     */
    public int dohvatiBrojLetova(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT count(*) FROM airplanes JOIN airports "
                + "on estdepartureairport = ident "
                + "WHERE ident = ?";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, icao);
                ResultSet rs = s.executeQuery();
                rs.next();
                int brojLetova = rs.getInt(1);

                return brojLetova;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return -1;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return -1;
        }
    }

    /**
     *
     * @param pbp
     * @return
     */
    public List<Aerodrom> dohvatiSvePraceneAerodrome(PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT DISTINCT a.* FROM airports a JOIN myairports m ON m.ident = a.IDENT";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            List<Aerodrom> aerodromi = new ArrayList<>();
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String nazivAeordroma = rs.getString("name");
                    String drzavaAerodroma = rs.getString("iso_country");
                    String[] koordinate = rs.getString("coordinates").split(",");
                    Lokacija lokacija = new Lokacija(koordinate[1], koordinate[0]);
                    Aerodrom a = new Aerodrom(ident, nazivAeordroma, drzavaAerodroma, lokacija);

                    aerodromi.add(a);
                }
                return aerodromi;

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
     * @param pKorisnik korisnik za kojeg zelimo naci aerodrome
     * @param pbp
     * @return lista aerodroma koje korisnik prati
     */
    public List<Aerodrom> dohvatiAerodromeKorisnika(String pKorisnik, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT * FROM airports a JOIN myairports m ON a.IDENT=m.IDENT "
                + "WHERE m.USERNAME=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            List<Aerodrom> aerodromi = new ArrayList<>();
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, pKorisnik);

                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String nazivAeordroma = rs.getString("name");
                    String drzavaAerodroma = rs.getString("iso_country");
                    String[] koordinate = rs.getString("coordinates").split(",");
                    Lokacija lokacija = new Lokacija(koordinate[1], koordinate[0]);
                    Aerodrom a = new Aerodrom(ident, nazivAeordroma, drzavaAerodroma, lokacija);

                    aerodromi.add(a);
                }
                return aerodromi;

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
