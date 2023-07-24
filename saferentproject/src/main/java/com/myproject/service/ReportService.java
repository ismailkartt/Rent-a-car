package com.myproject.service;

import com.myproject.domain.Car;
import com.myproject.domain.Reservation;
import com.myproject.domain.User;
import com.myproject.exception.message.ErrorMessage;
import com.myproject.report.ExcelReporter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    private final UserService userService;
    private final CarService carService;
    private final ReservationService reservationService;


    public ReportService(UserService userService, CarService carService, ReservationService reservationService) {
        this.userService = userService;
        this.carService = carService;
        this.reservationService = reservationService;
    }


    public ByteArrayInputStream getUserReport()  {

        List<User> users =   userService.getUsers();

        try {
            return ExcelReporter.getUserExcelReport(users);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }

    public ByteArrayInputStream getCarReport() {

        List<Car> cars = carService.getAllCar();

        try {
            return ExcelReporter.getCarExcelReport(cars);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }

    }

    public ByteArrayInputStream getReservationReport() {
        List<Reservation> reservations = reservationService.getAll();

        try {
            return ExcelReporter.getReservationExcelReport(reservations);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }





}
