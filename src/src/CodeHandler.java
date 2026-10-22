
import javacard.security.GenericSecretKey;
import javacard.security.KeyBuilder;
import javacard.security.Key;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.lang.Math;
import java.util.Arrays;
import java.math.BigInteger;


//HEY LOOK AT THIS
import static java.lang.IO.println;

public class CodeHandler {
    private javax.crypto.Cipher cipher;
    private SecureRandom keygen;
    private KeyFactory keymaker;

    private ECPrivateKey privkey;
    private ECPrivateKey Sharedkey;
    public SecretKey otherKey;
    public ECPublicKey publickey;

    //This block makes the Parameters of the curve we're using
    private BigInteger primestart = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951");
    private ECFieldFp field = new ECFieldFp(primestart);

    //  All these massive numbers come from
    //  https://www.secg.org/sec2-v2.pdf <- Section 2.4.2
    //  Just translated from hex to decimal to fit the constructors

    private BigInteger acoeff = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948");
    private BigInteger bcoeff = new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291");

    private byte[] seed = {(byte) 0xc4,(byte) 0x9d, (byte) 0x36, (byte) 0x08, (byte) 0x86, (byte) 0xe7, (byte) 0x04, (byte) 0x93,
            (byte) 0x6a, (byte) 0x66, (byte) 0x78, (byte) 0xe1, (byte) 0x13, (byte) 0x9d, (byte) 0x26, (byte) 0xb7,
            (byte) 0x81, (byte) 0x9f, (byte) 0x7e, (byte) 0x90};

    //Now to define the actual curve
    private EllipticCurve ecUsed = new EllipticCurve(field, acoeff, bcoeff, seed);

    //Setting base point G
    private BigInteger g_x = new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286");
    private BigInteger g_y = new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109");
    private ECPoint gpoint = new ECPoint(g_x, g_y);

    //Setting Order of function
    private BigInteger n = new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369");
    ECParameterSpec keyspecs = new ECParameterSpec(ecUsed, gpoint, n, 1);

    {
        try {
            cipher = Cipher.getInstance("AES_256/GCM/NoPadding", "SunJCE");

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        try {
            keymaker = KeyFactory.getInstance("EC");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {

            keygen = SecureRandom.getInstance("Windows-PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyPairGenerator pairgen;
    {
        try {
            pairgen = KeyPairGenerator.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private ECGenParameterSpec params = new ECGenParameterSpec("secp256r1");




    public CodeHandler() {
        try {
            pairgen.initialize(params, keygen);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        KeyPair keys = pairgen.genKeyPair();
        if (keys.getPrivate() instanceof ECPrivateKey) {
            privkey = (ECPrivateKey) keys.getPrivate();
        }
        if (keys.getPublic() instanceof ECPublicKey) {
            publickey = (ECPublicKey) keys.getPublic();
        }
    }

    public void setOther_calcShared(String other_x, String other_y) {
        byte[] x_bytes = other_x.getBytes();
        println("X Byte length: " + x_bytes.length);
        byte[] y_bytes = other_y.getBytes();
        BigInteger new_x = new BigInteger(x_bytes);

        /*Okay so with EC diffie-hellman, especially using the same curve and base point
        (as we are doing since we're using a standard curve specification)
        The public keys are just the point times the integer that is the private key
        SO! when you multiply their public key by your own private key, you wind up
        with a shared key due to the commutative property and yadda yadda.

        Basically, the shared key is their pub * your private, both sides do it.*/
        BigInteger privx = g_x.multiply(privkey.getS());
        new_x = new_x.add(privx);
        println(new_x.toByteArray().length);

        ECPrivateKeySpec newspecs = new ECPrivateKeySpec(new_x, keyspecs);
        try {
            PrivateKey catcher = keymaker.generatePrivate(newspecs);
            if (catcher instanceof ECPrivateKey) {
                this.Sharedkey = (ECPrivateKey) catcher;
            }
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

    }

    public String[] getPublicCoords() {
        /*THIS IS JUST THE PUBLIC KEY BUT
            *******LISTEN TO ME*******
        YOU NEED TO SEND THESE TO THE OTHER MACHINE
        SO THEY CAN CALCULATE THE SHARED KEY CORRECTLY. */

        ECPoint pubpoint = this.publickey.getW();
        BigInteger pubx = pubpoint.getAffineX();
        BigInteger puby = pubpoint.getAffineY();
        String[] strings = {pubx.toString(), puby.toString()};
        return strings;
    }


    /*GAME PLAN: This gets called EVERY time a message is to be sent.
    It is also only for the Shared messages, not any of the setup/requests */
    public String encoder(String message){
        byte[] iv = new byte[12];
        keygen.nextBytes(iv);
        GCMParameterSpec gcparams = new GCMParameterSpec(128, iv);

        try {
            cipher.init(1, Sharedkey, gcparams, keygen);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        byte[] input = message.getBytes(StandardCharsets.UTF_16);
        //This should be how long the encrypted message is
        //(source:https://www.trustedcalcs.com/en-us/guides/dev-tools/aes-gcm-encryption)
        byte[] output = new byte[(int) Math.ceil(input.length * 1.33) + 28];

        try {
            int ciphertext = cipher.doFinal(input, 0, input.length, output, 0);
        } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        String fin = new String(output, StandardCharsets.UTF_16);
        return fin;
    }

    /*GAME PLAN 2: Call this on every receipt once the shared key is established. */
    public String decoder(String ciphertext){
        byte[] text = ciphertext.getBytes(StandardCharsets.UTF_16);
        byte[] iv = Arrays.copyOfRange(text, 0, 11);
        byte[] output = new byte[text.length];
        GCMParameterSpec gcparams= new GCMParameterSpec(128, iv);
        //I'm just simplifying the length there, it's inefficient but I don't wanna deal.

        try {
            cipher.init(2, Sharedkey, gcparams, keygen);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        try {
            int plaintext = cipher.doFinal(text, 0, text.length, output, 0);
        } catch (ShortBufferException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        String fin = new String(output, StandardCharsets.UTF_16);
        return fin;
    }
    public static void main(String[] args) {
        CodeHandler please = new CodeHandler();
        CodeHandler client = new CodeHandler();
        String[] pcoords = please.getPublicCoords();
        String[] clcoords = client.getPublicCoords();
        println(please.publickey.getAlgorithm());
        println(please.publickey.getFormat());
        println(please.publickey.getEncoded());
        println(please.publickey.getW());
        println(please.publickey.getW().getAffineX());
        println(please.publickey.getW().getAffineY());
        please.setOther_calcShared(clcoords[0], clcoords[1]);
        println(please.Sharedkey.getEncoded());
        client.setOther_calcShared(pcoords[0], pcoords[1]);
        println(client.Sharedkey.getEncoded());

        String begging = "HI PLEASE WORK";
        String pencode = please.encoder(begging);
        println(pencode);
        String cldecode = client.decoder(pencode);
        println(cldecode);
    }
}
