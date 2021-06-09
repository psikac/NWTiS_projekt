/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.aplikacija_2.klijenti;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author NWTiS_1
 */
public class Klijent {
    
    public String izvrsiKomandu(String komanda, String adresa, int port) {
        String razlog = null;
        try {
            InetSocketAddress isa = new InetSocketAddress(adresa, port);
            StringBuilder sb;
            try (Socket uticnica = new Socket()) {
                uticnica.connect(isa, 70);
                InputStream is = uticnica.getInputStream();
                OutputStream os = uticnica.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write(komanda);
                osw.flush();
                uticnica.shutdownOutput();
                sb = new StringBuilder();
                while (true) {
                    int i = is.read();
                    if (i == -1) {
                        break;
                    }
                    sb.append((char) i);
                }
                uticnica.shutdownInput();
            }

            return sb.toString();
        } catch (UnknownHostException ex) {
            razlog = ex.getMessage();
           // if(razlog == "jdbc:mysql://localhost/nwtis_psikac_bp_2")
              // return sb.toString();
        } catch (IOException ex) {
            razlog = "Doslo je do pogreske pri spajanju. Provjerite unesene parametre adrese";
        }
        return "ERROR: Naredba nije izvrsena \nRazlog: " + razlog;
    }
}
