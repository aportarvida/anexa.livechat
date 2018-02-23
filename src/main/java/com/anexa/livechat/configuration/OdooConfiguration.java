package com.anexa.livechat.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.val;

@Configuration
public class OdooConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "odoo.livechat")
	public OdooProperties odooProperties() {
		val result = new OdooProperties();
		return result;
	}
}
