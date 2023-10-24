package net.spofo.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
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

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final MemberService memberService;
    private final PublicKeyRepository publicKeyRepository;
    private final RestClient restClient;
    private final String issuer = "https://kauth.kakao.com";
    private final String KAKAO_PUBLIC_KEY_URL = "https://kauth.kakao.com/.well-known/jwks.json";
    private final String prefix = "Bearer ";

    @Value("${auth.kakao.clientid}")
    private String appKey;

    public MemberResponse verifyToken(String token) { // 토큰 검증
        if (!isValidToken(token)) {
            throw new InvalidToken("토큰이 유효하지 않습니다.");
        }

        token = token.replace(prefix, "");

        DecodedJWT jwtOrigin = verifyValidation(token); // 토큰 유효성 검증
        if (!matchPublicKey(token)) { // 토큰의 공개키가 유효하지 않다면 DB를 업데이트하거나 실패라고 알려주거나
            getKakaoPublicKeys(); // 예외 발생 없이 잘 돌아오면 정상적인 토큰. (db가 업데이트 된 상태이므로 한 번 더 서명 검증 필요)
            if (!matchPublicKey(token)) {
                throw new InvalidToken("토큰이 유효하지 않습니다.(공개키 불일치)");
            }
        }
        // 토큰 검증 완료!
        String socialId = jwtOrigin.getSubject();
        MemberResponse memberResponse = memberService.findBySocialId(socialId);
        return memberResponse;
    }

    private boolean isValidToken(String token) {
        return token != null && token.startsWith(prefix);
    }

    private boolean matchPublicKey(String token) { // 토큰의 공개키와 비교하여 서명 검증
        List<PublicKeyInfo> storedPublicKey = loadPublicKeys();
        DecodedJWT jwtOrigin = JWT.decode(token);
        for (int i = 0; i < storedPublicKey.size(); i++) {
            if (jwtOrigin.getKeyId().equals(storedPublicKey.get(i).getKid())) {
                verifySignature(token); // 퍼블릭 키 만들어서 검증해야함
                return true; // 토큰의 공개키가 유효함.
            }
        }
        return false;
    }

    private void getKakaoPublicKeys() {
        // 카카오 공개키 목록 가져오기
        ResponseEntity response = restClient.get()
                .uri(KAKAO_PUBLIC_KEY_URL)
                .retrieve()
                .toEntity(String.class);

        String kidJson = response.getBody().toString();
        List<PublicKeyInfo> publicKeyList = new ArrayList<>();
        List<PublicKeyInfo> storedPublicKeyList = loadPublicKeys();

        try {
            // 1. 데이터 파싱
            JSONObject jsonObject = new JSONObject(kidJson);
            JSONArray keysArray = jsonObject.getJSONArray("keys");

            // 2. 파싱한 데이터로 리스트 만들기
            for (int i = 0; i < keysArray.length(); i++) {
                JSONObject keyObject = keysArray.getJSONObject(i);
                String kid = keyObject.getString("kid");
                String modulus = keyObject.getString("n");
                String exponent = keyObject.getString("e");
                PublicKeyInfo publicKeyInfo = PublicKeyInfo.builder()
                        .kid(kid)
                        .modulus(modulus)
                        .exponent(exponent)
                        .build();
                publicKeyList.add(publicKeyInfo);
            }
        } catch (Exception e) { //JSONExecption
            throw new InvalidToken("JSON이 유효하지 않습니다.");
        }

        if (!comparePublicKey(publicKeyList,
                storedPublicKeyList)) { // 만약 불러온 pk와 저장된 pk가 다르다면 공개키가 업데이트 된 것이므로 DB 업데이트
            saveNewPublicKey(publicKeyList);
        }
    }

    private boolean comparePublicKey(List<PublicKeyInfo> publicKeyList,
            List<PublicKeyInfo> storedPublicKeyList) {
        for (int i = 0; i < publicKeyList.size(); i++) {
            for (int j = 0; j < storedPublicKeyList.size(); j++) {
                if (publicKeyList.get(i).getKid().equals(storedPublicKeyList.get(j).getKid())) {
                    throw new InvalidToken("토큰이 유효하지 않습니다.(공개키 불일치)");
                }
            }
        }
        return false;
    }

    private void saveNewPublicKey(List<PublicKeyInfo> publicKeyList) {
        publicKeyRepository.deleteAllInBatch();

        List<PublicKey> publicKeys = publicKeyList.stream()
                .map(info -> PublicKey.from(info.getKid(), info.getModulus(), info.getExponent()))
                .collect(Collectors.toList());

        publicKeyRepository.saveAll(publicKeys);
    }

    private DecodedJWT verifyValidation(String token) {
        DecodedJWT jwtOrigin = JWT.decode(token);

        if (jwtOrigin.getExpiresAt().before(new Date())) {
            throw new InvalidToken("토큰이 만료되었습니다.");
        }
        if (!jwtOrigin.getIssuer().equals(issuer)
                || !jwtOrigin.getAudience().get(0).equals(appKey)) {
            throw new InvalidToken("토큰이 유효하지 않습니다.");
        }
        return jwtOrigin;
    }

    private void verifySignature(String token) {
        PublicKey publicKey = getNE(token);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(publicKey))
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

    private Key getRSAPublicKey(PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodeN = Base64.getUrlDecoder().decode(publicKey.getModulus());
        byte[] decodeE = Base64.getUrlDecoder().decode(publicKey.getExponent());
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(keySpec);
    }

    private PublicKey getNE(String token) {
        DecodedJWT jwt = JWT.decode(token);
        String kid = jwt.getKeyId();
        return publicKeyRepository.findByPublicKey(kid);
    }

    private List<PublicKeyInfo> loadPublicKeys() {
        return publicKeyRepository.findAll()
                .stream()
                .map(publicKey -> new PublicKeyInfo(publicKey.getPublicKey(),
                        publicKey.getModulus(),
                        publicKey.getExponent()))
                .collect(Collectors.toList());
    }

    @Getter
    @Builder
    private static class PublicKeyInfo {

        String kid;
        String modulus;
        String exponent;
    }
}