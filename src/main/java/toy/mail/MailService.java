package toy.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.util.HashMap;

@Service
public class MailService {

    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisRepository redisRepository;

    private final String domain = "http://localhost:8080/mail/verify-email?token=";

    public MailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, RedisRepository redisRepository) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.redisRepository = redisRepository;
    }

    public void basicMail(String email) {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(email);
        msg.setText("Basic Mail Sending");

        try {
            this.mailSender.send(msg);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }

    public void mimeMessagePreparatorMail(String email) {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, email);
            mimeMessage.setText("MimeMessagePreparator Mail Sending");
        };

        try {
            this.javaMailSender.send(preparator);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }

    }
    public void templateMail(String email) throws MessagingException {
        // 1. MimeMessage를 JavaMailSender를 통해 create한다.
        MimeMessage message = javaMailSender.createMimeMessage();

        // 2. MimeMessage를 조작하는데 도움을 주는 MimeMessageHelper를 생성하고, 조작한다.
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Hi, there!");

        // 3. 메일 본문에 실릴 HTML을 위한 TemplateEngine을 적용한다.
        String jwt = JwtUtils.createJwt(email);
        HashMap<String, String> emailValues = new HashMap<>(); // Model 객체처럼 사용
        emailValues.put("jwt", domain + jwt);

        Context context = new Context();
        emailValues.forEach((key, value) -> {
            context.setVariable(key, value);
        });

        String html = templateEngine.process("sent-mail",context);
        helper.setText(html, true);

        // 4. jwt를 redis에 저장한다.
        redisRepository.saveJwt(jwt, email);

        // 5. 메일을 전송한다.
        javaMailSender.send(message);
    }

    public String verifyEmail(String token) {
        return redisRepository.findEmailByJwt(token);
    }
}
