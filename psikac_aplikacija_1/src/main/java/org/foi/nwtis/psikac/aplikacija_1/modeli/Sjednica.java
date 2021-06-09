/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_1.modeli;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author NWTiS_1
 */
public class Sjednica {
    @Getter @Setter
    int id;
    
    @Getter @Setter
    String korisnik;
    
    @Getter @Setter
    long vrijemeKreiranja;
    
    @Getter @Setter
    long vrijemeDoKadaVrijedi;
    
    @Getter @Setter
    public int brojPreostalihZahtjeva;

    public Sjednica(int id, String korisnik, long trajanjeSjednice, int brojPreostalihZahtjeva) {
        this.id = id;
        this.korisnik = korisnik;
        this.vrijemeKreiranja = System.currentTimeMillis();
        this.vrijemeDoKadaVrijedi = this.vrijemeKreiranja + trajanjeSjednice;
        this.brojPreostalihZahtjeva = brojPreostalihZahtjeva;
    }

 

       
}
