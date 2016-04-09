package pl.com.bottega.ecommerce.system.application;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

public class SystemUser {
	private Id clientId;

	public SystemUser(Id clientId) {
		this.clientId = clientId;
	}

	/**
	 * 
	 * @return Domain model Client
	 */
	public Id getClientId() {
		return clientId;
	}

	public boolean isLoogedIn() {
		return clientId != null;
	}
}
