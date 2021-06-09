
package org.foi.nwtis.psikac.vjezba_03.konfiguracije;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KonfiguracijaBIN extends KonfiguracijaApstraktna{

    public KonfiguracijaBIN(String nazivDatoteke) {
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
            Object o = null;
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                o = ois.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                 throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke: '" + nazivDatoteke + "'!");
            }

            if (o instanceof Properties) {
                 this.postavke = (Properties) o;
            } else {
                throw new NeispravnaKonfiguracija("Problem datoteka: '" + nazivDatoteke + "' ne sadrži objekt tipa Properties!");
            }
        } else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: '" + nazivDatoteke + "' ne postoji!");
        }
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
        
        if(datoteka == null || datoteka.length()==0){
            throw new NeispravnaKonfiguracija("Nije definiran naziv datoteke!");
        }
        
        File f = new File(datoteka);
        if(!f.exists() || (f.exists() && f.isFile())){
            try ( ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
               oos.writeObject(this.postavke);
            } catch (IOException ex) {
                throw new NeispravnaKonfiguracija("Problem kod spremanja datoteke: " + datoteka + "!");
            }
        }
        else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: " + datoteka + " ne postoji!");
        }
    }
    
}
