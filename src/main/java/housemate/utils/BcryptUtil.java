package housemate.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BcryptUtil {

    final int LOG_ROUNDS = 12;

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    public boolean checkpw(String pasword, String hashPashword) {
        return BCrypt.checkpw(pasword, hashPashword);
    }
}
