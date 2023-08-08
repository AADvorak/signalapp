package link.signalapp.repository;

import link.signalapp.model.UserConfirm;
import link.signalapp.model.UserConfirmPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfirmRepository extends JpaRepository<UserConfirm, UserConfirmPK> {

    UserConfirm findByCode(String code);

}
