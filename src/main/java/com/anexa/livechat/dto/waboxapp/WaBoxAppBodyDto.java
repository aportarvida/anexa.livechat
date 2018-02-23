package com.anexa.livechat.dto.waboxapp;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaBoxAppBodyDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String text;

	private String caption;
	
	private String mimetype;
	
	private String url;

	private String thumb;
	
	private Long size;
}
