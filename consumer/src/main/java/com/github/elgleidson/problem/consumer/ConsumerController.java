package com.github.elgleidson.problem.consumer;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ConsumerController {

  private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

  private static final String PRODUCER_BASE_URL = "http://localhost:8080";
  private static final String PRIMES_URL = "/primes/{number}";

  private final WebClient webClient;

  @Autowired
  public ConsumerController(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(PRODUCER_BASE_URL).build();
  }

  @RequestMapping(method = RequestMethod.HEAD, value = "/primes/{number}")
  //@RequestMapping(method = RequestMethod.HEAD, value = "/primes/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
  // if we remove the "produces" then contract tests will call GET instead of HEAD
  public Mono<ResponseEntity<Object>> head(@PathVariable int number) {
    logger.info("enter HEAD: {}", number);
    return webClient.head()
        .uri(PRIMES_URL, number)
        .exchange()
        .map(ClientResponse::statusCode)
        .map(httpStatus -> httpStatus.is2xxSuccessful() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/primes/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<PrimeResponse>> get(@PathVariable int number) {
    logger.info("enter GET: {}", number);
    return webClient.get()
        .uri(PRIMES_URL, number)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(PrimeResponse.class)
        .map(ResponseEntity::ok);
  }

  public static class PrimeResponse {
    private List<Integer> primes;

    public List<Integer> getPrimes() {
      return primes;
    }

    public void setPrimes(List<Integer> primes) {
      this.primes = primes;
    }
  }
}
