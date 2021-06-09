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
public class Korisnik {

    @Getter
    @Setter
    public String korisnickoIme;

    @Getter
    @Setter
    public String lozinka;

    @Getter
    @Setter
    public String prezime;

    @Getter
    @Setter
    public String ime;

    public Korisnik(String korisnickoIme, String lozinka, String prezime, String ime) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.prezime = prezime;
        this.ime = ime;
    }
    
    

}
