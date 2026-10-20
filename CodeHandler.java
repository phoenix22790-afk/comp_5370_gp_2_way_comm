import javacardx.crypto.AEADCipher;
import javacardx.crypto.Cipher;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.lang.Math;
import java.util.Arrays;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class CodeHandler {
    private AEADCipher cipher = Cipher.getInstance(0xf3, false);
    private SecureRandom keygen = SecureRandom.getInstance("NativePRNGBlocking");
    private KeyPairGenerator pairgen = KeyPairGenerator.getInstance("EC");

    private X9ECParameters curveParams = CustomNamedCurves.getByName("Curve25519");
    private ECParameterSpec params = new ECParameterSpec(curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());

    private ECPrivateKey privkey;
    private ECPrivateKey Sharedkey;
    public ECPublicKey publickey;
    public ECPublicKey otherKey;
    
    public void CodeHandler() {
        pairgen = pairgen.initialize(params, keygen);
        KeyPair keys = pairgen.genKeyPair();
        privkey = keys.getPrivate();
        publickey = keys.getPublic();
    }

    public void setOtherKey(ECPublicKey other) {
        this.otherKey = other;
    }

    public void calcSharedKey() {
        Sharedkey = privkey * otherKey;
    }


    /*GAME PLAN: This gets called EVERY time a message is to be sent.
    It is also only for the Shared messages, not any of the setup/requests */
    public String encoder(String message) {
        byte[] iv = new byte[12];
        keygen.nextBytes(iv);
        cipher.init(Sharedkey, 0x2, iv, 0, 12);

        byte[] input = message.getBytes("UTF-16");
        byte[] output = new byte[(int) Math.ceil(input.length * 1.33) + 28];
        short ciphertext = cipher.doFinal(input, 0, input.length, output, output.length);

        String fin = new String(output, "UTF-16");
        return fin;
    }


    public String decoder(String ciphertext) {
        byte[] text = ciphertext.getBytes("UTF-16");
        byte[] iv = Arrays.copyOfRange(text, 0, 11);
        byte[] output = new byte[text.length];

        cipher.init(Sharedkey, 0x1, iv, 0, 12);
        short plaintext = cipher.doFinal(text, 0, text.length, output, output.length);
        String fin = new String(output, "UTF-16");
        return fin;
    }

    

}