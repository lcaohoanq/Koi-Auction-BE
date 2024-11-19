package com.swp391.koibe.exceptions;

import com.swp391.koibe.exceptions.base.DataWrongFormatException;

public class PasswordWrongFormatException extends DataWrongFormatException {

    public PasswordWrongFormatException(String message) {
        super(message);
    }

}
