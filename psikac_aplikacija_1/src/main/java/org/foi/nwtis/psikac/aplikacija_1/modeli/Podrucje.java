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
public class Podrucje {

    @Getter
    @Setter
    private String korisnickoIme;

    @Getter
    @Setter
    private String podrucjeRada;

    @Getter
    @Setter
    private boolean status;

    public Podrucje(String korisnickoIme, String podrucjeRada, boolean status) {
        this.korisnickoIme = korisnickoIme;
        this.podrucjeRada = podrucjeRada;
        this.status = status;
    }

}
