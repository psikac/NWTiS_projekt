/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_1.slusaci;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.psikac.aplikacija_1.posluzitelji.ServerKorisnika;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.psikac.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

/**
 *
 * @author NWTiS_1
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    private ServerKorisnika sk;

    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(sk.isServerRadi()==true){
            sk.zaustaviServer();
        }
        ServletContext servletContext = sce.getServletContext();
        servletContext.removeAttribute("Postavke");
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        String konf = servletContext.getInitParameter("konfiguracija");
        String datoteka = servletContext.getRealPath("/WEB-INF") + File.separator + konf;
        
        PostavkeBazaPodataka konfBP = new PostavkeBazaPodataka(datoteka);
        
        try {
            konfBP.ucitajKonfiguraciju();
            servletContext.setAttribute("Postavke", konfBP);
            sk = new ServerKorisnika(konfBP);
            sk.start();
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
