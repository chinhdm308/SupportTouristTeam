package dmc.supporttouristteam.data.model.gg;

public final class NearPlacesResponse {
    public final Result[] results;
    public final String status;

    public NearPlacesResponse(Result[] results, String status) {
        this.results = results;
        this.status = status;
    }

    public static final class Result {
        public final Geometry geometry;
        public final String place_id;
        public final String vicinity;
        public final String icon;
        public final String name;

        public Result(Geometry geometry, String place_id, String vicinity, String icon, String name) {
            this.geometry = geometry;
            this.place_id = place_id;
            this.vicinity = vicinity;
            this.icon = icon;
            this.name = name;
        }

        public static final class Geometry {
            public final Location location;

            public Geometry(Location location) {
                this.location = location;
            }

            public static final class Location {
                public final double lat;
                public final double lng;

                public Location(double lat, double lng) {
                    this.lat = lat;
                    this.lng = lng;
                }
            }
        }
    }
}

