package net.spofo.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.spofo.auth.dto.response.MemberResponse;
import net.spofo.auth.entity.PublicKey;
import net.spofo.auth.exception.InvalidTokenException;
import net.spofo.auth.repository.PublicKeyRepository;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class PublicKeyService {

    private final MemberService memberService;
    private final PublicKeyRepository publicKeyRepository;
    private final RestTemplate restTemplate;
    private final String KAKAO_PUBLIC_KEY_URL = "https://kauth.kakao.com/.well-known/jwks.json";

    public PublicKey savePublicKey(PublicKey publicKey) {
        return publicKeyRepository.save(publicKey);
    }

    public void deleteAllPublicKey() {
        publicKeyRepository.deleteAllInBatch();
    }

    public List<PublicKey> loadPublicKey() {
        return publicKeyRepository.findAll();
    }

    public void getKakaoPublicKeys() {
        // 카카오 공개키 목록 가져오기
        ResponseEntity<String> response = restTemplate.getForEntity(KAKAO_PUBLIC_KEY_URL,
                String.class);
        String kidJson = response.getBody();
        List<String> publicKeyList = new ArrayList<>();
        List<PublicKey> storedPublicKeyList = loadPublicKey();

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
            e.printStackTrace();
        }
        if (!matchPublicKey(publicKeyList, storedPublicKeyList)) {
            saveNewPublicKey(publicKeyList);
        }
    }

    public boolean matchPublicKey(List<String> publicKeyList, List<PublicKey> storedPublicKeyList) {
        for (int i = 0; i < publicKeyList.size(); i++) {
            for (int j = 0; j < storedPublicKeyList.size(); j++) {
                if (publicKeyList.get(i).equals(storedPublicKeyList.get(j))) {
                    throw new InvalidTokenException("유효하지 않은 토큰입니다.");
                }
            }
        }
        return false;
    }

    public void saveNewPublicKey(List<String> publicKeyList) {
        deleteAllPublicKey();
        publicKeyList.stream()
                .map(PublicKey::new) // 각 요소를 PublicKey 객체로 변환
                .forEach(this::savePublicKey); // 각 PublicKey를 저장
    }

    public MemberResponse verifySignature(String token) {
        List<PublicKey> findPK = loadPublicKey();
        DecodedJWT jwtOrigin = JWT.decode(token);

        for (int i = 0; i < findPK.size(); i++) {
            if (jwtOrigin.getKeyId().equals(findPK.get(i).getPublickey())) {
                // 토큰에서 id(sub) 가져와서 DB에 저장되어 있는지 확인
                String socialId = jwtOrigin.getSubject();
                MemberResponse memberResponse = memberService.findBySocialId(socialId);
                return memberResponse;
            }
        }
        // 다 돌았는데도 일치하는 공개키가 없다면 공개키 목록을 업데이트 하거나, 서명 실패라고 알려주기
        getKakaoPublicKeys();
        verifySignature(token);
        return null;
    }

    // TODO : 토큰 유효성 검사(만료/발급자/앱키 일치하는지 확인)
}