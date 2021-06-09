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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.podaci.MyAirport;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author NWTiS_2
 */
public class MyAirportsLogDAO {

    /**
     * provjerava postoji li zapis aerodroma za zadani datum
     *
     * @param a aerodrom koji se provjerava
     * @param datumPreuzimanja
     * @param pbp
     * @return
     */
    public boolean provjeriPostojanjeZapisa(MyAirport a, Date datumPreuzimanja, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String upit = "SELECT count(*) as rowCount FROM myairportslog WHERE ident = ? and flightdate = ?";
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, a.getIdent());
                s.setString(2, sdf.format(datumPreuzimanja));
                ResultSet rs = s.executeQuery();
                rs.next();
                int brojPronadenih = rs.getInt("rowCount");
                return brojPronadenih > 0;

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
     * dodaje zapis o aerodromu
     *
     * @param a aerodrom koji se dodaje
     * @param datumPreuzimanja
     * @param pbp
     * @return
     */
    public boolean dodajZapisOAerodromu(MyAirport a, Date datumPreuzimanja, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String upit = "INSERT INTO myairportslog (ident, flightdate, timeStored) "
                + "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, a.getIdent());
                s.setString(2, sdf.format(datumPreuzimanja));

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
