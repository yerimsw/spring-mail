package toy.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;

@Service
public class MailService {

    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public MailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
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

        // 3. 메일 본문에 실릴 HTML을 위한 TemplateEngine을 적용한다.
        HashMap<String, String> emailValues = new HashMap<>(); // Model 객체처럼 사용
        emailValues.put("jwt", JwtUtils.createJwt(email));

        Context context = new Context();
        emailValues.forEach((key, value) -> {
            context.setVariable(key, value);
        });

        String html = templateEngine.process("sent-mail",context);
        helper.setText(html, true);

        // 4. 메일을 전송한다.
        javaMailSender.send(message);
    }
}
