# SPOFO
JAVA / Spring Boot 기반 주식 포트폴리오 프로젝트.
<br>
<br>

## :one: 프로젝트 소개
추가하고자 하는 주식의 이름을 조회하여 포트폴리오에 추가할 수 있습니다.
모의/실제 태그를 이용하여 포트폴리오의 용도에 따라 다양한 유형의 포트폴리오를 생성할 수 있습니다.
<br>
<br>

## :two: 전체 구성도
![image](https://github.com/Yejin-Moon/algorithm/assets/74597602/73f9f19b-1a8c-4e12-be54-76bbcee80e00)
- 포트폴리오 서버 : 사용자의 포트폴리오를 관리할 수 있도록 하는 서버
- 인증 서버 : ID token의 유효성을 검증하는 서버
- 주식 서버 : 외부 API로부터 주식 정보를 가져오는 서버
<br>
<br>

## :three: 인증 서버 구성도
<img width="1160" alt="image" src="https://github.com/Yejin-Moon/algorithm/assets/74597602/75259926-103b-4141-845f-f076ee8ff364">
인증 서버는 소셜로그인 후 발급받은 ID token이 유효한지 검증하는 서버입니다.
포트폴리오 서버에서 ID token과 함께 검증을 요청하면, 검증 과정 수행 후 사용자 정보를 반환합니다.
<br>
<br>

## :four: 배포
- GitHub Actions
- Dokcer
- EC2
