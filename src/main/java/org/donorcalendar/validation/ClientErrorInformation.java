package org.donorcalendar.validation;

public class ClientErrorInformation {
    String errorMessage;
    String requestUrl;
    String requestHttpMethod;

    public ClientErrorInformation(String errorMessage, String requestUrl, String requestHttpMethod) {
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