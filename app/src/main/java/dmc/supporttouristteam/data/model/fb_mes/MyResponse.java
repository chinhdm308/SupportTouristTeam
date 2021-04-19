package dmc.supporttouristteam.data.model.fb_mes;

import java.util.List;

public class MyResponse {
    public long multicast_id;
    public int success, failure, canonical_ids;
    public List<Result> results;
}
