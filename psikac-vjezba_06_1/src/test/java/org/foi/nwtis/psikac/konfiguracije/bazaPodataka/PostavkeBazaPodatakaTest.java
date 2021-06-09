/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psikac.konfiguracije.bazaPodataka;

import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author NWTiS_1
 */
public class PostavkeBazaPodatakaTest {
    
    public PostavkeBazaPodatakaTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of ucitajKonfiguraciju method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testUcitajKonfiguraciju() throws Exception {
        System.out.println("ucitajKonfiguraciju");
        String nazivDatoteke = "";
        PostavkeBazaPodataka instance = null;
        instance.ucitajKonfiguraciju(nazivDatoteke);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of spremiKonfiguraciju method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testSpremiKonfiguraciju() throws Exception {
        System.out.println("spremiKonfiguraciju");
        String datoteka = "";
        PostavkeBazaPodataka instance = null;
        instance.spremiKonfiguraciju(datoteka);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAdminDatabase method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetAdminDatabase() {
        System.out.println("getAdminDatabase");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getAdminDatabase();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAdminPassword method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetAdminPassword() {
        System.out.println("getAdminPassword");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getAdminPassword();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAdminUsername method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetAdminUsername() {
        System.out.println("getAdminUsername");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getAdminUsername();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDriverDatabase method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetDriverDatabase_0args() {
        System.out.println("getDriverDatabase");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getDriverDatabase();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDriverDatabase method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetDriverDatabase_String() {
        System.out.println("getDriverDatabase");
        String urlBazePodataka = "";
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getDriverDatabase(urlBazePodataka);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDriversDatabase method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetDriversDatabase() {
        System.out.println("getDriversDatabase");
        PostavkeBazaPodataka instance = null;
        Properties expResult = null;
        Properties result = instance.getDriversDatabase();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getServerDatabase method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetServerDatabase() {
        System.out.println("getServerDatabase");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getServerDatabase();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserDatabase method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetUserDatabase() {
        System.out.println("getUserDatabase");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getUserDatabase();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserPassword method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetUserPassword() {
        System.out.println("getUserPassword");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getUserPassword();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserUsername method, of class PostavkeBazaPodataka.
     */
    @Test
    @Disabled
    public void testGetUserUsername() {
        System.out.println("getUserUsername");
        PostavkeBazaPodataka instance = null;
        String expResult = "";
        String result = instance.getUserUsername();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
