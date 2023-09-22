package net.spofo.auth.service;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private RestClient restClient;

    public String getStock() {
        restClient = RestClient.create();
        return restClient.get()
                .uri("https://www.stock.spofo.net/test/callStock:8080")
                .retrieve()
                .body(String.class);
    }

    public String getPortfolio() {
        restClient = RestClient.create();
        return restClient.get()
                .uri("https://www.portfolio.spofo.net/test/callPortfolio:8080")
                .retrieve()
                .body(String.class);
    }
}
