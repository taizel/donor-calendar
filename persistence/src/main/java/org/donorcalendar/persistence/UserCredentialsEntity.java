package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_credentials")
public class UserCredentialsEntity {

    @Id
    private Long userId;

    @NotNull
    private String password;

    protected UserCredentialsEntity() {
    }

    UserCredentialsEntity(Long userId, UserCredentials userCredentials) {
        this.userId = userId;
        this.password = userCredentials.getPassword();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    UserCredentials getUserCredentials() {
        return new UserCredentials(this.password);
    }
}