/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_1.posluzitelji;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.foi.nwtis.psikac.aplikacija_1.modeli.Korisnik;
import org.foi.nwtis.psikac.aplikacija_1.modeli.Podrucje;
import org.foi.nwtis.psikac.aplikacija_1.modeli.Sjednica;
import org.foi.nwtis.psikac.aplikacija_1.podaci.KorisnikDAO;
import org.foi.nwtis.psikac.aplikacija_1.podaci.OvlastiDAO;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author NWTiS_1
 */
public class ServerKorisnika extends Thread {

    @Getter
    private boolean serverRadi = false;
    private static PostavkeBazaPodataka pbp;
    private int port;
    private static long sjednicaTrajanje;
    private static int maksBrojZahtjeva;
    private int maksDretvi;
    private int maksCekaca = 100;
    private ThreadPoolExecutor executor;
    private int brojDretve = 0;
    private ServerSocket ss;
    protected static ArrayList<Sjednica> sjednice = new ArrayList<>();
    private static int idSjednice = 0;

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        pokreniServer();
        super.run(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public ServerKorisnika(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }

    public void pokreniServer() {
        System.out.println("Server pokrenut");

        serverRadi = true;
        ucitajPostavke();

        System.out.println("Port: " + port);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(maksDretvi, maksDretvi,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        System.out.println("Kreiram socket: " + port);

        try {
            ss = new ServerSocket();
            ss.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(port);
            ss.bind(sa, maksCekaca);

            System.out.println("Socket kreiran: " + port);
            while (true) {
                Socket uticnica = ss.accept();
                System.out.println("Spojen klijent sa " + uticnica.getInetAddress());
                System.out.println("Broj aktivnih dretvi :" + executor.getActiveCount());
                if (executor.getActiveCount() < maksDretvi) {
                    executor.execute(new DretvaZahtjeva(uticnica, brojDretve++));
                } else {
                    vratiPogresku(uticnica);
                }
            }
        } catch (IOException ex) {
            String poruka = null;
            switch (ex.getMessage()) {
                case "Address already in use":
                    poruka = "ERROR 18: Vrata/port " + port + " su zauzeta";
                    break;
                default:
                    poruka = "ERROR 18: Doslo je do pogreske";
            }
            System.out.println(poruka);
        } finally {
            try {
                ss.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerKorisnika.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void zaustaviServer() {
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (ss != null) {
                ss.close();
            }

        } catch (IOException ex) {
            System.out.println("Greska pri zaustavljanju servera!");
        }
        System.out.println("Zaustavljam server");
    }

    public void ucitajPostavke() {
        this.port = Integer.parseInt(pbp.dajPostavku("port"));
        this.maksDretvi = Integer.parseInt(pbp.dajPostavku("maks.dretvi"));
        maksBrojZahtjeva = Integer.parseInt(pbp.dajPostavku("maks.broj.zahtjeva"));
        sjednicaTrajanje = Long.parseLong(pbp.dajPostavku("sjednica.trajanje"));

    }

    private void vratiPogresku(Socket uticnica) {
        try {
            InetAddress ia = uticnica.getInetAddress();
            int portKorisnika = uticnica.getPort();
            InputStream is = uticnica.getInputStream();
            OutputStream os = uticnica.getOutputStream();

            StringBuilder sb = new StringBuilder();
            while (true) {
                int i = is.read();
                if (i == -1) {
                    break;
                }
                sb.append((char) i);
            }
            uticnica.shutdownInput();
            String zahtjev = sb.toString();
            long vrijemeZahtjeva = System.currentTimeMillis();
            System.out.println("ZAHTJEV: '" + zahtjev + "'");
            String odgovor = "ERROR 01: Ne postoji slobodna dretva za obradu zahtjeva";
            os.write(odgovor.getBytes());
            os.flush();
            uticnica.shutdownOutput();
        } catch (IOException ex) {
            System.out.println("ERROR 17: Problem kod obrade zahtjeva");
        }
    }

    private static class DretvaZahtjeva extends Thread {

        private int brojDretve;
        private Socket uticnica;

        public DretvaZahtjeva() {
        }

        private DretvaZahtjeva(Socket uticnica, int brojDretve) {
            this.brojDretve = brojDretve;
            this.uticnica = uticnica;
        }

        @Override
        public void run() {
            obradiZahtjev();
            super.run(); //To change body of generated methods, choose Tools | Templates.
        }

        private void obradiZahtjev() {
            try {
                InetAddress ia = uticnica.getInetAddress();
                int portKorisnika = uticnica.getPort();
                System.out.println("psikac: " + brojDretve);
                InputStream is = uticnica.getInputStream();
                OutputStream os = uticnica.getOutputStream();

                StringBuilder sb = new StringBuilder();
                while (true) {
                    int i = is.read();
                    if (i == -1) {
                        break;
                    }
                    sb.append((char) i);
                }
                uticnica.shutdownInput();
                String zahtjev = sb.toString();
                long vrijemeZahtjeva = System.currentTimeMillis();
                System.out.println("ZAHTJEV: '" + zahtjev + "'");
                String odgovor = procitajZahtjev(zahtjev);
                System.out.println(odgovor);
                os.write(odgovor.getBytes());
                os.flush();
                uticnica.shutdownOutput();
            } catch (IOException ex) {
                System.out.println("ERROR 18: Problem kod obrade zahtjeva");
            }
        }

        protected String procitajZahtjev(String zahtjev) {
            System.out.println("Citam zahtjev");
            String sintaksa = pripremiRegex();
            Pattern pattern = Pattern.compile(sintaksa);
            Matcher m = pattern.matcher(zahtjev);
            System.out.println("Provjeravam zahtjev");
            if (m.matches()) {
                String[] parametri = m.group().split(" ");
                switch (parametri[0]) {
                    case "ADD":
                        return dodajKorisnika(parametri);
                    case "AUTHEN":
                        return autentikacijaKorisnika(parametri);
                    case "LOGOUT":
                        return odjavaKorisnika(parametri);
                    case "GRANT":
                        return dodajPodrucje(parametri);
                    case "REVOKE":
                        return oduzmiPodrucje(parametri);
                    case "RIGHTS":
                        return ispisiOvlasti(parametri);
                    case "AUTHOR":
                        return autorizacijaKorisnika(parametri);
                    case "LIST":
                        return vratiPodatkeKorisnika(parametri);
                    case "LISTALL":
                        return vratiSveKorisnike(parametri);
                    default:
                        return "Default";
                }

            }
            return "ERROR 10: Format naredbe nije ispravan";
        }

        protected String pripremiRegex() {
            String regex = "^(ADD\\s\\w+\\s\\w+\\s\"\\w+\"\\s\"\\w+\")";
            regex += "|(AUTHEN\\s\\w+\\s\\w+";
            regex += "|LOGOUT\\s\\w+\\s\\d+";
            regex += "|GRANT\\s\\w+\\s\\d+\\s\\w+\\s\\w+";
            regex += "|REVOKE\\s\\w+\\s\\d+\\s\\w+\\s\\w+";
            regex += "|RIGHTS\\s\\w+\\s\\d+\\s\\w+";
            regex += "|AUTHOR\\s\\w+\\s\\d+\\s\\w+";
            regex += "|LIST\\s\\w+\\s\\d+\\s\\w+";
            regex += "|LISTALL\\s\\w+\\s\\d+";
            regex += ")$";

            return regex;
        }

        private String dodajKorisnika(String[] parametri) {
            String korisnickoIme = parametri[1];
            String lozinka = parametri[2];
            String prezime = parametri[3].replace("\"", "");;
            String ime = parametri[4].replace("\"", "");;
            Korisnik k = new Korisnik(korisnickoIme, lozinka, prezime, ime);
            KorisnikDAO kdao = new KorisnikDAO();
            if (!kdao.postojiKorisnik(k, pbp)) {
                if (kdao.dodajKorisnika(k, pbp)) {
                    return "OK";
                }
                return "ERROR 18: Greska pri dodavanju korisnika";
            }
            return "ERROR 18: Korisnik vec postoji u bazi podataka";
        }

        private String autentikacijaKorisnika(String[] parametri) {
            String korisnickoIme = parametri[1];
            String lozinka = parametri[2];
            KorisnikDAO kdao = new KorisnikDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, lozinka, true, pbp);
            if (k == null) {
                return "ERROR 11: Korisnik ili lozinka ne odgovaraju";
            }
            for (Sjednica s : sjednice) {
                if (s.getKorisnik().equals(korisnickoIme)) {
                    long produzetak = System.currentTimeMillis() + sjednicaTrajanje;
                    long doKadaVrijedi = s.getVrijemeDoKadaVrijedi();
                    s.setVrijemeDoKadaVrijedi(doKadaVrijedi + produzetak);
                    return "OK " + s.getId() + " " + s.getVrijemeDoKadaVrijedi() + " " + s.getBrojPreostalihZahtjeva();
                }
            }
            Sjednica s = new Sjednica(idSjednice++, k.getKorisnickoIme(), sjednicaTrajanje, maksBrojZahtjeva);
            sjednice.add(s);
            return "OK " + s.getId() + " " + s.getVrijemeDoKadaVrijedi() + " " + s.getBrojPreostalihZahtjeva();
        }

        private String odjavaKorisnika(String[] parametri) {
            String korisnickoIme = parametri[1];
            int id = Integer.parseInt(parametri[2]);
            KorisnikDAO kdao = new KorisnikDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, "", false, pbp);
            if (k == null) {
                return "ERROR 17: Trazeni korisnik ne postoji";
            }
            for (Sjednica s : sjednice) {
                if (s.getId() == id && s.getKorisnik().equals(korisnickoIme)) {
                    long doKadaVrijedi = System.currentTimeMillis();
                    s.setVrijemeDoKadaVrijedi(doKadaVrijedi);
                    s.setBrojPreostalihZahtjeva(0);
                    sjednice.remove(s);
                    return "OK";
                }
            }
            return "ERROR 15: Ne postoji vazeca sjednica";
        }

        private String dodajPodrucje(String[] parametri) {
            String korisnickoIme = parametri[1];
            int id = Integer.parseInt(parametri[2]);
            String podrucjeRada = parametri[3];
            String korisnikTrazi = parametri[4];
            KorisnikDAO kdao = new KorisnikDAO();
            OvlastiDAO odao = new OvlastiDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, "", false, pbp);
            if (k == null) {
                return "ERROR 17: Trazeni korisnik ne postoji";
            }
            for (Sjednica s : sjednice) {
                if (s.getId() == id && s.getKorisnik().equals(korisnickoIme)) {
                    if (s.brojPreostalihZahtjeva > 0) {
                        s.brojPreostalihZahtjeva--;
                        int stanje = odao.postojiPodrucje(korisnikTrazi, podrucjeRada, pbp);
                        System.out.println("Stanje: " + stanje);
                        switch (stanje) {
                            case 0:
                                if (odao.dodajPodrucje(korisnikTrazi, podrucjeRada, pbp)) {
                                    return "OK";
                                }
                                return "ERROR 18: Pogreska pri dodavanju podrucja";
                            case 1:
                                return "ERROR 13: Postoji aktivno podrucje";
                            case 2:
                                if (odao.aktivirajPodrucje(korisnikTrazi, podrucjeRada, pbp)) {
                                    return "OK";
                                }
                                return "ERROR 18: Pogreska pri aktiviranju podrucja";
                            default:
                                return "ERROR 18: Pogreska pri provjeri podrucja";
                        }
                    }
                    return "ERROR 16: Broj preostalih zahtjeva je 0";
                }
            }
            return "ERROR 15: Ne postoji vazeca sjednica";
        }

        private String oduzmiPodrucje(String[] parametri) {
            String korisnickoIme = parametri[1];
            int id = Integer.parseInt(parametri[2]);
            String podrucjeRada = parametri[3];
            String korisnikTrazi = parametri[4];
            KorisnikDAO kdao = new KorisnikDAO();
            OvlastiDAO odao = new OvlastiDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, "", false, pbp);
            if (k == null) {
                return "ERROR 17: Trazeni korisnik ne postoji";
            }
            for (Sjednica s : sjednice) {
                if (s.getId() == id && s.getKorisnik().equals(korisnickoIme)) {
                    if (s.brojPreostalihZahtjeva > 0) {
                        s.brojPreostalihZahtjeva--;
                        int stanje = odao.postojiPodrucje(korisnikTrazi, podrucjeRada, pbp);
                        System.out.println("Stanje: " + stanje);
                        switch (stanje) {
                            case 0:
                                return "ERROR 14: Ne postoji aktivno podrucje";
                            case 1:
                                if (odao.deaktivirajPodrucje(korisnikTrazi, podrucjeRada, pbp)) {
                                    return "OK";
                                }
                                return "ERROR 18: Pogreska pri dodavanju podrucja";
                            case 2:
                                return "ERROR 14: Ne postoji aktivno podrucje";
                            default:
                                return "ERROR 18: Pogreska pri provjeri podrucja";
                        }
                    }
                    return "ERROR 16: Broj preostalih zahtjeva je 0";
                }
            }
            return "ERROR 15: Ne postoji vazeca sjednica";
        }

        private String ispisiOvlasti(String[] parametri) {
            String korisnickoIme = parametri[1];
            int id = Integer.parseInt(parametri[2]);
            String korisnikTrazi = parametri[3];
            KorisnikDAO kdao = new KorisnikDAO();
            OvlastiDAO odao = new OvlastiDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, "", false, pbp);
            if (k == null) {
                return "ERROR 17: Trazeni korisnik ne postoji";
            }
            for (Sjednica s : sjednice) {
                if (s.getId() == id && s.getKorisnik().equals(korisnickoIme)) {
                    if (s.brojPreostalihZahtjeva > 0) {
                        s.brojPreostalihZahtjeva--;
                        List<Podrucje> podrucja = odao.dohvatiPodrucja(korisnikTrazi, pbp);
                        if (podrucja.isEmpty()) {
                            return "ERROR 14: Ne postoji aktivno podrucje";
                        }
                        String odgovor = "OK";
                        for (Podrucje p : podrucja) {
                            odgovor += " " + p.getPodrucjeRada();
                        }
                        return odgovor;

                    }
                    return "ERROR 16: Broj preostalih zahtjeva je 0";
                }
            }
            return "ERROR 15: Ne postoji vazeca sjednica";
        }

