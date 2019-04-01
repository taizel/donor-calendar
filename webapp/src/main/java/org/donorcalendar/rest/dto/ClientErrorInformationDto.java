package org.donorcalendar.rest.dto;

public class ClientErrorInformationDto {
    private String errorMessage;
    private String requestUrl;
    private String requestHttpMethod;

    public ClientErrorInformationDto(String errorMessage, String requestUrl, String requestHttpMethod) {
        this.errorMessage = errorMessage;
        this.requestUrl = requestUrl;
        this.requestHttpMethod = requestHttpMethod;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestHttpMethod() {
        return requestHttpMethod;
    }

    public void setRequestHttpMethod(String requestHttpMethod) {
        this.requestHttpMethod = requestHttpMethod;
    }
}