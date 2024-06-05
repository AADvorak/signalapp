package link.signalapp.repository.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class SpecificationUtils {

    public static Predicate likeIgnoreCase(CriteriaBuilder builder, Path<String> path, String value) {
        return builder.like(builder.upper(path), value.toUpperCase());
    }
}
