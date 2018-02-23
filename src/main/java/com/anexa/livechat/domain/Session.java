package com.anexa.livechat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private int channel;
    
    private String db;
    
    private String channelPhoneNumber;
    
    private String contactPhoneNumber;
    
    private int resId;

}
