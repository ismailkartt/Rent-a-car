package com.myproject.controller;

import com.myproject.dto.CarDTO;
import com.myproject.dto.request.CarRequest;
import com.myproject.dto.response.ResponseMessage;
import com.myproject.dto.response.SfResponse;
import com.myproject.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {

    private final CarService carService;


    public CarController(CarService carService) {
        this.carService = carService;
    }

    // SaveCar
    @PostMapping("/admin/{imageId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> saveCar(
            @PathVariable String imageId,
            @Valid @RequestBody CarRequest carRequest) {
        carService.saveCar(imageId, carRequest);
        SfResponse response = new SfResponse(ResponseMessage.CAR_SAVED_RESPONSE_MESSAGE, true);
        return ResponseEntity.ok(response);
    }

    // getAll
    @GetMapping("/visitors/all")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> allCars = carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

    // getAllWithPage
    @GetMapping("/visitors/pages")
    public ResponseEntity<Page<CarDTO>> getAllCarsWithPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String prop,
            @RequestParam(value = "direction",
                    required = false,
                    defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));

        Page<CarDTO> pageDTO = carService.findAllWithPage(pageable);
        return ResponseEntity.ok(pageDTO);
    }

    // getCarById
    @GetMapping("/visitors/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id){
        CarDTO carDTO = carService.findById(id);
        return ResponseEntity.ok(carDTO);
    }

    // Update Car With ImageId
    @PutMapping("/admin/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> updateCar(@RequestParam("id") Long id,
                                                @RequestParam("imageId") String imageId,
                                                @Valid @RequestBody CarRequest carRequest){
        carService.updateCar(id,imageId,carRequest);
        SfResponse response = new SfResponse(ResponseMessage.CAR_UPDATE_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }

    // Delete Mapping
    @DeleteMapping("/admin/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteCar(@PathVariable Long id){
        carService.removeById(id);
        SfResponse response = new SfResponse(ResponseMessage.CAR_DELETE_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }


}
