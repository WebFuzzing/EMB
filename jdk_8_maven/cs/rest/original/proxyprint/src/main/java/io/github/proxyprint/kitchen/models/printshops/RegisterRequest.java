/*
 * Copyright 2016 Jorge Caldas, José Cortez
 * José Francisco, Marcelo Gonçalves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.models.printshops;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 *
 * @author josesousa
 */
@Entity
@Table(name = "register_requests")
public class RegisterRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, name = "manager_name")
    private String managerName;
    @Column(nullable = false, name = "manager_username")
    private String managerUsername;
    @Column(nullable = false, name = "manager_email")
    private String managerEmail;
    @Column(nullable = false, name = "manager_password")
    private String managerPassword;
    @Column(nullable = false, name = "pshop_address")
    private String pShopAddress;
    @Column(nullable = false, name = "pshop_latitude")
    private Double pShopLatitude;
    @Column(nullable = false, name = "pshop_longitude")
    private Double pShopLongitude;
    @Column(nullable = false, name = "pshop_nif")
    private String pShopNIF;
    @Column(nullable = false, name = "pshop_name")
    private String pShopName;
    @JsonIgnore
    @Column(nullable = false, name = "accepted")
    private boolean accepted = false;
    @Column(nullable = false, name = "pshop_date_request")
    private String pShopDateRequest;
    @Column(nullable = true, name = "pshop_date_request_accepted")
    private String pShopDateRequestAccepted;

    public RegisterRequest() {
        // Return a Calendar based on default Locale and TimeZone
        this.pShopDateRequest = GregorianCalendarToString((GregorianCalendar) GregorianCalendar.getInstance());
        this.pShopDateRequestAccepted = null;
    }

    public RegisterRequest(String managerName, String managerUsername, String managerEmail, String managerPassword, String pShopAddress, Double pShopLatitude, Double pShopLongitude, String pShopNIF, String pShopName, boolean accepted) {
        this.managerName = managerName;
        this.managerUsername = managerUsername;
        this.managerEmail = managerEmail;
        this.managerPassword = managerPassword;
        this.pShopAddress = pShopAddress;
        this.pShopLatitude = pShopLatitude;
        this.pShopLongitude = pShopLongitude;
        this.pShopNIF = pShopNIF;
        this.pShopName = pShopName;
        this.accepted = accepted;
        this.pShopDateRequest = GregorianCalendarToString((GregorianCalendar) GregorianCalendar.getInstance());
        this.pShopDateRequestAccepted = null;
    }

    public long getId() {
        return id;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getManagerUsername() {
        return managerUsername;
    }

    public void setManagerUsername(String managerUsername) {
        this.managerUsername = managerUsername;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }

    public String getpShopAddress() {
        return pShopAddress;
    }

    public void setpShopAddress(String pShopAddress) {
        this.pShopAddress = pShopAddress;
    }

    public Double getpShopLatitude() {
        return pShopLatitude;
    }

    public void setpShopLatitude(Double pShopLatitude) {
        this.pShopLatitude = pShopLatitude;
    }

    public Double getpShopLongitude() {
        return pShopLongitude;
    }

    public void setpShopLongitude(Double pShopLongitude) {
        this.pShopLongitude = pShopLongitude;
    }

    public String getpShopNIF() {
        return pShopNIF;
    }

    public void setpShopNIF(String pShopNIF) {
        this.pShopNIF = pShopNIF;
    }

    public String getpShopName() {
        return pShopName;
    }

    public void setpShopName(String pShopName) {
        this.pShopName = pShopName;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getpShopDateRequest() {
        return pShopDateRequest;
    }

    public String getpShopDateRequestAccepted() {
        return pShopDateRequestAccepted;
    }

    public void setpShopDateRequestAccepted(GregorianCalendar date) {
        this.pShopDateRequestAccepted = GregorianCalendarToString(date);
    }

    public RegisterRequest(long id, String managerName, String managerUsername, String managerEmail, String managerPassword, String pShopAddress, Double pShopLatitude, Double pShopLongitude, String pShopNIF, String pShopName, String pShopDateRequest, String pShopDateRequestAccepted) {
        this.id = id;
        this.managerName = managerName;
        this.managerUsername = managerUsername;
        this.managerEmail = managerEmail;
        this.managerPassword = managerPassword;
        this.pShopAddress = pShopAddress;
        this.pShopLatitude = pShopLatitude;
        this.pShopLongitude = pShopLongitude;
        this.pShopNIF = pShopNIF;
        this.pShopName = pShopName;
        this.pShopDateRequest = pShopDateRequest;
        this.pShopDateRequestAccepted = pShopDateRequestAccepted;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" + "id=" + id + ", managerName=" + managerName + ", managerUsername=" + managerUsername + ", managerEmail=" + managerEmail + ", managerPassword=" + managerPassword + ", pShopAddress=" + pShopAddress + ", pShopLatitude=" + pShopLatitude + ", pShopLongitude=" + pShopLongitude + ", pShopNIF=" + pShopNIF + ", pShopName=" + pShopName + ", accepted=" + accepted + ", pShopDateRequest=" + pShopDateRequest + ", pShopDateRequestAccepted=" + pShopDateRequestAccepted + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 67 * hash + Objects.hashCode(this.managerName);
        hash = 67 * hash + Objects.hashCode(this.managerUsername);
        hash = 67 * hash + Objects.hashCode(this.managerEmail);
        hash = 67 * hash + Objects.hashCode(this.managerPassword);
        hash = 67 * hash + Objects.hashCode(this.pShopAddress);
        hash = 67 * hash + Objects.hashCode(this.pShopLatitude);
        hash = 67 * hash + Objects.hashCode(this.pShopLongitude);
        hash = 67 * hash + Objects.hashCode(this.pShopNIF);
        hash = 67 * hash + Objects.hashCode(this.pShopName);
        hash = 67 * hash + (this.accepted ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.pShopDateRequest);
        hash = 67 * hash + Objects.hashCode(this.pShopDateRequestAccepted);
        return hash;
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
        final RegisterRequest other = (RegisterRequest) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.accepted != other.accepted) {
            return false;
        }
        if (!Objects.equals(this.managerName, other.managerName)) {
            return false;
        }
        if (!Objects.equals(this.managerUsername, other.managerUsername)) {
            return false;
        }
        if (!Objects.equals(this.managerEmail, other.managerEmail)) {
            return false;
        }
        if (!Objects.equals(this.managerPassword, other.managerPassword)) {
            return false;
        }
        if (!Objects.equals(this.pShopAddress, other.pShopAddress)) {
            return false;
        }
        if (!Objects.equals(this.pShopNIF, other.pShopNIF)) {
            return false;
        }
        if (!Objects.equals(this.pShopName, other.pShopName)) {
            return false;
        }
        if (!Objects.equals(this.pShopDateRequest, other.pShopDateRequest)) {
            return false;
        }
        if (!Objects.equals(this.pShopDateRequestAccepted, other.pShopDateRequestAccepted)) {
            return false;
        }
        if (!Objects.equals(this.pShopLatitude, other.pShopLatitude)) {
            return false;
        }
        if (!Objects.equals(this.pShopLongitude, other.pShopLongitude)) {
            return false;
        }
        return true;
    }

    /**
     * From GregoriaCalendar to String.
     *
     * @param c, GregorianCalendar instance
     * @return Well formated string for display
     */
    private String GregorianCalendarToString(GregorianCalendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        sdf.setCalendar(c);
        String dateFormatted = sdf.format(c.getTime());
        return dateFormatted;
    }
}
