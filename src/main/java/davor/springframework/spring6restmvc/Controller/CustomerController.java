package davor.springframework.spring6restmvc.Controller;

import davor.springframework.spring6restmvc.model.Customer;
import davor.springframework.spring6restmvc.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET)
    private List<Customer> listCustomer(){
        return customerService.listCustomers();
    }

    @RequestMapping(value = "{customerId}", method = RequestMethod.GET)
    public Customer getCustomerById(@PathVariable("customerId") UUID customerId){

        log.debug("Get customer by id - in controller");
        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    public ResponseEntity newCustomer(@RequestBody Customer customer){
        Customer savedCustomer = customerService.saveNewCustomer(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location",
                "/api/v1/customer/"+ savedCustomer.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);

    }
}
