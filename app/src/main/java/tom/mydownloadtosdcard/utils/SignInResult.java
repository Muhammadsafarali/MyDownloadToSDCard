package tom.mydownloadtosdcard.utils;

import java.util.Date;

/**
 * Created by 3dium on 15.12.2017.
 */

public class SignInResult {

    private String token;
    private Date exp_date;
    private boolean error;
    private String description;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExp_date() {
        return exp_date;
    }

    public void setExp_date(Date exp_date) {
        this.exp_date = exp_date;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
