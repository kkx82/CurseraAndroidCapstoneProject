package com.appspot.potlachkk.mobile.events;

import com.appspot.potlachkk.mobile.models.LoginStatus;

public class LogoutOKResponse {
	private LoginStatus status;

	public LoginStatus getStatus() {
		return status;
	}

	public void setStatus(LoginStatus status) {
		this.status = status;
	}

	public LogoutOKResponse(LoginStatus status) {
		super();
		this.status = status;
	}
}
