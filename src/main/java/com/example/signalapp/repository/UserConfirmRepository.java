package com.example.signalapp.repository;

import com.example.signalapp.model.UserConfirm;
import com.example.signalapp.model.UserPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfirmRepository extends JpaRepository<UserConfirm, UserPK> {
}
