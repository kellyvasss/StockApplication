package user;

import org.apache.shiro.crypto.hash.Sha256Hash;

import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.security.SecureRandom;

public class hash {

    public static String hash(String raw) {

        ByteSource salt = generateSalt();

        String hashedPassword = new SimpleHash("SHA-256", raw, salt, 10000).toHex();

        return hashedPassword;

    }
    public static ByteSource generateSalt() {

        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return ByteSource.Util.bytes(saltBytes);
    }
}
