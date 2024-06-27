package link.signalapp.repository;

import link.signalapp.dto.request.paging.UserFiltersDto;
import link.signalapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterUserRepository {

    Page<User> findByFilters(UserFiltersDto filters, Pageable pageable);
}
