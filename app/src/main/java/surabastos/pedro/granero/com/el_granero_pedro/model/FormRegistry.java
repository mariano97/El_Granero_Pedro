package surabastos.pedro.granero.com.el_granero_pedro.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FormRegistry {

    private String id;
    private String firstName;
    private String secondName;
    private String birthday;
    private String age;
    private String addressRecidence;
    private String district;
    private String minicipality;
    private String phone;
    private String email;

    public FormRegistry() {
    }

    public FormRegistry(String id, String firstName, String secondName, String birthday, String age,
                        String addressRecidence, String district, String minicipality, String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthday = birthday;
        this.age = age;
        this.addressRecidence = addressRecidence;
        this.district = district;
        this.minicipality = minicipality;
        this.phone = phone;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddressRecidence() {
        return addressRecidence;
    }

    public void setAddressRecidence(String addressRecidence) {
        this.addressRecidence = addressRecidence;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMinicipality() {
        return minicipality;
    }

    public void setMinicipality(String minicipality) {
        this.minicipality = minicipality;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
