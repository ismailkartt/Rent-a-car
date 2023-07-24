package com.myproject.service;

import com.myproject.domain.Car;
import com.myproject.domain.ImageFile;
import com.myproject.dto.CarDTO;
import com.myproject.dto.request.CarRequest;
import com.myproject.exception.BadRequestException;
import com.myproject.exception.ConflictException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.exception.message.ErrorMessage;
import com.myproject.mapper.CarMapper;
import com.myproject.repository.CarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final ImageFileService imageFileService;
    private final CarMapper carMapper;
    private final ReservationService reservationService;

    public CarService(CarRepository carRepository, ImageFileService imageFileService, CarMapper carMapper, ReservationService reservationService) {
        this.carRepository = carRepository;
        this.imageFileService = imageFileService;
        this.carMapper = carMapper;
        this.reservationService = reservationService;
    }


    public void saveCar(String imageId, CarRequest carRequest) {
        // image id Repoda var mı ?
        ImageFile imageFile = imageFileService.findImageById(imageId);
        //
        Integer usedCarCount = carRepository.findCarCountByImageId(imageFile.getId());
        if (usedCarCount>0){
           throw new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
        }
        // mapper islemi
        Car car = carMapper.carRequestToCar(carRequest);
        // Image bilgisini Car'a ekliyoruz
        Set<ImageFile> imFiles = new HashSet<>();
        imFiles.add(imageFile);
        car.setImage(imFiles);
        carRepository.save(car);

    }

    public List<CarDTO> getAllCars() {
        List<Car> carList = carRepository.findAll();
        return carMapper.map(carList);

    }


    public Page<CarDTO> findAllWithPage(Pageable pageable) {
        Page<Car> carPage = carRepository.findAll(pageable);
        return carPage.map(car->carMapper.carToCarDTO(car));
    }

    public CarDTO findById(Long id) {
        Car car = getCar(id);
        return carMapper.carToCarDTO(car);

    }

    // yardımcı method
    private Car getCar(Long id){
        Car car = carRepository.findCarById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION,id)));
        return car;
    }

    public void updateCar(Long id, String imageId, CarRequest carRequest) {
        Car car = getCar(id);

        // !!! builtIn ???
        if(car.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        // !!! verilen image daha önce başka araç içni kullanılmış mı ???
        ImageFile imageFile =  imageFileService.findImageById(imageId);

        List<Car> carList = carRepository.findCarsByImageId(imageFile.getId());
        for (Car c : carList) {
            // Long --> long
            if(car.getId().longValue()!= c.getId().longValue()){
                throw  new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
            }
        }
        car.setAge(carRequest.getAge());
        car.setAirConditioning(carRequest.getAirConditioning());
        car.setBuiltIn(carRequest.getBuiltIn());
        car.setDoors(carRequest.getDoors());
        car.setFuelType(carRequest.getFuelType());
        car.setLuggage(carRequest.getLuggage());
        car.setModel(carRequest.getModel());
        car.setPricePerHour(carRequest.getPricePerHour());
        car.setSeats(carRequest.getSeats());
        car.setTransmission(carRequest.getTransmission());

        car.getImage().add(imageFile);

        carRepository.save(car);


    }

    public void removeById(Long id) {
        Car car = getCar(id);

        //!!!built-in
        if(car.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        // rezervasyon kontrolu
        boolean exists = reservationService.existsByCar(car);
        if (exists){
            throw new BadRequestException(ErrorMessage.CAR_USED_BY_RESERVATION_MESSAGE);
        }

        carRepository.delete(car);

    }

    public Car getCarById(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION,carId)));
        return car;
    }

    public List<Car> getAllCar() {
        return carRepository.getAllBy();
    }
}



