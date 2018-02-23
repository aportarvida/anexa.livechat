package com.anexa.livechat.configuration;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public class WaBoxAppProperties {
	
	private String url;
	
	private String token;
}
