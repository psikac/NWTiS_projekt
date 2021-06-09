package org.foi.nwtis.psikac.vjezba_03.konfiguracije;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KonfiguracijaJSON extends KonfiguracijaApstraktna {

    public KonfiguracijaJSON(String nazivDatoteke) {
        super(nazivDatoteke);
    }

    @Override
    public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
        this.obrisiSvePostavke();
        if (nazivDatoteke == null || nazivDatoteke.length() == 0) {
            throw new NeispravnaKonfiguracija("Nije definiran naziv datoteke!");
        }

        File f = new File(nazivDatoteke);
        if (f.exists() && f.isFile()) {
           try {
                
                Gson gson = new Gson();
                
                this.postavke = gson.fromJson(new FileReader(nazivDatoteke), Properties.class);
                
            } catch (IOException ex) {
                throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke: " + nazivDatoteke + "!");
            }
        } else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: " + nazivDatoteke + " ne postoji!");
        }
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NeispravnaKonfiguracija("Nije definiran naziv datoteke!");
        }

        File f = new File(datoteka);
        if (!f.exists() || (f.exists() && f.isFile())) {

            Writer writer = null;
            try {
                writer = new FileWriter(datoteka,false);
                Gson gson = new Gson();
                Map<String, Object> map = new HashMap<>();
                postavke.forEach((k, v) -> map.put(k.toString(),v));
                gson.toJson(map, writer);
            } catch (IOException ex) {
                Logger.getLogger(KonfiguracijaJSON.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(KonfiguracijaJSON.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


            
        } else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: " + datoteka + " ne postoji!");
        }
    }

}
