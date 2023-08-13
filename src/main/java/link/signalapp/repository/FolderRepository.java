package link.signalapp.repository;

import link.signalapp.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Integer> {

    @Query(value = "select count(1) from folder " +
            "where user_id = :userId", nativeQuery = true)
    int countByUserId(int userId);

    List<Folder> findByUserId(int userId);

    Optional<Folder> findByIdAndUserId(int id, int userId);

}
