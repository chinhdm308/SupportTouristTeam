package dmc.supporttouristteam.data.model;

public class LovePlace {
    private String placeName;
    private String placeAddress;
    private String placeDescription;
    private String placeImageUrl;
    private double lat;
    private double lng;

    public LovePlace() {

    }

    public LovePlace(String placeName, String placeAddress, String placeDescription, String placeImageUrl, double lat, double lng) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeDescription = placeDescription;
        this.placeImageUrl = placeImageUrl;
        this.lat = lat;
        this.lng = lng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getPlaceImageUrl() {
        return placeImageUrl;
    }

    public void setPlaceImageUrl(String placeImageUrl) {
        this.placeImageUrl = placeImageUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
