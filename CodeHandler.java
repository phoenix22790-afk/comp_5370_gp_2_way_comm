import javacardx.crypto.AEADCipher;
import javacardx.crypto.Cipher;
import javacard.crypto.GenericSecretKey;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.lang.Math;
import java.util.Arrays;
import java.security.spec.NamedParameterSpec;
import java.math.BigInteger;

public class CodeHandler {
    private AEADCipher cipher = Cipher.getInstance(0xf3, false);
    private SecureRandom keygen = SecureRandom.getInstance("NativePRNGBlocking");
    private KeyPairGenerator pairgen = KeyPairGenerator.getInstance("EC");

    private NamedParameterSpec params = new NamedParameterSpec("ED25519");

    private ECPrivateKey privkey;
    private GenericSecretKey Sharedkey;
    public GenericSecretKey otherKey;
    public ECPublicKey publickey;
    
    
    public void CodeHandler() {
        pairgen = pairgen.initialize(params, keygen);
        KeyPair keys = pairgen.genKeyPair();
        privkey = keys.getPrivate();
        publickey = keys.getPublic();
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
        new_x = new_x.multiply(privkey);
        new_y = new_y.multiply(privkey);
        len = new_x.toByteArray().length + new_y.toByteArray().length
        String sharecombine = new_x.toString().concat(new_y.toString());
        
        
        String combine = other_x.concat(other_y);

        //Okay the other key is the coordinates of W aka the X and Y that we transmitted earlier.
        this.otherKey = new GenericPublicKey();
        this.otherKey.setKey(fin, 0, fin.length);


        this.SharedKey = new GenericPublicKey();
        byte[] share = sharecombine.getBytes();
        this.SharedKey.setKey(share, 0, share.length);

    }

    public String[] getPublicCoords() {
        /*THIS IS JUST THE PUBLIC KEY BUT
            *******LISTEN TO ME*******
        YOU NEED TO SEND THESE TO THE OTHER MACHINE
        SO THEY CAN CALCULATE THE SHARED KEY CORRECTLY. */

        ECPoint pubpoint = this.publicKey.getW()
        BigInteger pubx = pubpoint.getAffineX();
        BigInteger puby = pubpoint.getAffineY();
        String[] strings = {pubx.toString(), puby.toString()}
        return strings;
    }


    /*GAME PLAN: This gets called EVERY time a message is to be sent.
    It is also only for the Shared messages, not any of the setup/requests */
    public String encoder(String message) {
        byte[] iv = new byte[12];
        keygen.nextBytes(iv);
        cipher.init(Sharedkey, 0x2, iv, 0, 12);

        byte[] input = message.getBytes("UTF-16");
        //This should be how long the encrypted message is 
        //(source:https://www.trustedcalcs.com/en-us/guides/dev-tools/aes-gcm-encryption)
        byte[] output = new byte[(int) Math.ceil(input.length * 1.33) + 28];
        short ciphertext = cipher.doFinal(input, 0, input.length, output, output.length);

        String fin = new String(output, "UTF-16");
        return fin;
    }

    /*GAME PLAN 2: Call this on every receipt once the shared key is established. */
    public String decoder(String ciphertext) {
        byte[] text = ciphertext.getBytes("UTF-16");
        byte[] iv = Arrays.copyOfRange(text, 0, 11);
        byte[] output = new byte[text.length];
        //I'm just simplifying the length there, it's inefficient but I don't wanna deal.

        cipher.init(Sharedkey, 0x1, iv, 0, 12);
        short plaintext = cipherx.doFinal(text, 0, text.length, output, output.length);
        String fin = new String(output, "UTF-16");
        return fin;
    }

    

}