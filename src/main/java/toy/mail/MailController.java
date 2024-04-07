package toy.mail;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/basic")
    public String mailSendTestV1(@RequestParam String email, Model model) {
        mailService.basicMail(email);
        model.addAttribute("email", email);
        return "result";
    }

    @GetMapping("/java-mail-sender")
    public String mailSendTestV2(@RequestParam String email, Model model) {
        mailService.mimeMessagePreparatorMail(email);
        model.addAttribute("email", email);
        return "result";
    }

    @GetMapping("/template-engine")
    public String mailSendTestV4(@RequestParam String email, Model model) throws MessagingException {
        mailService.templateMail(email);
        model.addAttribute("email", email);
        return "result";
    }
}
