package link.signalapp.repository.specifications;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import link.signalapp.dto.request.paging.UserFiltersDto;
import link.signalapp.model.User;
import link.signalapp.model.UserInRole;
import link.signalapp.repository.FilterUserRepository;
import link.signalapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpecificationsFilterUserRepositoryImpl implements FilterUserRepository {

    private final UserRepository userRepository;

    @Override
    public Page<User> findByFilters(UserFiltersDto filters, Pageable pageable) {
        return userRepository.findAll(makeSpecification(filters), pageable);
    }

    private Specification<User> makeSpecification(UserFiltersDto filters) {
        String search = filters.getSearchFormatted();
        var roleIds = filters.getRoleIds();
        return Specification.where(search == null ? null : nameOrEmailLike(search))
                .and(roleIds == null ? null : existsRoleWithIdIn(roleIds));
    }

    private Specification<User> nameOrEmailLike(String search) {
        return (root, query, builder) -> builder.or(
                SpecificationUtils.likeIgnoreCase(builder, root.get("firstName"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("lastName"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("patronymic"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("email"), search)
        );
    }

    private Specification<User> existsRoleWithIdIn(List<Integer> roleIds) {
        return (root, query, builder) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<UserInRole> subRoot = subquery.from(UserInRole.class);
            return builder.exists(subquery.select(builder.literal(1)).where(
                    subRoot.get("id").get("roleId").in(roleIds),
                    builder.equal(subRoot.get("id").get("userId"), root.get("id"))
            ));
        };
    }
}
