/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.bcp.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author erlend
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${bcp.mail.from:}")
    private String from;

    public void send(String to, String subject, String text) {
        if (mailSender == null) {
            LOGGER.info("{} => {}\n{}", to, subject, text);
        } else {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            if (!from.isEmpty()) {
                mailMessage.setFrom(from);
            }
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            mailSender.send(mailMessage);
            LOGGER.info("Message '{}' sent to '{}'.", subject, to);
        }
    }
}
