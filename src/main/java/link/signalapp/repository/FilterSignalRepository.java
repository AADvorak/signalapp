package link.signalapp.repository;

import link.signalapp.dto.request.paging.SignalFiltersDto;
import link.signalapp.model.Signal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterSignalRepository {

    Page<Signal> findByUserIdAndFilters(int userId, SignalFiltersDto filters, Pageable pageable);
}
