package com.anexa.livechat.dto.waboxapp;

import java.io.Serializable;

import com.anexa.livechat.enums.WaBoxAppContactType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaBoxAppContactDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String phoneNumber;

	private String name;

	private WaBoxAppContactType type;
}
