package com.anexa.livechat.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.val;

@Configuration
public class WaBoxAppConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "waboxapp.chat")
	public WaBoxAppProperties waBoxAppProperties() {
		val result = new WaBoxAppProperties();
		return result;
	}
}
