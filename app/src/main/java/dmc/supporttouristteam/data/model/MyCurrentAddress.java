package dmc.supporttouristteam.data.model;

public final class MyCurrentAddress {
    public final Result[] results;
    public final String status;

    public MyCurrentAddress(Result[] results, String status) {
        this.results = results;
        this.status = status;
    }

    public Result[] getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public static class Result {
        public final String formatted_address;
        public final String place_id;

        public Result(String formatted_address, String place_id) {
            this.formatted_address = formatted_address;
            this.place_id = place_id;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public String getPlace_id() {
            return place_id;
        }
    }
}
