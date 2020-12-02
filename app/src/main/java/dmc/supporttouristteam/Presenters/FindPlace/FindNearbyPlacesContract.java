package dmc.supporttouristteam.Presenters.FindPlace;

import dmc.supporttouristteam.Models.MyLocation;
import dmc.supporttouristteam.Models.Place;

public interface FindNearbyPlacesContract {

    interface View {
        void showPlace(Place place);
        void showMarkerUser();
    }

    interface Presenter {
        void doSearchPlacesNearYou(MyLocation myLocation, String[] placeTypeList, int i);
    }

    interface OnOperationListener {

    }
}
