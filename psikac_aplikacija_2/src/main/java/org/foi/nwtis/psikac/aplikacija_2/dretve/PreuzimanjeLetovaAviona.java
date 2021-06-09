/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_2.dretve;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.foi.nwtis.podaci.MyAirport;
import org.foi.nwtis.psikac.aplikacija_2.podaci.AerodromDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.AirplaneDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.MyAirportsDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.MyAirportsLogDAO;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author NWTiS_1
 */
public class PreuzimanjeLetovaAviona extends Thread {

    private PostavkeBazaPodataka pbp;
    private OSKlijent osk;
    private Date datumPreuzimanja;
    private Date datumDo;
    private Date datumKraja;
    private int trajanjeCiklusa;
    private boolean status;
    private long trajanjePauze;
    private boolean kraj = false;

    public PreuzimanjeLetovaAviona(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }

    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        System.out.println("[PreuzimanjeLetovaAviona] Pocinje preuzimanje podataka");
        while (!kraj && datumPreuzimanja.before(datumKraja)) {
            System.out.println("[PreuzimanjeLetovaAviona] Preuzimamo podatke");
            try {
                long pocetak = System.currentTimeMillis();
                preuzmiPodatkeOLetovima();
                long kraj = System.currentTimeMillis();
                long razlika = kraj - pocetak;
                datumPreuzimanja = inkrementirajDatum(datumPreuzimanja);
                datumDo = inkrementirajDatum(datumDo);
                System.out.println("[PreuzimanjeLetovaAviona] Pauziranje rada dretve");
                long spavanje = trajanjeCiklusa * 1000 - razlika;
                if(spavanje<0){
                    System.out.println("[PreuzimanjeLetovaAviona] Vrijednost spavanja je negativna");
                }
                else{
                    Thread.sleep(spavanje);
                }
                
            } catch (InterruptedException ex) {
                System.out.println("[PreuzimanjeLetovaAviona] Prekid rada");
            }

        }
        System.out.println("[PreuzimanjeLetovaAviona] Preuzimanje podataka je zavrseno");
        super.run(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Za zadani se datum preuzimaju svi letovi pojedinog aerodroma
     * @throws InterruptedException 
     */
    private void preuzmiPodatkeOLetovima() throws InterruptedException {
        AirplaneDAO adao = new AirplaneDAO();
        MyAirportsDAO maDao = new MyAirportsDAO();
        MyAirportsLogDAO mylogDao = new MyAirportsLogDAO();
        HashMap<String, MyAirport> lista = maDao.dohvatiSveMojeAerodrome(pbp);
        int i = 0;
        for (Map.Entry<String, MyAirport> entry : lista.entrySet()) {
            MyAirport a = entry.getValue();
            if (!mylogDao.provjeriPostojanjeZapisa(a, datumPreuzimanja, pbp)) {
                System.out.println("[PreuzimanjeLetovaAviona] Preuzimam podatke o aerodromu: [" + a.getIdent() + " | " + datumPreuzimanja + "]" );

                Timestamp odKada = new Timestamp(datumPreuzimanja.getTime());
                Timestamp doKada = new Timestamp(datumDo.getTime());

                List<AvionLeti> avioni = osk.getDepartures(a.getIdent(), odKada, doKada);

                for (AvionLeti al : avioni) {
                    if (al.getEstArrivalAirport() != null) {
                        adao.dodajLet(al, pbp);
                    }
                }
                mylogDao.dodajZapisOAerodromu(a, datumPreuzimanja, pbp);
                Thread.sleep(trajanjePauze);
            }

        }
    }

    @Override
    public synchronized void start() {
        boolean status = Boolean.parseBoolean(pbp.dajPostavku(("preuzimanje.letova.status")));
        if (!status) {
            System.out.println("[PreuzimanjeLetovaAviona] Ništa se ne preuzima!");
            return;
        }
        try {
            this.datumPreuzimanja = vratiDatum(pbp.dajPostavku("preuzimanje.letova.pocetak"));
            this.datumDo = inkrementirajDatum(datumPreuzimanja);
            this.datumKraja = vratiDatum(pbp.dajPostavku("preuzimanje.kraj"));
        } catch (ParseException ex) {
            System.out.println("[PreuzimanjeLetovaAviona] Problem pri pretvaranju datuma");
        }

        this.status = Boolean.parseBoolean(pbp.dajPostavku("preuzimanje.letova.status"));
        this.trajanjePauze = Integer.parseInt(pbp.dajPostavku("preuzimanje.letova.pauza"));
        this.trajanjeCiklusa = Integer.parseInt(pbp.dajPostavku("preuzimanje.letova.ciklus"));
        String korisnik = pbp.dajPostavku("OpenSkyNetwork.korisnik");
        String lozinka = pbp.dajPostavku("OpenSkyNetwork.lozinka");
        this.osk = new OSKlijent(korisnik, lozinka);
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 
     * @param datum string vrijednost datuma koju zelimo pretvoriti
     * @return datum odredenog formata
     * @throws ParseException 
     */
    private static Date vratiDatum(String datum) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.parse(datum);
    }

    /**
     * 
     * @param datumPreuzimanja 
     * @return 
     */
    private static Date inkrementirajDatum(Date datumPreuzimanja) {
        Calendar c = Calendar.getInstance();
        c.setTime(datumPreuzimanja);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

}
