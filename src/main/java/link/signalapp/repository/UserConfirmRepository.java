package link.signalapp.repository;

import link.signalapp.model.UserConfirm;
import link.signalapp.model.UserConfirmId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfirmRepository extends JpaRepository<UserConfirm, UserConfirmId> {

    UserConfirm findByCode(String code);

}
