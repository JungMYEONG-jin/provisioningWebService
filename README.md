# provisioningWebService
타 부서에서 ios 프로비저닝 갱신 요청이 자주옵니다. 이걸 지원하다보니 업무 집중도가 떨어질 때 가 있었는데요.. 이젠 이 과정을 자동으로 만들고자 해당 프로젝트를 기획하였습니다.

## Diagram

![Diagram](TableDiagram.png)


전체적으로 중심이 되는 곳은 Profile입니다. 프로비저닝을 재발급하는 서비스다 보니 이렇게 설계를 하였습니다.
매번 API 요청을 해서 해당 프로비저닝과 매핑된 디바이스, 인증서를 가져오는건 비효율적이고 시간적 특성상 효율적이지 않다고 생각했습니다.
이와 같은 이유로 Bundle, Device, Certificate table을 구성하고 프로비저닝과 매핑되는 각 테이블의 관계를
ProfileDevice, ProfileCertificate, ProfileBundle로 나타냈습니다.
프로비저닝이 업데이트 되면 업데이트 후 받은 값을 파싱하여 연관된 table을 업데이트 시켜줍니다.
아직 구현할게 남았지만 구현후 프로비저닝 파일을 생성해 자동으로 SVN 서버에 올리는것 까지가 목표입니다.


## Faced Troubles

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



## SVNkit
우선 프로비저닝을 재발급하고 이를 다시 프로비저닝으로 만드는 과정은 성공적으로 마무리 되었다.
프로비저닝 파일은 svn 경로에 관리되기 때문에 이를 어떻게 해야 넘길 수 있을지 고민을 해야 했다.
선택지는 다음 3가지 였다.
1. 서버 경로에 취합하고 한번에 옮길까?
2. 스크립트 짜서 넘기기
3. 자바 코드로 가능한지 찾아보기

우선 1번을 선택하게 되면 전체적인 관리가 힘들것이란 생각이 들었다. 전체 경로에 넘기기 전까지
다른 사람은 최신화된 프로비저닝 파일을 받을 수 없다는점이 문제였다.
2번은 스크립트를 짜서 svn에 올려야 하는데 각 앱 프로비저닝 마다 경로가 존재한다. 하지만 이를 전부 각각
넣어주려면 많이 무리가 갈것이라 생각했다. 심지어 나중에 경로가 변경된다면..흠...
결국 남은 선택지는 3번인데 처음에는 어떻게 넘겨야 하는지 정보가 없었다.
그러다 SVNkit을 사용하면 넘길 수 있다는걸 알게 되었고 이를 적용해 svn에 올리는게 성공했다.

## 진행사항 
>12/16
- ProfileCertificate 생성 완료
- BundleId 있는지? 생성 완료
- ProfileBundleId 생성 완료
>12/22
- 프로비저닝 업데이트 기능 구현 완료
>12/27
- SVN 업로드 성공
- 전체 코드 리팩토링 필요.
