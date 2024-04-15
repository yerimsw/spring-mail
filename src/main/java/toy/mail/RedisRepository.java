package toy.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RedisRepository {

    private final StringRedisTemplate template;

    public RedisRepository(StringRedisTemplate template) {
        this.template = template;
    }

    public void saveJwt(String jwt, String email) {
        template.opsForValue().set(jwt, email);
        log.info("jwt saved");
    }

    public String findEmailByJwt(String jwt) {
        String email = template.opsForValue().getAndDelete(jwt);
        log.info("jwt found");
        return email == null ? "Email Not Found" : email;
    }
}
