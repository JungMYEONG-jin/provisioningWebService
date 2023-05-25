# 개요
- 프로젝트명: provisioningWebService
- 개발인원: 2명(백엔드 1, 프론트 1)
- 개발기간: 2022.12.09 ~ 2022.12.30
- 주요 기능: 프로비저닝 리스트 조회, 프로비저닝 재발급, 디바이스 등록
- 개발 언어: Java 8, React
- 환경: SpringBoot 2.7.6, gradle 7.5.1, Spring Data JPA, QueryDSL
- DB: MySQL
- 형상관리: Git
- CI/CD: Jenkins
- 참고문서: https://developer.apple.com/documentation/appstoreconnectapi
- 소개: ios 프로비저닝 갱신 요청이 자주옵니다. 이걸 지원하다보니 업무 집중도가 상당히 떨어지는데요... 
- 또한 인증서가 바뀔 경우... 모든 프로비저닝 파일을 업데이트해야합니다...
- 이젠 이 과정을 자동으로 만들고자 해당 프로젝트를 기획하였습니다.

# 요구사항
1. 프로비저닝 조회 
- 유저는 현재 존재하는 프로비저닝 리스트를 조회 할 수 있어야합니다.
- 타입으로 조회가 가능해야 합니다.
- 이름으로 조회가 가능해야 합니다.
2. 프로비저닝 재발급
- 유저는 프로비저닝 재발급이 가능해야 합니다.
- 프로비저닝 이름을 수정할 수 있어야 합니다.
- 프로비저닝에 등록할 디바이스를 선택할 수 있어야 합니다.
- 프로비저닝에 사용할 인증서를 선택할 수 있어야 합니다.
- 프로비저닝에 사용할 번들 ID를 선택할 수 있어야 합니다.
- SVN에 저장될 경로를 고를 수 있어야 합니다.
3. 디바이스 등록
- 매년 디바이스를 제거하고 새로 추가하는 과정이 필요합니다.
- 그런데 수기 등록은 귀찮습니다.
- 어느 세월에...
- json 으로 전달 받아 등록 요청하면 편리합니다..

# DB 구조

![Diagram](TableDiagram.png)
전체적으로 중심이 되는 곳은 Profile table 입니다. 프로비저닝이 주 관심 도메인이기 때문입니다...
사실 애플 API를 여러번 이용하면 DB를 구축하지 않아도 됐습니다.
하지만 매번 API 요청을 해서 모든 프로비저닝 정보를 가져오고, 수정하고 싶은 프로비저닝과 매핑된 디바이스, 인증서를 가져오는건 시간적 특성상 비효율적이라 생각했습니다.
(사용자에게 빠른 화면을 제공해야 하는데 API 통신상 최악의 경우 1~3초가 걸리고 데이터 가공 시간까지 포함하면 많이 느려질거라 생각했습니다...)
이와 같은 이유로 DB를 구축하고 프로비저닝과 매핑되는 각 테이블의 관계를 나타냈습니다.
프로비저닝이 업데이트 되면 업데이트 후 Response 값을 파싱하여 연관된 테이블을 업데이트 시켜줍니다.
그리고 새로 발급된 프로비저닝 컨텍스트를 디코딩후 SVN 경로에 올려 프로비저닝 파일을 관리합니다.
현재 요청 -> 프로비저닝 발급 프로세스에서 재발급을 원하는 유저가 직접 재발급후 기존 SVN 경로에서 다운로드가 가능하기 때문에 업무 효율성이 상당히 증진되었습니다.(물론 저만 쓰기 때문에 맞는지는?ㅋ.ㅋ)

# API 
1. 프로비저닝 리스트 조회: 현재 발급된 프로비저닝 리스트를 조회 할 수 있습니다.
>curl --location --request GET '${server}/apple/profile/list'
2. 프로비저닝 수정 페이지: 프로비저닝 수정 페이지에 사용될 데이터를 제공합니다.
>curl --location --request GET '${server}/resources/profiles/edit/${프로비저닝ID}'
3. 프로비저닝 수정 요청: 프로비저닝 수정 요청을 하여 재발급을 진행합니다.
>curl --location --request POST '${server}/resources/profiles/edit/${프로비저닝ID}' \
--header 'Content-Type: application/json' \


# 와이어프레임
1. 프로비저닝 조회 페이지
![Main](main.png)
2. 타입 검색
![Type](typeSearch.png)
3. 이름 검색
![Type](nameSearch.png)
4. 재발급
![Reissue](editMain.png)


# Trouble Shooting

### JPA 연관관계 같이 삭제
1. orphanRemoval = true 연관관계가 끊어지면 같이 삭제한다. 다른 엔터티에서 해당 엔터티를 참조하면 사용 못함.
2. CascadeType.REMOVE 해당 엔터티를 삭제할때 연관된 엔터티를 갖는 애들을 삭제하는것. 다른 엔터티에서 참조해도 사용 가능하다.

### Spring Security 없이 CORS 전역 설정 안먹히는 이유
1. allowedOrigins cannot be * if allowCredentials is true. This is documented at Mozilla.org
내가 *으로 설정해도 credential을 false로 해야 먹힘.

### update 후 정보 변경이 안된 현상 발견했음
Transaction 종료가 제대로 안되서 발생한거 같음.
레벨 설정을 통해 해결완료.

### Delete 단건으로 삭제되는 현상
>@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("DELETE FROM ProfileDevice pd WHERE pd.profile = :profile")

Modifying 영속성 문제 해결을 위해 clearAutomatically, flushAutomatically true 필요함!

### Bulk Delete not support Cascade Option
JPA는 bulk 삭제시 cascade를 지원하지 않는다.

### SVNkit
우선 프로비저닝을 재발급하고 이를 다시 프로비저닝으로 만드는 과정은 성공적으로 마무리 되었다.
프로비저닝 파일은 svn 경로에 관리되기 때문에 이를 어떻게 해야 넘길 수 있을지 고민을 해야 했다.
선택지는 다음 3가지 였다.
1. 서버 경로에 취합하고 한번에 옮길까?
2. 스크립트 짜서 넘기기
3. 자바 코드로 가능한지 찾아보기

우선 1번을 선택하게 되면 전체적인 관리가 힘들것이란 생각이 들었다. 전체 경로에 넘기기 전까지
다른 사람은 최신화된 프로비저닝 파일을 받을 수 없다는점이 문제였다.
2번은 스크립트를 짜서 svn에 올려야 하는데 각 앱 프로비저닝 마다 경로가 존재한다. 하지만 이를 전부 각각
넣어주려면 많이 무리가 갈것이라 생각했다. 심지어 나중에 경로가 변경된다면...흠...
결국 남은 선택지는 3번이라 3번으로 했다..

# 진행사항 
>2022/12/16
- ProfileCertificate 생성 완료
- BundleId 있는지? 생성 완료
- ProfileBundleId 생성 완료
>2022/12/22
- 프로비저닝 업데이트 기능 구현 완료
>2022/12/27
- SVN 업로드 성공
- 전체 코드 리팩토링 필요.
>2023/03/09
- 디바이스 등록 기능 추가.
- 전체 프로비저닝 재발급이 필요할때 매우 유용.