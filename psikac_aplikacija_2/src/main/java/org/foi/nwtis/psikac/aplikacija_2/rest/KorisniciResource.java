package org.foi.nwtis.psikac.aplikacija_2.rest;

import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Korisnik;

import org.foi.nwtis.psikac.aplikacija_2.podaci.KorisnikDAO;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author
 */
@Path("korisnici")
public class KorisniciResource {

    @Inject
    ServletContext context;

    /*@GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        return Response
                .ok("ping Jakarta EE")
                .build();
    }*/
    /**
     * provjerava podatke korisnika i vraca sve korisnike u bazi podataka
     *
     * @param korisnik
     * @param lozinka
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnike(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        KorisnikDAO kdao = new KorisnikDAO();
        String odgovor = kdao.dohvatiSveKorisnike(korisnik, lozinka, pbp);
        String[] parametri = odgovor.split(" ");

        System.out.println("odgovor: " + odgovor);
        switch (parametri[0]) {
            case "OK":
                return Response
                        .status(Response.Status.OK)
                        .entity(procitajKorisnike(odgovor))
                        .build();
            case "ERROR":
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson(odgovor))
                        .build();
            default:
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("ERROR")
                        .build();

        }

    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajKorisnika(Korisnik noviKorisnik) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        KorisnikDAO kdao = new KorisnikDAO();
        String odgovor = kdao.dodajKorisnika(noviKorisnik, pbp);
        String[] parametri = odgovor.split(" ");

        System.out.println("odgovor: " + odgovor);
        switch (parametri[0]) {
            case "OK":
                return Response
                        .status(Response.Status.OK)
                        .entity(odgovor)
                        .build();
            case "ERROR":
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson(odgovor))
                        .build();
            default:
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Pogreska u radu web servera")
                        .build();

        }
    }

    @Path("{korisnik}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnika(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String pKorisnik) {
        Gson gson = new Gson();
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        KorisnikDAO kdao = new KorisnikDAO();
        String odgovor = kdao.dohvatiKorisnika(korisnik, lozinka, pKorisnik, pbp);
        String[] parametri = odgovor.split(" ");

        System.out.println("odgovor: " + odgovor);
        switch (parametri[0]) {
            case "OK":
                return Response
                        .status(Response.Status.OK)
                        .entity(odgovor)
                        .build();
            case "ERROR":
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson(odgovor))
                        .build();
            default:
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Pogreska u radu web servera")
                        .build();

        }
    }

    private List<Korisnik> procitajKorisnike(String odgovor) {
        List<Korisnik> listaKorisnika = new ArrayList<>();
        String rezultat = odgovor.replace("OK ", "");
        String[] zapisiKorisnika = rezultat.split(" ");
        for (String s : zapisiKorisnika) {
            String[] podaci = s.replace("\"", "").split("\t");
            Korisnik noviKorisnik = new Korisnik(podaci[0], podaci[1], podaci[2], 0);
            listaKorisnika.add(noviKorisnik);
        }

        return listaKorisnika;
    }
}
