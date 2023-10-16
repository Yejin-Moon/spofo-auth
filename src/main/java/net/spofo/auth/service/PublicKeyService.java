package net.spofo.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import net.spofo.auth.dto.response.MemberResponse;
import net.spofo.auth.entity.PublicKey;
import net.spofo.auth.repository.PublicKeyRepository;
import net.spofo.auth.exception.InvalidToken;

@RequiredArgsConstructor
@Service
public class PublicKeyService {

    private final MemberService memberService;
    private final PublicKeyRepository publicKeyRepository;
    private final RestClient restClient;
    private final String issuer = "https://kauth.kakao.com";
    private final String KAKAO_PUBLIC_KEY_URL = "https://kauth.kakao.com/.well-known/jwks.json";

    private final String n = "q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw";
    private final String e = "AQAB";

    @Value("${auth.kakao.clientid}")
    private String appKey;

    public MemberResponse verifyToken(String token) { // 토큰 검증
        DecodedJWT jwtOrigin = verifyValidation(token);

        if (verifySignature(token) == false) { // 토큰의 공개키가 유효하지 않다면 DB를 업데이트하거나 실패라고 알려주거나
            getKakaoPublicKeys(); // 예외 발생 없이 잘 돌아오면 정상적인 토큰. (db가 업데이트 된 상태이므로 한 번 더 서명 검증 필요)
            if (verifySignature(token) == false) {
                throw new InvalidToken("토큰이 유효하지 않습니다.(공개키 불일치)");
            }
        }
        // 토큰 검증 완료!
        String socialId = jwtOrigin.getSubject();
        MemberResponse memberResponse = memberService.findBySocialId(socialId);
        return memberResponse;
    }

    public boolean verifySignature(String token) { // 토큰의 공개키와 비교하여 서명 검증
        List<PublicKey> storedPublicKey = publicKeyRepository.findAll();
        DecodedJWT jwtOrigin = JWT.decode(token);
        for (int i = 0; i < storedPublicKey.size(); i++) {
            if (jwtOrigin.getKeyId().equals(storedPublicKey.get(i).getPublickey())) {
                getOIDCTokenJws(token);
                return true; // 토큰의 공개키가 유효함.
            }
        }
        return false;
    }

    public void getKakaoPublicKeys() {
        // 카카오 공개키 목록 가져오기
        ResponseEntity response = restClient.get()
                .uri(KAKAO_PUBLIC_KEY_URL)
                .retrieve()
                .toEntity(String.class);

        String kidJson = response.getBody().toString();
        List<String> publicKeyList = new ArrayList<>();
        List<PublicKey> storedPublicKeyList = publicKeyRepository.findAll();

        try {
            // 1. 데이터 파싱
            JSONObject jsonObject = new JSONObject(kidJson);
            JSONArray keysArray = jsonObject.getJSONArray("keys");

            // 2. 파싱한 데이터로 리스트 만들기
            for (int i = 0; i < keysArray.length(); i++) {
                JSONObject keyObject = keysArray.getJSONObject(i);
                String kid = keyObject.getString("kid");
                publicKeyList.add(kid);
            }
        } catch (Exception e) { //JSONExecption
            throw new InvalidToken("JSON이 유효하지 않습니다.");
        }

        if (!matchPublicKey(publicKeyList,
                storedPublicKeyList)) { // 만약 불러온 pk와 저장된 pk가 다르다면 공개키가 업데이트 된 것이므로 DB 업데이트
            saveNewPublicKey(publicKeyList);
        }
    }

    public boolean matchPublicKey(List<String> publicKeyList, List<PublicKey> storedPublicKeyList) {
        for (int i = 0; i < publicKeyList.size(); i++) {
            for (int j = 0; j < storedPublicKeyList.size(); j++) {
                if (publicKeyList.get(i).equals(storedPublicKeyList.get(j).getPublickey())) {
                    throw new InvalidToken("토큰이 유효하지 않습니다.(공개키 불일치)");
                }
            }
        }
        return false;
    }

    public void saveNewPublicKey(List<String> publicKeyList) {
        publicKeyRepository.deleteAllInBatch();
        publicKeyList.stream()
                .map(PublicKey::new) // 각 요소를 PublicKey 객체로 변환
                .forEach(this::savePublicKey); // 각 PublicKey를 저장
    }

    public DecodedJWT verifyValidation(String token) {
        DecodedJWT jwtOrigin = JWT.decode(token);

        if (jwtOrigin.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
            throw new InvalidToken("토큰이 만료되었습니다.");
        }
        if (!jwtOrigin.getIssuer().equals(issuer)
                || !jwtOrigin.getAudience().get(0).equals(appKey)) {
            throw new InvalidToken("토큰이 유효하지 않습니다.");
        }
        return jwtOrigin;
    }

    public Jws<Claims> getOIDCTokenJws(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (SignatureException ex) {
            throw new InvalidToken("서명이 유효하지 않습니다.");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException();
        } catch (InvalidKeySpecException ex) {
            throw new RuntimeException();
        }
    }

    private Key getRSAPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodeN = Base64.getUrlDecoder().decode(n);
        byte[] decodeE = Base64.getUrlDecoder().decode(e);
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(keySpec);
    }

    public PublicKey savePublicKey(PublicKey publicKey) {
        return publicKeyRepository.save(publicKey);
    }
}