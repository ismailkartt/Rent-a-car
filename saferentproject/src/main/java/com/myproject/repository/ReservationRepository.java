package com.myproject.repository;

import com.myproject.domain.Car;
import com.myproject.domain.Reservation;
import com.myproject.domain.User;
import com.myproject.domain.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {


    @Query("Select r From Reservation r " +
            "Join Fetch Car c on r.car=c.id Where " +
            "c.id=:carId and (r.status not in :status) and :pickUpTime Between r.pickUpTime and r.dropOfTime " +
            "or " +
            "c.id=:carId and (r.status not in :status) and :dropOfTime Between r.pickUpTime and r.dropOfTime " +
            "or " +
            "c.id=:carId and (r.status not in :status) and (r.pickUpTime Between :pickUpTime and :dropOfTime)")
    List<Reservation> checkCarStatus(@Param("carId") Long carId,
                                     @Param("pickUpTime") LocalDateTime pickUpTime,
                                     @Param("dropOfTime") LocalDateTime  dropOfTime,
                                     @Param("status") ReservationStatus[] status);


    @EntityGraph(attributePaths = {"car","car.image"})
    List<Reservation> findAll();


    @EntityGraph(attributePaths = {"car","car.image"})
    Page<Reservation> findAll(Pageable pageable);


    @EntityGraph(attributePaths = {"car","car.image","user"})
    Optional<Reservation> findById(Long id);

    @EntityGraph(attributePaths = {"car","car.image","user"})
    Page<Reservation> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.image","user"})
    Optional<Reservation> findByIdAndUser(Long id, User user);


    boolean existsByCar(Car car);

    boolean existsByUser(User user);

    @EntityGraph(attributePaths = {"car","user"})
    List<Reservation> findAllBy();
}
