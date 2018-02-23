package com.anexa.livechat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anexa.livechat.domain.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

	Session findOneByChannelAndDbAndChannelPhoneNumberAndContactPhoneNumber(int channel, String db, String channelPhoneNumber, String contactPhoneNumber);

}
