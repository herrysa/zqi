package com.zqi.frame.exception;

public class ZqiException
    extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -6029262068121573969L;

    public ZqiException( final String msg ) {
        super( msg );
    }

    public ZqiException() {

    }
}
