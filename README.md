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


## 진행사항 
>12/16
- ProfileCertificate 생성 완료
- BundleId 있는지? 생성 완료
- ProfileBundleId 생성 완료
>12/22
- 프로비저닝 업데이트 기능 구현 완료