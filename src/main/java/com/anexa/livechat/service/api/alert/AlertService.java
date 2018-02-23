package com.anexa.livechat.service.api.alert;

import org.springframework.transaction.annotation.Transactional;

public interface AlertService<R, M> {

	@Transactional
	M getMessage(R request);

	@Transactional
	M getMessage(R request, Throwable t);
}