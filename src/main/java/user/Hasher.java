package user;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.security.SecureRandom;

public class Hasher {

    // Hashar en valfri string
    public static String hash(String raw) {

        ByteSource salt = generateSalt();

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
    public static Boolean verify(String raw, String hashed, ByteSource salt) {
        String toVerify = new SimpleHash("SHA-256", raw, salt,100).toHex();
        return toVerify.equals(hashed);
    }
}

