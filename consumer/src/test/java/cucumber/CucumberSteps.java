package cucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.elgleidson.problem.consumer.ConsumerApplication;
import com.github.elgleidson.problem.consumer.ConsumerController.PrimeResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConsumerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(stubsMode = StubsMode.LOCAL, ids = "com.github.elgleidson:spring-contract-problem-producer:+:stubs:8080")
public class CucumberSteps {

  @Autowired
  private TestRestTemplate dao;

  private HttpStatus httpStatus;
  private PrimeResponse responseBody;

  private int number;

  @Given("the number {int}")
  public void givenANumber(int number) {
    this.number = number;
  }

  @When("I hit HEAD")
  public void whenIHitHead() {
    ResponseEntity<Object> responseEntity = dao.exchange("/primes/{number}", HttpMethod.HEAD, HttpEntity.EMPTY, Object.class, number);
    httpStatus = responseEntity.getStatusCode();
  }

  @When("I hit GET")
  public void whenIHitGet() {
    ResponseEntity<PrimeResponse> responseEntity = dao.exchange("/primes/{number}", HttpMethod.GET, HttpEntity.EMPTY, PrimeResponse.class, number);
    httpStatus = responseEntity.getStatusCode();
    responseBody = responseEntity.getBody();
  }

  @Then("the API returns status {int}")
  public void thenApiReturnsStatus(int status) {
    assertEquals(HttpStatus.valueOf(status), httpStatus);
  }

  @Then("the API returns body")
  public void theAPIReturnsBody(List<Integer> primes) {
    assertEquals(primes, responseBody.getPrimes());
  }
}
