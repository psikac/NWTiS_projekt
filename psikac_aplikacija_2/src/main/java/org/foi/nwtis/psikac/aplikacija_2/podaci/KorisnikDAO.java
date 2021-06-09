/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_2.podaci;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.psikac.aplikacija_2.klijenti.Klijent;

import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author NWTiS_2
 */
public class KorisnikDAO {

    public String autenticirajKorisnika(String korisnik, String lozinka, PostavkeBazaPodataka pbp) {
        String adresaServera = pbp.dajPostavku("server.korisnika.adresa");
        int portServera = Integer.parseInt(pbp.dajPostavku("server.korisnika.port"));
        Klijent klijent = new Klijent();

        String idSjednice;

        String naredba = "AUTHEN " + korisnik + " " + lozinka;
        return klijent.izvrsiKomandu(naredba, adresaServera, portServera);

    }

    /**
     *
     * @param korisnik korisnikcko ime korisnika kojeg zelimo dohvatiti
     * @param lozinka lozinka korisnika kojeg zelimo dohvatiti
     * @param prijava parametar oznacava radi li se o situaciji prijave ili ne
     * @param pbp
     * @return
     */
    public String dohvatiKorisnika(String korisnik, String lozinka, String korisnikTrazi, PostavkeBazaPodataka pbp) {
        String adresaServera = pbp.dajPostavku("server.korisnika.adresa");
        int portServera = Integer.parseInt(pbp.dajPostavku("server.korisnika.port"));
        Klijent klijent = new Klijent();

        String idSjednice;

        String naredba = "AUTHEN " + korisnik + " " + lozinka;
        String rezultat = klijent.izvrsiKomandu(naredba, adresaServera, portServera);
        String[] odgovor = rezultat.split(" ");
        switch (odgovor[0]) {
            case "ERROR":
                return rezultat;
            case "OK":
                idSjednice = odgovor[1];
                naredba = "LIST " + korisnik + " " + idSjednice + " " + korisnikTrazi;
                rezultat = klijent.izvrsiKomandu(naredba, adresaServera, portServera);
                return rezultat;
            default:
                return null;
        }
    }

    /**
     *
     * @param pbp
     * @return
     */
    public String dohvatiSveKorisnike(String korisnik, String lozinka, PostavkeBazaPodataka pbp) {
        String adresaServera = pbp.dajPostavku("server.korisnika.adresa");
        int portServera = Integer.parseInt(pbp.dajPostavku("server.korisnika.port"));
        System.out.println(adresaServera + " " + portServera);
        Klijent klijent = new Klijent();

        String idSjednice;

        String naredba = "AUTHEN " + korisnik + " " + lozinka;
        String rezultat = klijent.izvrsiKomandu(naredba, adresaServera, portServera);
        System.out.println("autentifikacija " + rezultat);
        String[] odgovor = rezultat.split(" ");
        switch (odgovor[0]) {
            case "ERROR":
                return rezultat;
            case "OK":
                idSjednice = odgovor[1];
                naredba = "LISTALL " + korisnik + " " + idSjednice;
                System.out.println("naredba " + naredba);
                rezultat = klijent.izvrsiKomandu(naredba, adresaServera, portServera);
                System.out.println("korisnici " + rezultat);
                return rezultat;
            default:
                return null;
        }
    }

    public String dodajKorisnika(Korisnik k, PostavkeBazaPodataka pbp) {
        String adresaServera = pbp.dajPostavku("server.korisnika.adresa");
        int portServera = Integer.parseInt(pbp.dajPostavku("server.korisnika.port"));
        System.out.println(adresaServera + " " + portServera);
        Klijent klijent = new Klijent();

        

        String naredba = "ADD " + k.getKorisnik() + " " + k.getLozinka() + " \"" + k.getPrezime() + "\" \"" + k.getIme() + "\"";
        String rezultat = klijent.izvrsiKomandu(naredba, adresaServera, portServera);
        System.out.println("dodavanje " + rezultat);
        return rezultat;
    }

   

}
