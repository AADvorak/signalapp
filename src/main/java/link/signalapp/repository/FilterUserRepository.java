package link.signalapp.repository;

import link.signalapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FilterUserRepository {
    Page<User> findByFilter(String search, List<Integer> roleIds, Pageable pageable);
}
