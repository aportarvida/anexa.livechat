package com.anexa.livechat.configuration;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public class OdooProperties {
	
	private String url;
	
	private String DefaultUser;
	
	private String DefaultPassword;
			
	private int maxAttemptCount;
}
