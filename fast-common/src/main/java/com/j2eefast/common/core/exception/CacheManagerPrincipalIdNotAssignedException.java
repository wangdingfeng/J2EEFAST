package com.j2eefast.common.core.exception;

public class CacheManagerPrincipalIdNotAssignedException extends RuntimeException  {
	
	private static final long serialVersionUID = 1L;
	
	private static final String MESSAGE = "CacheManager didn't assign Principal Id field name!";

    public CacheManagerPrincipalIdNotAssignedException() {
        super(MESSAGE);
    }
}
