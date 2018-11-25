package org.donorcalendar.persistence;

import org.donorcalendar.model.UserSecurityDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_security")
public class UserSecurityDetailsEntity {

    @Id
    private Long userId;

    @NotNull
    private String password;

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

    UserSecurityDetails getUserSecurityDetails() {
        return new UserSecurityDetails(this.password);
    }
}