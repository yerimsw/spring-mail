package toy.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;

@Controller
@RequestMapping("/mail")
public class MailController {
    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public MailController(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @GetMapping("/basic")
    public void mailSendTestV1() {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo("swyr1016@gmail.com");
        msg.setText("Test Mail Sending");
        try {
            this.mailSender.send(msg);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }

    @GetMapping("/java-mail-sender")
    public void mailSendTestV2() {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, "swyr1016@gamil.com");
            mimeMessage.setFrom("swyr1016@gmail.com");
            mimeMessage.setText("Test Mail Sending - JavaMailSender");
        };

        try {
            this.javaMailSender.send(preparator);
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }

    @GetMapping("/template-engine")
    public String mailSendTestV4(@RequestParam String name, String email, Model model) throws MessagingException {
        // 1. MimeMessage를 JavaMailSender를 통해 create한다.
        MimeMessage message = javaMailSender.createMimeMessage();

        // 2. MimeMessage를 조작하는데 도움을 주는 MimeMessageHelper를 생성하고, 조작한다.
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("swyr1016@gmail.com");

        // 3. 메일 본문에 실릴 HTML을 위한 TemplateEngine을 적용한다.
        HashMap<String, String> emailValues = new HashMap<>(); // Model 객체처럼 사용
        emailValues.put("name",name);
        emailValues.put("jwt","jwtToken");

        Context context = new Context();
        emailValues.forEach((key, value) -> {
            context.setVariable(key, value);
        });

        String html = templateEngine.process("mail",context);
        helper.setText(html, true);

        // 4. 메일을 전송한다.
        javaMailSender.send(message);

        model.addAttribute("email", email);

        return "result";
    }
}
