/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_2.modeli;

import java.io.Serializable;

/**
 *
 * @author NWTiS_1
 */

public class RequestBody implements Serializable{

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }

    public RequestBody() {
    }

    public String getNaziv() {
        return naziv;
    }

    public String getDrzava() {
        return drzava;
    }
    private String naziv;
    private String drzava;

    public RequestBody(String naziv, String drzava) {
        this.naziv = naziv;
        this.drzava = drzava;
    }
    
}
