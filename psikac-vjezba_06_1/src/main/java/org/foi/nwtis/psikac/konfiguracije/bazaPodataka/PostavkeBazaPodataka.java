/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.konfiguracije.bazaPodataka;

import java.util.Properties;
import org.foi.nwtis.psikac.vjezba_03.konfiguracije.Konfiguracija;
import org.foi.nwtis.psikac.vjezba_03.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.psikac.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

/**
 *
 * @author NWTiS_1
 */
public class PostavkeBazaPodataka extends KonfiguracijaApstraktna  implements KonfiguracijaBP {

    public PostavkeBazaPodataka(String nazivDatoteke) {
        super(nazivDatoteke);
    }
    
    @Override
    public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
        Konfiguracija konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
        this.kopirajKonfiguraciju(konf.dajSvePostavke());
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
        Konfiguracija konf = KonfiguracijaApstraktna.dajKonfiguraciju(datoteka);
        konf.kopirajKonfiguraciju(this.dajSvePostavke());
        konf.spremiKonfiguraciju();
    }

    @Override
    public String getAdminDatabase() {
        return dajPostavku("admin.database");
    }

    @Override
    public String getAdminPassword() {
        return dajPostavku("admin.password");
    }

    @Override
    public String getAdminUsername() {
        return dajPostavku("admin.username");
    }

    @Override
    public String getDriverDatabase() {
        return getDriverDatabase(this.getServerDatabase());
    }

    @Override
    public String getDriverDatabase(String urlBazePodataka) {
        String[] vrsta = urlBazePodataka.split(":");
        return dajPostavku("jdbc."+vrsta[1]);
    }

    @Override
    public Properties getDriversDatabase() {
        Properties p = new Properties();
        String protokol = "jdbc.";
        for (Object o : this.dajSvePostavke().keySet()) {
            String k = (String) o;
            if (k.startsWith(protokol)) {
                p.setProperty(k, this.dajPostavku(k));
            }
        }
        return p;
    }

    @Override
    public String getServerDatabase() {
        return dajPostavku("server.database");
    }

    @Override
    public String getUserDatabase() {
        return dajPostavku("user.database");
    }

    @Override
    public String getUserPassword() {
        return dajPostavku("user.password");
    }

    @Override
    public String getUserUsername() {
        return dajPostavku("user.username");
    }
    
}
