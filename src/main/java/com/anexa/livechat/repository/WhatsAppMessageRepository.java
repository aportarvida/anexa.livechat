package com.anexa.livechat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anexa.livechat.domain.WhatsAppMessage;

public interface WhatsAppMessageRepository extends JpaRepository<WhatsAppMessage, Long> {
	
}