        private String autorizacijaKorisnika(String[] parametri) {
            String korisnickoIme = parametri[1];
            int id = Integer.parseInt(parametri[2]);
            String podrucjeRada = parametri[3];
            KorisnikDAO kdao = new KorisnikDAO();
            OvlastiDAO odao = new OvlastiDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, "", false, pbp);
            if (k == null) {
                return "ERROR 17: Trazeni korisnik ne postoji";
            }
            for (Sjednica s : sjednice) {
                if (s.getId() == id && s.getKorisnik().equals(korisnickoIme)) {
                    if (s.brojPreostalihZahtjeva > 0) {
                        s.brojPreostalihZahtjeva--;
                        int stanje = odao.postojiPodrucje(korisnickoIme, podrucjeRada, pbp);
                        switch (stanje) {
                            case 0:
                                return "ERROR 14: Ne postoji aktivno podrucje";
                            case 1:
                                return "OK";
                            case 2:
                                return "ERROR 14: Ne postoji aktivno podrucje";
                            default:
                                return "ERROR 18: Pogreska pri provjeri podrucja";
                        }

                    }
                    return "ERROR 16: Broj preostalih zahtjeva je 0";
                }
            }
            return "ERROR 15: Ne postoji vazeca sjednica";
        }

        private String vratiPodatkeKorisnika(String[] parametri) {
            String korisnickoIme = parametri[1];
            int id = Integer.parseInt(parametri[2]);
            String korisnikTrazi = parametri[3];
            KorisnikDAO kdao = new KorisnikDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, "", false, pbp);
            if (k == null) {
                return "ERROR 17: Trazeni korisnik ne postoji";
            }
            for (Sjednica s : sjednice) {
                if (s.getId() == id && s.getKorisnik().equals(korisnickoIme)) {
                    if (s.brojPreostalihZahtjeva > 0) {
                        s.brojPreostalihZahtjeva--;
                        Korisnik korisnik = kdao.dohvatiKorisnika(korisnikTrazi, "", false, pbp);
                        if (korisnik == null) {
                            return "ERROR 17: Trazeni korisnik ne postoji";
                        }
                        String odgovor = "OK \"" + korisnik.korisnickoIme + "\t" + korisnik.prezime + "\t" + korisnik.ime + "\"";
                        return odgovor;
                    }
                    return "ERROR 16: Broj preostalih zahtjeva je 0";
                }
            }
            return "ERROR 15: Ne postoji vazeca sjednica";
        }

        private String vratiSveKorisnike(String[] parametri) {
              String korisnickoIme = parametri[1];
            int id = Integer.parseInt(parametri[2]);
            KorisnikDAO kdao = new KorisnikDAO();
            Korisnik k = kdao.dohvatiKorisnika(korisnickoIme, "", false, pbp);
            if (k == null) {
                return "ERROR 17: Trazeni korisnik ne postoji";
            }
            for (Sjednica s : sjednice) {
                if (s.getId() == id && s.getKorisnik().equals(korisnickoIme)) {
                    if (s.brojPreostalihZahtjeva > 0) {
                        s.brojPreostalihZahtjeva--;
                        List<Korisnik> korisnici = kdao.dohvatiSveKorisnike(pbp);
                        String odgovor = "OK";
                        for(Korisnik kor : korisnici){
                            odgovor += " \"" + kor.korisnickoIme + "\t" + kor.prezime + "\t" + kor.ime + "\"";
                        }
                        return odgovor;
                    }
                    return "ERROR 16: Broj preostalih zahtjeva je 0";
                }
            }
            return "ERROR 15: Ne postoji vazeca sjednica";
        }

    }

}
