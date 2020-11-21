package com.j2eefast.common.core.exception;

public class PrincipalIdNullException extends RuntimeException  {
	private static final long serialVersionUID = 1L;
	
	private static final String MESSAGE = "Principal Id shouldn't be null!";

    public PrincipalIdNullException(@SuppressWarnings("rawtypes") Class clazz, String idMethodName) {
        super(clazz + " id field: " +  idMethodName + ", value is null\n" + MESSAGE);
    }
}
