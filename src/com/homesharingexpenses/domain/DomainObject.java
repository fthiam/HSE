package com.homesharingexpenses.domain;

/**
 * Every domain object should extend this abstract class.
 */
public abstract class DomainObject {

    // ======================================
    // =             Attributes             =
    // ======================================

    // Every domain object has a unique identifier.

	public abstract String getId();

	public abstract void setId(String id);

	public String getCname() {
		return this.getClass().getName();
	}
}
