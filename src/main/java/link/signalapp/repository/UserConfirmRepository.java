package link.signalapp.repository;

import link.signalapp.model.UserConfirm;
import link.signalapp.model.UserPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfirmRepository extends JpaRepository<UserConfirm, UserPK> {

    UserConfirm findByCode(String code);

}
