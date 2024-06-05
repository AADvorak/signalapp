package link.signalapp.service.specifications;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import link.signalapp.model.Signal;
import link.signalapp.model.SignalInFolder;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class SignalSpecifications {

    public static Specification<Signal> userIdEqual(int userId) {
        return (root, query, builder) -> builder.equal(root.get("userId"), userId);
    }

    public static Specification<Signal> nameOrDescriptionLike(String search) {
        return (root, query, builder) -> builder.or(
                SpecificationUtils.likeIgnoreCase(builder, root.get("name"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("description"), search)
        );
    }

    public static Specification<Signal> sampleRateIn(List<BigDecimal> sampleRates) {
        return (root, query, builder) -> root.get("sampleRate").in(sampleRates);
    }

    public static Specification<Signal> existsFolderWithIdIn(List<Integer> folderIds) {
        return (root, query, builder) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<SignalInFolder> subRoot = subquery.from(SignalInFolder.class);
            return builder.exists(subquery.select(builder.literal(1)).where(
                    subRoot.get("id").get("folderId").in(folderIds),
                    builder.equal(subRoot.get("id").get("signalId"), root.get("id"))
            ));
        };
    }
}
