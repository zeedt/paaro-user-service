package com.zeed.user.services;

import com.zeed.user.sendgrid.SendGridEmail;
import com.zeed.usermanagement.models.EmailNotification;
import com.zeed.usermanagement.models.ManagedUser;
import org.hibernate.engine.spi.Managed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class UserEmailService {

    private TemplateEngine templateEngine;

    @Autowired
    private SendGridEmail sendGridEmail;

    @Value("${email.activate-account:email-template/account-activation}")
    private String activateAccountPath;

    @Value("${email.activate-account-subject:Paaro - Account activation}")
    private String activateAccountSubject;

    @Value("${email.activate-account:email-template/account-deactivation}")
    private String deactivateAccountPath;

    @Value("${email.activate-account-subject:Paaro - Account deactivation}")
    private String deactivateAccountSubject;


    @Value("${email.forgot-password:email-template/password-reset}")
    private String forgotPasswordPath;

    @Value("${email.forgot-password-subject:Paaro - Password reset}")
    private String forgotPasswordSubject;


    @Value("${email.forgot-password:email-template/admin-user-creation}")
    private String newAdminUserPath;

    @Value("${email.forgot-password-subject:Paaro - Admin user created}")
    private String newAdminUserSubject;

    @Autowired
    private UserEmailService(@Qualifier("emailTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendActivateAccountEmail(ManagedUser managedUser) throws IOException {

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getActivationEmailContent(managedUser));
        emailNotification.setSubject(activateAccountSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(managedUser.getEmail());

        sendGridEmail.sendEmailWithNoAttachment(emailNotification);
    }

    private String getActivationEmailContent(ManagedUser managedUser) {
        Context context = new Context();
        context.setVariable("email", managedUser.getEmail());
        context.setVariable("firstName", managedUser.getFirstName());
        return this.templateEngine.process(activateAccountPath,context);
    }

    @Async
    public void sendDeactivateAccountEmail(ManagedUser managedUser) throws IOException {

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getDeactivationEmailContent(managedUser));
        emailNotification.setSubject(deactivateAccountSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(managedUser.getEmail());

        sendGridEmail.sendEmailWithNoAttachment(emailNotification);
    }

    private String getDeactivationEmailContent(ManagedUser managedUser) {
        Context context = new Context();
        context.setVariable("email", managedUser.getEmail());
        context.setVariable("firstName", managedUser.getFirstName());
        return this.templateEngine.process(deactivateAccountPath,context);
    }

    @Async
    public void sendForgotPasswordEmail(String email, String password, String firstName) throws IOException {

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getForgotPasswordEmailContent(firstName, password));
        emailNotification.setSubject(forgotPasswordSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(email);

        sendGridEmail.sendEmailWithNoAttachment(emailNotification);
    }

    private String getForgotPasswordEmailContent(String firstName, String password) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("password", password);
        return this.templateEngine.process(forgotPasswordPath,context);
    }

    @Async
    public void sendNewAdminUserEmail(String email, String password, String firstName) throws IOException {

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setContent(getNewAdminUserEmailContent(firstName, password));
        emailNotification.setSubject(newAdminUserSubject);
        emailNotification.setTo("yusufsaheedtaiwo@gmail.com");
        emailNotification.addTo("soluwawunmi@gmail.com");
        emailNotification.addTo(email);

        sendGridEmail.sendEmailWithNoAttachment(emailNotification);
    }

    private String getNewAdminUserEmailContent(String firstName, String password) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("password", password);
        return this.templateEngine.process(newAdminUserPath,context);
    }

}
