package user;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.security.SecureRandom;

public class Hasher {

    // Hashar en valfri string
    public static String hash(String raw, ByteSource salt) {
        String hashedPassword = new SimpleHash("SHA-256", raw, salt, 100).toHex();
        return hashedPassword;
    }
    // Genererar random byte
    public static ByteSource generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return ByteSource.Util.bytes(saltBytes);
    }

    // Kontrollerar genom att ta in användarens inskrivna uppgift, användarens lagrade uppgift + salt
    // och kör samma krypteringsalgoritm för att kolla om värdena stämmer
    public static Boolean verify(String raw, String hashed, String salt) {
        byte[] bytes = convertFromStr(salt);
        String toVerify = new SimpleHash("SHA-256", raw, bytes,100).toHex();
        return toVerify.equals(hashed);
    }
    public static byte[] convertFromStr(String salt) {
        byte[] saltBytes = new byte[salt.length()/2];
        for (int i = 0; i < saltBytes.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(salt.substring(index, index + 2), 16);
            saltBytes[i] = (byte) j;
        }
        return saltBytes;
    }
}

