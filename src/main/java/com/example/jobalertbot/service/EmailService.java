package com.example.jobalertbot.service;

import com.example.jobalertbot.config.EmailProperties;
import com.example.jobalertbot.model.JobPosting;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public EmailService(JavaMailSender mailSender,
                        EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
    }

    public void sendJobAlert(List<JobPosting> jobs) {
        System.out.println("Sending email for " + jobs.size() + " jobs...");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailProperties.getFrom());
            helper.setTo(emailProperties.getTo().toArray(new String[0]));
            helper.setSubject("New Matching Jobs Found (" + jobs.size() + ")");

            helper.setText(buildHtml(jobs), true);

            mailSender.send(message);

            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildHtml(List<JobPosting> jobs) {
        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h2>New Matching Jobs Found</h2>");
        html.append("<p>Total jobs: <b>")
                .append(jobs.size())
                .append("</b></p>");

        html.append("<ul>");

        for (JobPosting job : jobs) {
            html.append("<li style='margin-bottom: 12px;'>")
                    .append("<b>")
                    .append(escape(job.getTitle()))
                    .append("</b>")
                    .append(" - ")
                    .append(escape(job.getCompany()))
                    .append("<br/>")
                    .append("Location: ")
                    .append(escape(job.getLocation()))
                    .append("<br/>")
                    .append("Score: ")
                    .append(job.getRelevanceScore())
                    .append("<br/>")
                    .append("<a href='")
                    .append(job.getUrl())
                    .append("'>Apply Here</a>")
                    .append("</li>");
        }

        html.append("</ul>");
        html.append("</body></html>");

        return html.toString();
    }

    private String escape(String value) {
        return value == null ? "" : value;
    }
}

//package com.example.jobalertbot.service;
//
//import com.example.jobalertbot.model.JobPosting;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class EmailService {
//
//    public void sendJobAlert(List<JobPosting> jobs) {
//        System.out.println();
//        System.out.println("==================================================");
//        System.out.println(" NEW MATCHING JOBS FOUND (" + jobs.size() + ")");
//        System.out.println("==================================================");
//
//        for (int i = 0; i < jobs.size(); i++) {
//            JobPosting job = jobs.get(i);
//
//            System.out.println();
//            System.out.println((i + 1) + ". " + job.getTitle());
//            System.out.println("Company   : " + job.getCompany());
//            System.out.println("Location  : " + job.getLocation());
//            System.out.println("Score     : " + job.getRelevanceScore());
//            System.out.println("Posted On : " + job.getPostedDate());
//            System.out.println("Apply URL : " + job.getUrl());
//        }
//
//        System.out.println();
//        System.out.println("==================================================");
//        System.out.println(" END OF JOB ALERT");
//        System.out.println("==================================================");
//        System.out.println();
//    }
//}