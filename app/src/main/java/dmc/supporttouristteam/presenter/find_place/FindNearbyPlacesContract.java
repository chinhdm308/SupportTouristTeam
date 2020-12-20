package dmc.supporttouristteam.presenter.find_place;

import dmc.supporttouristteam.data.model.MyLocation;
import dmc.supporttouristteam.data.model.Place;

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
