package davor.springframework.spring6restmvc.Controller;

import davor.springframework.spring6restmvc.Repositories.CustomerRepository;
import davor.springframework.spring6restmvc.entities.Customer;
import davor.springframework.spring6restmvc.model.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
