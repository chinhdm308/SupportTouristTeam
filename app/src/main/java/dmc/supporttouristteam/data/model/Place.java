package dmc.supporttouristteam.data.model;

public class Place {
    double lat;
    double lng;
    String icon;
    String name;
    String place_id;
    String vicinity;

    public Place() {
    }

    public Place(double lat, double lng, String icon, String name, String place_id, String vicinity) {
        this.lat = lat;
        this.lng = lng;
        this.icon = icon;
        this.name = name;
        this.place_id = place_id;
        this.vicinity = vicinity;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "Place{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", place_id='" + place_id + '\'' +
                ", vicinity='" + vicinity + '\'' +
                '}';
    }
}
