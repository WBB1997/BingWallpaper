package Util.Bing;

public class StoryBean {
    private String date;
    private String title;
    private String attribute;
    private String para1;
    private String para2;
    private String provider;
    private String imageUrl;
    private String primaryImageUrl;
    private String Country;
    private String City;
    private String Longitude;
    private String Latitude;
    private String Continent;
    private String CityInEnglish;
    private String CountryCode;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getPara1() {
        return para1;
    }

    public void setPara1(String para1) {
        this.para1 = para1;
    }

    public String getPara2() {
        return para2;
    }

    public void setPara2(String para2) {
        this.para2 = para2;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getContinent() {
        return Continent;
    }

    public void setContinent(String continent) {
        Continent = continent;
    }

    public String getCityInEnglish() {
        return CityInEnglish;
    }

    public void setCityInEnglish(String cityInEnglish) {
        CityInEnglish = cityInEnglish;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    @Override
    public String toString() {
        return "StoryBean{" +
                "date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", attribute='" + attribute + '\'' +
                ", para1='" + para1 + '\'' +
                ", para2='" + para2 + '\'' +
                ", provider='" + provider + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", primaryImageUrl='" + primaryImageUrl + '\'' +
                ", Country='" + Country + '\'' +
                ", City='" + City + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", Latitude='" + Latitude + '\'' +
                ", Continent='" + Continent + '\'' +
                ", CityInEnglish='" + CityInEnglish + '\'' +
                ", CountryCode='" + CountryCode + '\'' +
                '}';
    }
}
