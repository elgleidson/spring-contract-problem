package com.github.elgleidson.problem.producer;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ProducerController {

  private static final Logger logger = LoggerFactory.getLogger(ProducerController.class);

  private static final List<Integer> PRIME_NUMBERS = List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101);

  @RequestMapping(method = RequestMethod.HEAD, value = "/primes/{number}")
  public Mono<ResponseEntity<Object>> head(@PathVariable int number) {
    logger.info("enter HEAD: {}", number);
    return Mono.just(PRIME_NUMBERS.contains(number) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/primes/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<PrimeResponse>> get(@PathVariable int number) {
    logger.info("enter GET: {}", number);
    return Mono.just(PRIME_NUMBERS.contains(number)
        ? ResponseEntity.ok(new PrimeResponse(PRIME_NUMBERS.subList(0, PRIME_NUMBERS.indexOf(number))))
        : ResponseEntity.notFound().build()
    );
  }

  public static class PrimeResponse {
    private final List<Integer> primes;

    public PrimeResponse(List<Integer> primes) {
      this.primes = primes;
    }

    public List<Integer> getPrimes() {
      return primes;
    }
  }
}
