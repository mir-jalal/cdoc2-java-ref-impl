package ee.cyber.cdoc2.crypto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import static ee.cyber.cdoc2.KeyUtil.getKeyPairRsaInstance;
import static org.junit.jupiter.api.Assertions.*;


class RsaTest {

    // openssl genrsa -out rsa_priv.pem 2048
    @SuppressWarnings({"checkstyle:OperatorWrap", "squid:S6706"})
    static final String RSA_KEY_PEM =
        """
            -----BEGIN RSA PRIVATE KEY-----
            MIIEogIBAAKCAQEAs18v09QVTnzSTRrFnVhkxDWM2rSHOua2rPz60CVazfOk5Vv9
            Jo4Nq6Uzo3yWS4DZ+3JgO5iRntFeI0NWZGsPGbMWGWKlb4OYlbK0gnBdwsi4LS6L
            nRx7CfYKxOicL5akXqDP2NCoytHFG8QePPE1XAHC7pHyC+hEa7Hggol6sdbEWOK7
            WLCmIjmJ+gJx2O3bg433ad0LX6swvclGNbe0z+YagmYsu4ChfwY9Be6oVRvQzFEH
            OKh+tEibyutdFJ9fioyMjjZtL6lEx5xKUJ18d4efC9apvJ810wRkMDqwR203HEJr
            kRnBg9RtkVPNzOMl8eH6eUdS/oDW5eysnKbtrwIDAQABAoIBAD8N0QRH442Jt2u/
            Y4RiVFnc8TzYhUkhXUoGTCzrVLZdVbQC2ES7Xvbdxf9MhpDYJMiNdmK8yUPpGYyP
            2UjHkbFZEQWvdbRzsCm/flD0KyGT6ZqIaC+8mUvxH+wEURMxg2p4YVg4UX2qq/2M
            vYxyxm0neVzgFRQ2fAbXqrJ4nZbx75KAU8Vepyn1zUHbyjwnaxly+rjiEkTujtrV
            +/FjAwZQ/mnoS0GbMlX6DKfMm6+wJW0ZYZKoqkaCuMPMT+msn2x3DPVgbwm8OO/V
            Qari5g8XwB+b9R5dWnli9dGyW9jFzQ/rhkgni0Z3hVc4lP/6WWbfxwRmAXFjIVyf
            PeavBsECgYEA4N/aivOA3YLUVBlKojUf3deBM6FvPuvELfhSCNVfotIwSsT2krP4
            NKR56PvbScNopdnaXgw1SrmWLZjEQy16cv8DL3a7o70HjSiHn15GZSqt1pBkBKuc
            9fj7JhvWCyNIqcAguchSQS4gf1GrvgIQUvBahIo3vJbdkOpmnfO7QrECgYEAzDMB
            OtgO/0PX5VGr9u7K9Fm7ER/fQym7L+pTigMgFUP15dfO4srbHzYyPyws+jOazDbF
            huGeX8rQqdFJtRgkfVLFVK+taK6UFh4tspzFW1QTX+cIt2XVR9gb4Aq+8mbodb0c
            feT70sOXqeaxrqqgwpdI20B04xALyQTUMQqbjl8CgYAZyhJqNRrmTIbFTlE84RLS
            glCS90Sm1qsdCol98dqR9cEMEiKlGHaysto4WgoAH6T0wFNGzeeetkH+4LJBcgnE
            /nIDE37ZfGhNTAShxlIUcByXqt+NmZDatL8406BsjpNaxGn8ZHjqeLvJXjhwBhSR
            LndzE9bojfTDFd7G5pjnQQKBgFV2f2xGYzh5B5IFtaha1vyf1YhcQ5ATljF+rEoV
            9saPtAnnYcJPzpfoke0YqxZopMAVqGREZ4mGFAEPA/9URGljTA2enUAz2OzM4qlf
            rcYEkTtRMbe4WiSAkWIafUJsyZwFczhJrw/OJtrIH9OPvErVEHwbJRCndZdDex+v
            Zd2XAoGAcA2ntZzUHM7K3A02pCPHtW6X2pmvgcNdQk2trI893mEPdFKPH+FPdaEa
            hxr/6JxIBbP93XG7m0Na8g0SaSl+jJHQOe2Vii7SgCSI47mp2bECUEQXqw0gek+E
            FpMKKtuLmE733CZbg85d9dCMU808+XR+psNvUR6XhxRB+lzgVjc=
            -----END RSA PRIVATE KEY-----
            """;

    // X.509 encoding, Java default for RSAPublicKey
    //    SEQUENCE (2 elem)
    //        SEQUENCE (2 elem)
    //            OBJECT IDENTIFIER 1.2.840.113549.1.1.1 rsaEncryption (PKCS #1)
    //            NULL
    //        BIT STRING (2160 bit) 001100001000001000000001000010100000001010000010000000010000000100000…
    //            SEQUENCE (2 elem)
    //                INTEGER (2048 bit) 226435949622400733452861302723380091312050670462871263385722374431288…
    //                INTEGER 65537
    // openssl rsa -in rsa_priv.pem -outform PEM -pubout -out rsa_pub.pem
    @SuppressWarnings("checkstyle:OperatorWrap")
    static final String PUB_KEY_PEM = """
        -----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs18v09QVTnzSTRrFnVhk
        xDWM2rSHOua2rPz60CVazfOk5Vv9Jo4Nq6Uzo3yWS4DZ+3JgO5iRntFeI0NWZGsP
        GbMWGWKlb4OYlbK0gnBdwsi4LS6LnRx7CfYKxOicL5akXqDP2NCoytHFG8QePPE1
        XAHC7pHyC+hEa7Hggol6sdbEWOK7WLCmIjmJ+gJx2O3bg433ad0LX6swvclGNbe0
        z+YagmYsu4ChfwY9Be6oVRvQzFEHOKh+tEibyutdFJ9fioyMjjZtL6lEx5xKUJ18
        d4efC9apvJ810wRkMDqwR203HEJrkRnBg9RtkVPNzOMl8eH6eUdS/oDW5eysnKbt
        rwIDAQAB
        -----END PUBLIC KEY-----""";

