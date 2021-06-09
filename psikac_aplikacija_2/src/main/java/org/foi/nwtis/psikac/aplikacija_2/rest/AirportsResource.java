package org.foi.nwtis.psikac.aplikacija_2.rest;

import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.psikac.aplikacija_2.modeli.RequestBody;
import org.foi.nwtis.psikac.aplikacija_2.podaci.AerodromDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.AirplaneDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.KorisnikDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.MeteoDAO;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

//curl -X OPTIONS http//:localhost:8084/psikac_zadaca_2_1/rest/aerodromi > aerodromi.wadl
/**
 *
 * @author
 */
@Path("aerodromi")
public class AirportsResource {

    @Inject
    ServletContext context;

    /**
     * provjerava podatke korisnika te vraca sve aerodrome za zadane parametre drzave i naziva
     * aerodroma
     *
     * @param korisnik
     * @param lozinka
     * @param naziv
     * @param drzava
     * @return
     */
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrome(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            RequestBody rq) {
        String naziv = rq.getNaziv();
        String drzava = rq.getDrzava();
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();

        System.out.println("Vracam sve aerodrome");
        AerodromDAO adao = new AerodromDAO();
        KorisnikDAO kdao = new KorisnikDAO();
        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                List<Aerodrom> aerodromi = adao.dohvatiSveAerodrome(naziv, drzava, pbp);
                return Response
                        .status(Response.Status.OK)
                        .entity(aerodromi)
                        .build();
            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }

    }

    /**
     * provjerava podatke korisnika vraca specifican aerodrom za zadani icao
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return
     */
    @Path("{icao}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrom(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        AerodromDAO adao = new AerodromDAO();
        KorisnikDAO kdao = new KorisnikDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                Aerodrom aerodrom = adao.dohvatiAerodrom(icao, pbp);
                if (aerodrom != null) {
                    return Response
                            .status(Response.Status.OK)
                            .entity(aerodrom)
                            .build();
                }
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson("Trazeni aerodrom ne postoji."))
                        .build();
            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }

    }

    /**
     * provjerava podatke korisnika i vraca broj letova za zadani aerodrom
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return
     */
    @Path("{icao}/letovi")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajBrojLetova(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        AerodromDAO adao = new AerodromDAO();
        KorisnikDAO kdao = new KorisnikDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                int brojLetova = adao.dohvatiBrojLetova(icao, pbp);
                if (brojLetova != -1) {
                    return Response
                            .status(Response.Status.OK)
                            .entity(brojLetova)
                            .build();
                }
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson("Doslo je do pogreske kod obrade zahtjeva."))
                        .build();
            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }

    }

    /**
     * provjerava podatke korisnika i vraca broj letova za zadani aerodrom
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return
     */
    @Path("{icao}/letovi/{dan}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajLetoveNaDan(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao, @PathParam("dan") String dan) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        AirplaneDAO adao = new AirplaneDAO();
        KorisnikDAO kdao = new KorisnikDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                List<AvionLeti> letovi = adao.dajLetove(icao, dan, pbp);
                return Response
                        .status(Response.Status.OK)
                        .entity(letovi)
                        .build();
            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }

    }
    
     /**
     * provjerava podatke korisnika i vraca broj letova za zadani aerodrom
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return
     */
    @Path("{icao}/meteoDan/{dan}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajMeteoPodatke(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao, @PathParam("dan") String dan) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        MeteoDAO mdao = new MeteoDAO();
        KorisnikDAO kdao = new KorisnikDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                List<MeteoOriginal> podaci = mdao.dajMeteoPodatke(icao, dan, pbp);
                return Response
                        .status(Response.Status.OK)
                        .entity(podaci)
                        .build();
            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }
    }
    
     @Path("{icao}/meteoVrijeme/{vrijeme}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajMeteoPodatakZaVrijeme(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao, @PathParam("vrijeme") String vrijeme) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        MeteoDAO mdao = new MeteoDAO();
        KorisnikDAO kdao = new KorisnikDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                MeteoOriginal podaci = mdao.dajMeteoPodatakeZaVrijeme(icao, vrijeme, pbp);
                
                       return Response
                        .status(Response.Status.OK)
                        .entity(podaci)
                        .build();
                
             
            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }
    }
}
