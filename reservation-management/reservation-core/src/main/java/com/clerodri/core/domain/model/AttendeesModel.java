package com.clerodri.core.domain.model;

public class AttendeesModel extends UserModel{

    private String reservationStatus;

    public AttendeesModel(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public AttendeesModel(UserModel userModel, String reservationStatus) {
        super(userModel.getId(), userModel.getUsername(), userModel.getPassword(),
                userModel.getEmail(), userModel.getRole());
        this.reservationStatus = reservationStatus;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}
