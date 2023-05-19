package user;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import java.security.SecureRandom;


/**
 * Klass används för att hasha lösenord. Andvänd i följande ordning:
 *
 * 1. Generera salt med "generateSalt().toString() och tilldela det till new User om User INTE finns i databasen
 * 2. Tilldela det krypterade lösenordet till User med hash("lösenord", ByteSource.Util.bytes(u.getPasSalt())
 * 3. Vid verifiering använd "verify("lösenord", user.getPassword(), user.getPasSalt())
 */

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
        ByteSource b = ByteSource.Util.bytes(salt);
        String toVerify = new SimpleHash("SHA-256", raw, b,100).toHex();
        System.out.println("från verify lösenord: " + toVerify);
        return toVerify.equals(hashed);
    }
}

