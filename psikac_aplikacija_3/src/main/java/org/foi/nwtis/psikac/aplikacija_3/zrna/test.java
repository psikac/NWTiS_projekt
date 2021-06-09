/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_3.zrna;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author NWTiS_1
 */
@Named(value = "test")
@SessionScoped
public class test implements Serializable {

    /**
     * Creates a new instance of test
     */
    public test() {
    }
    
    private void klik(){
        System.out.println("Click");
    }
    
}
