package com.anexa.livechat.service.impl.odoo;

import org.apache.xmlrpc.XmlRpcException;

public class SessionDeletedException extends RuntimeException {

	public SessionDeletedException(XmlRpcException e) {
		super(e);
	}

	private static final long serialVersionUID = 1L;

}
