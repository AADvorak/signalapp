package link.signalapp.repository.specifications;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import link.signalapp.dto.request.paging.SignalFiltersDto;
import link.signalapp.model.Signal;
import link.signalapp.model.SignalInFolder;
import link.signalapp.repository.FilterSignalRepository;
import link.signalapp.repository.SignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpecificationsFilterSignalRepositoryImpl implements FilterSignalRepository {

    private final SignalRepository signalRepository;

    @Override
    public Page<Signal> findByUserIdAndFilters(int userId, SignalFiltersDto filters, Pageable pageable) {
        return signalRepository.findAll(makeSpecification(userId, filters), pageable);
    }

    private Specification<Signal> makeSpecification(int userId, SignalFiltersDto filters) {
        String search = filters.getSearchFormatted();
        var sampleRates = filters.getSampleRates();
        var folderIds = filters.getFolderIds();
        return Specification.where(userIdEqual(userId))
                .and(search == null ? null : nameOrDescriptionLike(search))
                .and(sampleRates == null ? null : sampleRateIn(sampleRates))
                .and(folderIds == null ? null : existsFolderWithIdIn(folderIds));
    }

    private Specification<Signal> userIdEqual(int userId) {
        return (root, query, builder) -> builder.equal(root.get("userId"), userId);
    }

    private Specification<Signal> nameOrDescriptionLike(String search) {
        return (root, query, builder) -> builder.or(
                SpecificationUtils.likeIgnoreCase(builder, root.get("name"), search),
                SpecificationUtils.likeIgnoreCase(builder, root.get("description"), search)
        );
    }

    private Specification<Signal> sampleRateIn(List<BigDecimal> sampleRates) {
        return (root, query, builder) -> root.get("sampleRate").in(sampleRates);
    }

    private Specification<Signal> existsFolderWithIdIn(List<Integer> folderIds) {
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
