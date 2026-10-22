import javacardx.crypto.AEADCipher;
import javacardx.crypto.Cipher;
import javacard.security.GenericSecretKey;
import javacard.security.KeyBuilder;
import javacard.security.Key;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.lang.Math;
import java.util.Arrays;
import java.math.BigInteger;


//HEY LOOK AT THIS
import static java.lang.IO.println;

public class CodeHandler {
    private AEADCipher cipher = (AEADCipher) Cipher.getInstance((byte) 0xf3, false);
    private SecureRandom keygen;
    {
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

    private ECPrivateKey privkey;
    private GenericSecretKey Sharedkey;
    public GenericSecretKey otherKey;
    public ECPublicKey publickey;


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
        byte[] y_bytes = other_y.getBytes();
        BigInteger new_x = new BigInteger(x_bytes);
        BigInteger new_y = new BigInteger(y_bytes);

        /*Okay so with EC diffie-hellman, especially using the same curve and base point
        (as we are doing since we're using a standard curve specification)
        The public keys are just the point times the integer that is the private key
        SO! when you multiply their public key by your own private key, you wind up
        with a shared key due to the commutative property and yadda yadda.

        Basically, the shared key is their pub * your private, both sides do it.*/
        new_x = new_x.multiply(privkey.getS());
        new_y = new_y.multiply(privkey.getS());
        int len = new_x.toByteArray().length + new_y.toByteArray().length;
        String sharecombine = new_x.toString().concat(new_y.toString());


        String combine = other_x.concat(other_y);
        byte[] fin = combine.getBytes();

        //Okay the other key is the coordinates of W aka the X and Y that we transmitted earlier.
        GenericSecretKey tester = (GenericSecretKey) KeyBuilder.buildKey((byte) 0x14, (short) fin.length, false);
        println(tester);
        tester.setKey(fin, (short) 0, (short) fin.length);
        if (tester instanceof GenericSecretKey) {
            this.otherKey = (GenericSecretKey) tester;
        }
        this.otherKey.setKey(fin, (short) 0, (short) fin.length);



        byte[] share = sharecombine.getBytes();
        tester = (GenericSecretKey) KeyBuilder.buildKey((byte) 0x14, (short) fin.length, false);
        if (tester instanceof GenericSecretKey) {
            this.Sharedkey = (GenericSecretKey) tester;
        }
        this.Sharedkey.setKey(share, (short) 0, (short) share.length);

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
    public String encoder(String message) {
        byte[] iv = new byte[12];
        keygen.nextBytes(iv);
        cipher.init(Sharedkey, (byte) 0x2, iv, (short) 0, (short) 12);

        byte[] input = message.getBytes(StandardCharsets.UTF_16);
        //This should be how long the encrypted message is
        //(source:https://www.trustedcalcs.com/en-us/guides/dev-tools/aes-gcm-encryption)
        byte[] output = new byte[(int) Math.ceil(input.length * 1.33) + 28];
        short ciphertext = cipher.doFinal(input, (short) 0, (short) input.length, output, (short) output.length);

        String fin = new String(output, StandardCharsets.UTF_16);
        return fin;
    }

    /*GAME PLAN 2: Call this on every receipt once the shared key is established. */
    public String decoder(String ciphertext) {
        byte[] text = ciphertext.getBytes(StandardCharsets.UTF_16);
        byte[] iv = Arrays.copyOfRange(text, 0, 11);
        byte[] output = new byte[text.length];
        //I'm just simplifying the length there, it's inefficient but I don't wanna deal.

        cipher.init(Sharedkey, (byte) 0x1, iv, (short) 0, (short) 12);
        short plaintext = cipher.doFinal(text, (short) 0, (short) text.length, output, (short) output.length);
        String fin = new String(output, StandardCharsets.UTF_16);
        return fin;
    }
    public static void main(String[] args) {
        CodeHandler please = new CodeHandler();
        CodeHandler client = new CodeHandler();
        String[] pcoords = please.getPublicCoords();
        String[] clcoords = client.getPublicCoords();
        please.setOther_calcShared(clcoords[0], clcoords[1]);
    }
}
