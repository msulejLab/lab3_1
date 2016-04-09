package pl.com.bottega.ecommerce.sales.application.api.handler;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservationRepositoryStub implements ReservationRepository {
    List<Reservation> reservations = new ArrayList<>();

    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public Reservation load(Id reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(reservationId)) {
                return reservation;
            }
        }

        Reservation.ReservationStatus status = Reservation.ReservationStatus.OPENED;
        ClientData clientData = new ClientData(Id.generate(), "Bob");
        Date createDate = new Date(Calendar.getInstance().getTimeInMillis());

        return new Reservation(reservationId, status, clientData, createDate);
    }

    public int size() {
        return reservations.size();
    }

    public void clear(){
        reservations.clear();
    }
}
