package ours.shifumissage;

import org.junit.Test;

import static org.junit.Assert.*;

public class CrypthorTests {
    Crypthor crypteur = new Crypthor();
    String originalMessage = "azm";
    String expectedEnc = "ban";


    @Test
    public void encryptTest() {
        String res = crypteur.cryptCesar(originalMessage,1);
        assertEquals(expectedEnc, res);
    }

    @Test
    public void decrytTest(){
        String res = crypteur.decryptCesar(expectedEnc, 1);
        assertEquals(originalMessage, res);
    }

    @Test
    public void thorTest(){
        int key = crypteur.getKey();
        String res = crypteur.cryptCesar(originalMessage, key);
        res =crypteur.decryptCesar(res,key);
        assertEquals(originalMessage, res);
    }


}