package mj.provisioning.profile.adapter.out.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfilePlatform;
import mj.provisioning.profile.domain.ProfileType;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mj.provisioning.profile.domain.QProfile.profile;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class CustomProfileRepositoryImpl implements CustomProfileRepository{

    private final JPAQueryFactory jpaQueryFactory;
    /**
     *  String name;
     *  ProfilePlatform profilePlatform;
     *  ProfileType profileType;
     */
    private BooleanExpression nameLike(String name){
        return hasText(name)?profile.name.like("%"+name+"%"):null;
    }

    private BooleanExpression profilePlatformEq(ProfilePlatform profilePlatform){
        return profilePlatform!=null?profile.platform.eq(profilePlatform):null;
    }

    private BooleanExpression profileTypeEq(ProfileType profileType){
        return profileType!=null?profile.profileType.eq(profileType):null;
    }

    /**
     * 애플 검색 조건 구현
     * @param condition
     * @return
     */
    @Override
    public List<ProfileShowDto> getSearchByCondition(ProfileSearchCondition condition) {

        return jpaQueryFactory.select(Projections.fields(ProfileShowDto.class,
                        profile.id.as("key"),
                        profile.profileId,
                        profile.name,
                        profile.platform,
                        profile.profileType,
                        profile.expirationDate))
                .from(profile)
                .where(nameLike(condition.getName()),
                        profilePlatformEq(ProfilePlatform.get(condition.getProfilePlatform())),
                        profileTypeEq(ProfileType.get(condition.getProfileType())))
                .fetch();
    }

    /**
     * xToMany 에는 Fetch join 여러번이 불가능하다.
     * batch_size 적용하고
     * 가장 많이 연관된 자식을 fetch join 해주면 어느정도 해소가 가능하다
     * 디바이스가 가장 많으므로 디바이스로 가자..
     * 번들은 xToOne이라 가능하다.
     * @param profileId
     * @return
     */
    @Override
    public Profile findByFetchJoin(String profileId) {
        return jpaQueryFactory.select(profile)
                .from(profile)
                .where(profile.profileId.eq(profileId))
                .leftJoin(profile.deviceList)
                .fetchJoin()
                .leftJoin(profile.profileBundle)
                .fetchJoin()
                .fetchOne();
    }

    @Override
    public List<Profile> findByNameLike(String name) {
        return jpaQueryFactory.selectFrom(profile)
                .where(nameLike(name))
                .leftJoin(profile.deviceList)
                .fetchJoin()
                .leftJoin(profile.profileBundle)
                .fetchJoin()
                .fetch();
    }

}
