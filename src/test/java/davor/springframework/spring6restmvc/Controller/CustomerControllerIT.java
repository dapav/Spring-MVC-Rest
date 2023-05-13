package davor.springframework.spring6restmvc.Controller;

import davor.springframework.spring6restmvc.Repositories.CustomerRepository;
import davor.springframework.spring6restmvc.entities.Beer;
import davor.springframework.spring6restmvc.entities.Customer;
import davor.springframework.spring6restmvc.mappers.CustomerMapper;
import davor.springframework.spring6restmvc.model.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    void testDeleteByIdNotFound(){
        assertThrows(NotFoundException.class, ()->{
           customerController.deleteCustomerByID(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void deleteById(){
        Customer customer = customerRepository.findAll().get(0);
        ResponseEntity responseEntity = customerController
                .deleteCustomerByID(customer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }
    @Test
    void testUpdateNotFound(){
        assertThrows(NotFoundException.class, ()->{
           customerController.updateCustomerById(UUID.randomUUID(),CustomerDTO.builder().build());
        });
    }
    @Rollback
    @Transactional
    @Test
    void updateExistingCustomer(){
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(customer);
        customerDTO.setId(null);
        customerDTO.setVersion(null);
        final String customerName="Updated";
        customerDTO.setCustomerName(customerName);

        ResponseEntity responseEntity = customerController.
                updateCustomerById(customer.getId(), customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        Customer updatedCustomer = customerRepository.findById(customer.getId())
                .get();
        assertThat(updatedCustomer.getCustomerName()).isEqualTo(customerName);

    }
    @Rollback
    @Transactional
    @Test
    void testNewCustomer(){
        CustomerDTO customerDTO = CustomerDTO.builder()
        .customerName("New Name").build();

        ResponseEntity responseEntity = customerController
                .newCustomer(customerDTO);

        assertThat(responseEntity.getStatusCode()).
                isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        Customer customer = customerRepository.findById(savedUUID).get();
        assertThat(customer).isNotNull();
    }
    @Test
    void testCustomerById(){
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO dto = customerController.getCustomerById(customer.getId());
        assertThat(dto).isNotNull();

    }

    @Test
    void testListCustomer(){
        List<CustomerDTO> dtos = customerController.listCustomer();
        assertThat(dtos.size()).isEqualTo(3);

    }

    @Test
    void testCustomerIdNotFound(){
        assertThrows(NotFoundException.class, ()->{
            customerController.getCustomerById(UUID.randomUUID());
        });

    }

    @Rollback
    @Transactional
    @Test
    void testCustomerEmptyList(){
        customerRepository.deleteAll();
        List<CustomerDTO> dtos = customerController.listCustomer();
        assertThat(dtos.size()).isEqualTo(0);
    }


}
