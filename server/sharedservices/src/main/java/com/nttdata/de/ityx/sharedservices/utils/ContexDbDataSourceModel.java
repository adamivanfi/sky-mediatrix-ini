package com.nttdata.de.ityx.sharedservices.utils;

/**
 * Data holder for the preferences needed to construct a database connection.
 */

public class ContexDbDataSourceModel {
	public ContexDbDataSourceModel(String url, String user, String password) {
        super();
        this.url = url;
        this.user = user;
        this.password = password;	
	}
	
    public static ContexDbDataSourceModel build(String url, String user, String password) {
        return new ContexDbDataSourceModel(url, user, password);
    }

    private String url;
    private String user;
    private String password;


    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        String builder = "DataSourceConfiguration [url=" + this.url + ", user=" + this.user + ", password=" + "PROTECTED" + "]";
        return builder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
        result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
        result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ContexDbDataSourceModel other = (ContexDbDataSourceModel) obj;
        if (this.password == null) {
            if (other.password != null) {
                return false;
            }
        }
        else if (!this.password.equals(other.password)) {
            return false;
        }
        if (this.url == null) {
            if (other.url != null) {
                return false;
            }
        }
        else if (!this.url.equals(other.url)) {
            return false;
        }
        if (this.user == null) {
            if (other.user != null) {
                return false;
            }
        }
        else if (!this.user.equals(other.user)) {
            return false;
        }
        return true;
    }


}
