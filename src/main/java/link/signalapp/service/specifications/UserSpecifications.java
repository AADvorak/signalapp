package link.signalapp.service.specifications;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import link.signalapp.model.User;
import link.signalapp.model.UserInRole;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UserSpecifications {

    public static Specification<User> nameOrEmailLike(String search) {
        return (root, query, builder) -> builder.or(
                SpecificationUtils.likeIgnoreCase(builder, root.get("firstName"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("lastName"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("patronymic"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("email"), search)
        );
    }

    public static Specification<User> existsRoleWithIdIn(List<Integer> roleIds) {
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
