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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.psikac.aplikacija_1.modeli.Korisnik;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author NWTiS_2
 */
public class KorisnikDAO {

    /**
     *
     * @param korisnik korisnikcko ime korisnika kojeg zelimo dohvatiti
     * @param lozinka lozinka korisnika kojeg zelimo dohvatiti
     * @param prijava parametar oznacava radi li se o situaciji prijave ili ne
     * @param pbp
     * @return
     */
    public Korisnik dohvatiKorisnika(String korisnik, String lozinka, Boolean prijava, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ime, prezime, korisnickoIme, lozinka "
                + "FROM korisnici WHERE korisnickoIme = ?";

        if (prijava) {
            upit += " and lozinka = ?";
        }

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnik);
                if (prijava) {
                    s.setString(2, lozinka);
                }
                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String korisnik1 = rs.getString("korisnickoIme");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");

                    Korisnik k = new Korisnik(korisnik1, "******", prezime, ime);
                    return k;
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
     * @param pbp
     * @return
     */
    public List<Korisnik> dohvatiSveKorisnike(PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT korisnickoIme, prezime, ime FROM korisnici";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Korisnik> korisnici = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String korisnickoIme = rs.getString("korisnickoIme");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    Korisnik k = new Korisnik(korisnickoIme, "", prezime, ime);

                    korisnici.add(k);
                }
                return korisnici;

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
     * @param k korisnik cije podatke zelimo azurirati
     * @param lozinka
     * @param pbp
     * @return vraca je li korisnik uspjesno azuriran
     */
    public boolean azurirajKorisnika(Korisnik k, String lozinka, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE korisnici SET ime = ?, prezime = ?, lozinka = ? WHERE korisnik = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, k.getIme());
                s.setString(2, k.getPrezime());
                s.setString(3, lozinka);
                s.setString(4, k.getKorisnickoIme());

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

    /**
     *
     * @param k korisnik kojeg se zeli dodati
     * @param pbp
     * @return je li korisnik uspjesno dodan
     */
    public boolean dodajKorisnika(Korisnik k, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO korisnici (ime, prezime, korisnickoIme, lozinka) "
                + "VALUES (?, ?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, k.getIme());
                s.setString(2, k.getPrezime());
                s.setString(3, k.getKorisnickoIme());
                s.setString(4, k.getLozinka());

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

    /**
     *
     * @param icao
     * @param pbp
     * @return
     */
    public List<Korisnik> dohvatiKorisnikeZaIcao(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ime, prezime, korisnickoIme, lozinka FROM KORISNICI "
                + "k JOIN MYAIRPORTS m ON k.KORISNIK = m.USERNAME "
                + "WHERE m.IDENT=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Korisnik> korisnici = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, icao);

                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String korisnik1 = rs.getString("korisnik");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    Korisnik k = new Korisnik(korisnik1, "******", prezime, ime);

                    korisnici.add(k);
                }
                return korisnici;

            } catch (SQLException ex) {
                System.out.println("SQL ERROR:" + ex.getMessage());
                return null;
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found:" + ex.getMessage());
            return null;
        }
    }

    public boolean postojiKorisnik(Korisnik k, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT count(*) as brojPojava "
                + "FROM korisnici WHERE korisnickoIme = ?";
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, k.getKorisnickoIme());
                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    int brojPojava = Integer.parseInt(rs.getString("brojPojava"));
                    if (brojPojava == 1) {
                        return true;
                    }
                }

                  } catch (SQLException ex) {
                 System.out.println("SQL ERROR:" + ex.getMessage());
                 return false;
            }
        } catch (ClassNotFoundException ex) {
             System.out.println("class not found:" + ex.getMessage());
             return false;
        }
        return false;
    }

}
