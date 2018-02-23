package com.anexa.livechat.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.val;


@Configuration
public class MailConfiguration {

	public MailConfiguration() {
		super();
	}

	@Bean
	@ConfigurationProperties(prefix = "odoo.livechat.mail")
	public MailProperties mailProperties() {
		val result = new MailProperties();
		return result;
	}

}