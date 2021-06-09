package org.foi.nwtis.psikac.aplikacija_2.rest;

import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import java.util.Map;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.psikac.aplikacija_2.podaci.AerodromDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.KorisnikDAO;
import org.foi.nwtis.psikac.aplikacija_2.podaci.MyAirportsDAO;
import org.foi.nwtis.psikac.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 *
 * @author
 */
@Path("mojiAerodromi")
public class MyAirportsResource {

    @Inject
    ServletContext context;

    /**
     * provjerava podatke korisnika i vraca sve aerodrome
     *
     * @param korisnik
     * @param lozinka
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajSveAerodrome(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        AerodromDAO adao = new AerodromDAO();
        KorisnikDAO kdao = new KorisnikDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                List<Aerodrom> aerodromi = adao.dohvatiSvePraceneAerodrome(pbp);
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
     * provjerava podatke korisnika i vraca sve korisnike koji prate aerodrom
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return
     */
    @Path("{icao}/prate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnike(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        KorisnikDAO kdao = new KorisnikDAO();
        MyAirportsDAO mdao = new MyAirportsDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        List<Korisnik> popisZaVracanje = new ArrayList<>();
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                List<String> korisnici = mdao.dohvatiAerodromeZaIcao(icao, pbp);
                if (!korisnici.isEmpty()) {
                    String odgovor = kdao.dohvatiSveKorisnike(korisnik, lozinka, pbp);
                    String[] parametriOdgovora = odgovor.split(" ");
                    switch (parametriOdgovora[0]) {
                        case "OK":
                            List<Korisnik> popisKorisnika = procitajKorisnike(odgovor);

                            korisnici.forEach(s -> {
                                popisKorisnika.stream().filter(k -> (k.getKorisnik().equals(s))).forEachOrdered(k -> {
                                    popisZaVracanje.add(k);
                                });
                            });
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(popisZaVracanje)
                                    .build();

                        default:
                            return Response.status(Response.Status.NOT_FOUND).
                                    entity(gson.toJson(odgovor)).build();
                    }
                }
                return Response
                        .status(Response.Status.OK)
                        .entity(popisZaVracanje)
                        .build();
            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }
    }

    /**
     * provjerava podatke korisnika i vraca sve aerodrome koje prati korisnik
     *
     * @param korisnik
     * @param lozinka
     * @param pKorisnik
     * @return
     */
    @Path("{korisnik}/prati")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodromeKorisnika(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String pKorisnik) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        KorisnikDAO kdao = new KorisnikDAO();
        AerodromDAO adao = new AerodromDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                String odgovor = kdao.dohvatiKorisnika(korisnik, lozinka, pKorisnik, pbp);

                String[] parametriOdgovora = odgovor.split(" ");
                switch (parametriOdgovora[0]) {
                    case "OK":
                        List<Aerodrom> aerodromi = adao.dohvatiAerodromeKorisnika(pKorisnik, pbp);
                        if (aerodromi != null && !aerodromi.isEmpty()) {
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(aerodromi)
                                    .build();
                        } else if (aerodromi.isEmpty()) {
                            return Response
                                    .status(Response.Status.NOT_FOUND)
                                    .entity(gson.toJson("Korisnik ne prati niti jedan aerodrom"))
                                    .build();
                        }

                    default:
                        return Response.status(Response.Status.NOT_FOUND).
                                entity(gson.toJson(odgovor)).build();
                }

            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }
    }

    /**
     * provjerava podatke korisnika te dodaje zadani aerodrom korisniku za pracenje
     *
     * @param korisnik
     * @param lozinka
     * @param pKorisnik
     * @param icao
     * @return
     */
    @Path("{korisnik}/prati")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajKorisnikuAerodrom(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String pKorisnik,
            Map<String, String> icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        KorisnikDAO kdao = new KorisnikDAO();
        AerodromDAO adao = new AerodromDAO();
        MyAirportsDAO mdao = new MyAirportsDAO();

        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                String odgovor = kdao.dohvatiKorisnika(korisnik, lozinka, pKorisnik, pbp);

                String[] parametriOdgovora = odgovor.split(" ");
                switch (parametriOdgovora[0]) {
                    case "OK":
                        Aerodrom a = adao.dohvatiAerodrom(icao.get("icao"), pbp);
                        if (a == null) {
                            return Response
                                    .status(Response.Status.NOT_FOUND)
                                    .entity(gson.toJson("Ne postoji zadani aerodrom"))
                                    .build();
                        }
                        if (!mdao.provjeriZapis(a, pKorisnik, lozinka, pbp)) {
                            boolean dodanAerodrom = mdao.dodajMojAerodrom(a, pKorisnik, lozinka, pbp);
                            if (dodanAerodrom) {
                                return Response
                                        .status(Response.Status.OK)
                                        .entity(a)
                                        .build();
                            }
                            return Response
                                    .status(Response.Status.NOT_FOUND)
                                    .entity(gson.toJson("Došlo je do pogreške pri obradi zahtjeva."))
                                    .build();
                        }
                        return Response
                                .status(Response.Status.NOT_FOUND)
                                .entity(gson.toJson("Postoji već zapis u bazi podataka."))
                                .build();

                    default:
                        return Response.status(Response.Status.NOT_FOUND).
                                entity(gson.toJson(odgovor)).build();
                }

            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
        }

    }

    /**
     * provjerava podatke korisnika te brise zadani aerodrom iz korisnikove kolekcije
     *
     * @param korisnik
     * @param lozinka
     * @param pKorisnik
     * @param icao
     * @return
     */
    @Path("{korisnik}/prati/{icao}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public Response izbrisiKorisnikovAerodrom(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String pKorisnik,
            @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        Gson gson = new Gson();
        KorisnikDAO kdao = new KorisnikDAO();
        AerodromDAO adao = new AerodromDAO();
        MyAirportsDAO mdao = new MyAirportsDAO();
        String autentikacija = kdao.autenticirajKorisnika(korisnik, lozinka, pbp);
        String[] parametri = autentikacija.split(" ");
        switch (parametri[0]) {
            case "OK":
                String odgovor = kdao.dohvatiKorisnika(korisnik, lozinka, pKorisnik, pbp);

                String[] parametriOdgovora = odgovor.split(" ");
                switch (parametriOdgovora[0]) {
                    case "OK":
                        Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
                        if (a == null) {
                            return Response
                                    .status(Response.Status.NOT_FOUND)
                                    .entity(gson.toJson("Ne postoji zadani aerodrom!"))
                                    .build();
                        }
                        if (mdao.provjeriZapis(a, pKorisnik, lozinka, pbp)) {
                            boolean dodanAerodrom = mdao.izbrisiZadaniAerodrom(a, pKorisnik, lozinka, pbp);
                            if (dodanAerodrom) {
                                return Response
                                        .status(Response.Status.OK)
                                        .entity(a)
                                        .build();
                            }
                            return Response
                                    .status(Response.Status.NOT_FOUND)
                                    .entity(gson.toJson("Došlo je do pogreške pri obradi zahtjeva."))
                                    .build();
                        }
                        return Response
                                .status(Response.Status.NOT_FOUND)
                                .entity(gson.toJson("Ne postoji zapis u bazi podataka."))
                                .build();

                    default:
                        return Response.status(Response.Status.NOT_FOUND).
                                entity(gson.toJson(odgovor)).build();
                }

            default:
                return Response.status(Response.Status.NOT_FOUND).
                        entity(gson.toJson(autentikacija)).build();
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

    private Korisnik pretvoriUKorisnika(String odgovor) {

        String rezultat = odgovor.replace("OK ", "");

        String[] podaci = rezultat.replace("\"", "").split("\t");
        Korisnik kor = new Korisnik(podaci[0], podaci[1], podaci[2], 0);

        return kor;
    }

}
