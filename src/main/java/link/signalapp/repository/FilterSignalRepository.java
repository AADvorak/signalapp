package link.signalapp.repository;

import link.signalapp.model.Signal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface FilterSignalRepository {

    Page<Signal> findByUserIdAndFilter(
            int userId,
            String search,
            List<BigDecimal> sampleRates,
            List<Integer> folderIds,
            Pageable pageable
    );
}
