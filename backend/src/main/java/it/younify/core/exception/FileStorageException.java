package it.younify.core.exception;

import lombok.Getter;

@Getter
public class FileStorageException extends RuntimeException {

    private String internalCode;

    public FileStorageException(String message){

        super(message);
    }

    public FileStorageException(String message, String internalCode){

        super(message);
        this.internalCode = internalCode;
    }
}
