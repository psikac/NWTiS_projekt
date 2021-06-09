package org.foi.nwtis.psikac.aplikacija_2.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configures Jakarta RESTful Web Services for the application.
 *
 * @author Juneau
 */
@ApplicationPath("rest")
public class JakartaRestConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> klase = new HashSet<>();
        klase.add(JakartaEE9Resource.class);
        klase.add(KorisniciResource.class);
        klase.add(AirportsResource.class);
        klase.add(MyAirportsResource.class);

        return klase;
    }

}
