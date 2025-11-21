package org.donorcalendar.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.donorcalendar.model.UserCredentials;

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