    // openssl rsa -pubin -in rsa_pub.pem -RSAPublicKey_out -out pub.pem
    // header, footer and '\n' removed to get base64 encoded RSAPublicKey structure defined in
    // RFC8017 RSA Public Key Syntax (A.1.1) https://www.rfc-editor.org/rfc/rfc8017#page-54
    //    SEQUENCE (2 elem)
    //        INTEGER (2048 bit) 226435949622400733452861302723380091312050670462871263385722374431288…
    //        INTEGER 65537
    @SuppressWarnings("checkstyle:OperatorWrap")
    static final String PUB_KEY_RSA_PUB_KEY_B64 =
            //-----BEGIN RSA PUBLIC KEY-----
            "MIIBCgKCAQEAs18v09QVTnzSTRrFnVhkxDWM2rSHOua2rPz60CVazfOk5Vv9Jo4N" +
            "q6Uzo3yWS4DZ+3JgO5iRntFeI0NWZGsPGbMWGWKlb4OYlbK0gnBdwsi4LS6LnRx7" +
            "CfYKxOicL5akXqDP2NCoytHFG8QePPE1XAHC7pHyC+hEa7Hggol6sdbEWOK7WLCm" +
            "IjmJ+gJx2O3bg433ad0LX6swvclGNbe0z+YagmYsu4ChfwY9Be6oVRvQzFEHOKh+" +
            "tEibyutdFJ9fioyMjjZtL6lEx5xKUJ18d4efC9apvJ810wRkMDqwR203HEJrkRnB" +
            "g9RtkVPNzOMl8eH6eUdS/oDW5eysnKbtrwIDAQAB";
            //-----END RSA PUBLIC KEY-----

    @Test
    void testRsaOep() throws Exception {

        KeyPairGenerator generator = getKeyPairRsaInstance();
        generator.initialize(2048, SecureRandom.getInstanceStrong());

        KeyPair keyPair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String plainSecret = "_secret_"; // 8 bytes as FMK_LEN

        checkRsaEncryption(plainSecret, publicKey, privateKey);
    }

    static void checkRsaEncryption(String plainSecret, RSAPublicKey publicKey, RSAPrivateKey privateKey)
            throws Exception {
        byte[] data = plainSecret.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = RsaUtils.rsaEncrypt(data, publicKey);

        byte[] decryptedBytes = RsaUtils.rsaDecrypt(encrypted, privateKey);
        String decrypted =  new String(decryptedBytes, StandardCharsets.UTF_8);

        assertEquals(plainSecret, decrypted);
    }

    @Test
    void testLoadRsaKeys() throws Exception {
        PublicKey publicKey = PemTools.loadPublicKey(PUB_KEY_PEM);
        KeyPair keyPair = PemTools.loadKeyPair(RSA_KEY_PEM);

        assertTrue(KeyAlgorithm.isRsaKeysAlgorithm(keyPair.getPublic().getAlgorithm()));
        assertEquals(publicKey, keyPair.getPublic());

        checkRsaEncryption("secret", (RSAPublicKey) publicKey, (RSAPrivateKey) keyPair.getPrivate());
    }

    @Test
    void testRsaPubKeyEncode() throws IOException {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) PemTools.loadPublicKey(PUB_KEY_PEM);
        byte[] encoded = RsaUtils.encodeRsaPubKey(rsaPublicKey);

        assertEquals(PUB_KEY_RSA_PUB_KEY_B64, Base64.getEncoder().encodeToString(encoded));
    }

    @Test
    void testRsaPubKeyDecode() throws IOException, GeneralSecurityException {

        byte[] rsaPubDer = Base64.getDecoder().decode(PUB_KEY_RSA_PUB_KEY_B64);

        RSAPublicKey rsaPublicKey = RsaUtils.decodeRsaPubKey(rsaPubDer);
        RSAPublicKey expected = (RSAPublicKey) PemTools.loadPublicKey(PUB_KEY_PEM);

        assertEquals(expected, rsaPublicKey);
    }

    @Test
    void testRsaEncodingDigiDocInteroperability() throws GeneralSecurityException, IOException {
        // RSA pub key encoded by DigiDoc client, interoperability test
        @SuppressWarnings("checkstyle:LineLength")
        String m = "MIIBCgKCAQEAxKTvy+zUftuk3gK5SbUW6RUG3VQYTgqFQrjcAuLyquhSIun06xu9nz4N3Vfqg9h4BUYQKmcGNwoYC7ka1bjnQcblUy7FSznlwQssddE7BL4r3av52atU90Dvr3K9eaJmlfTpbpEa1JpUkDpnDCKf2vM6wPxhxNQYDDoA6QnKFjxOlfzJno/pHFnHKqdDBqmoJlU2o2XcTHzm5vtkg/Z4jjb7tHkrLV75tl+puXK1Kr598cl8pvsLRQHm0L+zKMs+btLZv2LlLNkXEl6dIm73a60bSf65Ya5fLeC+/znde3QibmlmN3yJenP/5bZsqmxExQmX9mFLrAr1g/8jw6O2xwIDAQAB";
        //should not throw exceptions
        RSAPublicKey rsaPublicKey = RsaUtils.decodeRsaPubKey(Base64.getDecoder().decode(m));
        assertNotNull(rsaPublicKey);
    }

}

