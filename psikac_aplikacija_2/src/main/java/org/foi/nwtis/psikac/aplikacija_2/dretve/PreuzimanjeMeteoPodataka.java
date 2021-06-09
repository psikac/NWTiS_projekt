/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_2.dretve;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.MyAirport;
import org.foi.nwtis.psikac.aplikacija_2.podaci.AerodromDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.AirplaneDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.MeteoDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.MyAirportsDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.MyAirportsLogDAO;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.Meteo;
import org.foi.nwtis.rest.podaci.MeteoOriginal;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 *
 * @author NWTiS_1
 */
public class PreuzimanjeMeteoPodataka extends Thread {

    private PostavkeBazaPodataka pbp;
    private OWMKlijent owm;
    private int trajanjeCiklusa;
    private boolean status;
    private long trajanjePauze;
    private boolean kraj = false;

    public PreuzimanjeMeteoPodataka(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }

    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        System.out.println("[PreuzimanjeMeteoPodataka] Pocinje preuzimanje podataka");
        while (!kraj) {
            System.out.println("[PreuzimanjeMeteoPodataka] Preuzimamo podatke");
            try {
                long pocetak = System.currentTimeMillis();
                preuzmiMeteoPodatke();
                long kraj = System.currentTimeMillis();
                long razlika = kraj - pocetak;
                System.out.println("[PreuzimanjeMeteoPodataka] Pauziranje rada dretve");
                long spavanje = trajanjeCiklusa * 1000 - razlika;
                if (spavanje < 0) {
                    System.out.println("[PreuzimanjeMeteoPodataka] Vrijednost spavanja je negativna");
                } else {
                    Thread.sleep(spavanje);
                }

            } catch (InterruptedException ex) {
                System.out.println("[PreuzimanjeMeteoPodataka] Prekid rada");
            }

        }
        System.out.println("[PreuzimanjeMeteoPodataka] Preuzimanje podataka je zavrseno");
        super.run(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Za zadani se datum preuzimaju svi letovi pojedinog aerodroma
     *
     * @throws InterruptedException
     */
    @Override
    public synchronized void start() {
        boolean status = Boolean.parseBoolean(pbp.dajPostavku(("preuzimanje.meteo.status")));
        if (!status) {
            System.out.println("[PreuzimanjeMeteoPodataka] Ništa se ne preuzima!");
            return;
        }
        this.status = Boolean.parseBoolean(pbp.dajPostavku("preuzimanje.meteo.status"));
        this.trajanjePauze = Integer.parseInt(pbp.dajPostavku("preuzimanje.meteo.pauza"));
        this.trajanjeCiklusa = Integer.parseInt(pbp.dajPostavku("preuzimanje.meteo.ciklus"));
        String apiKey = pbp.dajPostavku("OpenWeatherMap.apikey");
        this.owm = new OWMKlijent(apiKey);
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    

    private void preuzmiMeteoPodatke() throws InterruptedException {
        MyAirportsDAO maDao = new MyAirportsDAO();
        AerodromDAO aeroDao = new AerodromDAO();
        MeteoDAO meteoDAO = new MeteoDAO();
        HashMap<String, MyAirport> lista = maDao.dohvatiSveMojeAerodrome(pbp);
        int i = 0;
        for (Map.Entry<String, MyAirport> entry : lista.entrySet()) {
            MyAirport a = entry.getValue();
            System.out.println("[PreuzimanjeMeteoPodataka] Preuzimam podatke o aerodromu: [" + a.getIdent() +"]");
            Aerodrom aerodrom = aeroDao.dohvatiAerodrom(a.getIdent(), pbp);
            Lokacija lok = aerodrom.getLokacija();

            MeteoOriginal podaci = owm.getRealTimeWeatherOriginal(lok.getLatitude(), lok.getLongitude());

         // System.out.println("json" + podaci.getJsonMeteo());
            meteoDAO.dodajMeteoPodatak(podaci, a.getIdent(), pbp);
            Thread.sleep(trajanjePauze);

        }
    }

}
