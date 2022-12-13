package mj.provisioning.profile.adapter.out.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
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
    private BooleanExpression nameEq(String name){
        return hasText(name)?profile.name.eq(name):null;
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
        return jpaQueryFactory.select(Projections.fields(ProfileShowDto.class, profile.name,
                        profile.platform,
                        profile.profileType,
                        profile.expirationDate))
                .from(profile)
                .where(nameEq(condition.getName()),
                        profilePlatformEq(condition.getProfilePlatform()),
                        profileTypeEq(condition.getProfileType()))
                .fetch();
    }
}
