package link.signalapp.service.admin;

import link.signalapp.dto.request.paging.UsersPageDtoRequest;
import link.signalapp.dto.response.PageDtoResponse;
import link.signalapp.dto.response.RoleDtoResponse;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.error.exception.SignalAppNotFoundException;
import link.signalapp.file.FileManager;
import link.signalapp.mapper.RoleMapper;
import link.signalapp.mapper.UserMapper;
import link.signalapp.model.Role;
import link.signalapp.model.User;
import link.signalapp.model.UserToken;
import link.signalapp.properties.ApplicationProperties;
import link.signalapp.repository.*;
import link.signalapp.service.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final String DEFAULT_SORT_FIELD = "createTime";
    private static final List<String> AVAILABLE_SORT_FIELDS = List.of("firstName", "lastName",
            "patronymic", "email", "createTime");

    private final UserRepository userRepository;
    private final FilterUserRepository filterUserRepository;
    private final UserTokenRepository userTokenRepository;
    private final RoleRepository roleRepository;
    private final SignalRepository signalRepository;
    private final FileManager fileManager;
    private final ApplicationProperties applicationProperties;
    private final PagingUtils pagingUtils = new PagingUtils(AVAILABLE_SORT_FIELDS, DEFAULT_SORT_FIELD);

    public PageDtoResponse<UserDtoResponse> getPage(UsersPageDtoRequest request) {
        Pageable pageable = pagingUtils.getPageable(request, applicationProperties.getMaxPageSize());
        Page<User> users = filterUserRepository.findByFilters(request.getFilters(), pageable);
        PageDtoResponse<UserDtoResponse> pageDtoResponse
                = new PageDtoResponse<UserDtoResponse>()
                .setData(users.stream().map(UserMapper.INSTANCE::userToDto).toList())
                .setPages(users.getTotalPages())
                .setElements(users.getTotalElements());
        pageDtoResponse.getData().forEach(this::setLastActionTime);
        pageDtoResponse.getData().forEach(this::setStoredSignalsNumber);
        return pageDtoResponse;
    }

    public List<RoleDtoResponse> setRole(int userId, int roleId) {
        User user = userRepository.findById(userId).orElseThrow(SignalAppNotFoundException::new);
        Set<Role> roles = user.getRoles();
        if (!checkRolesContainsRoleWithId(roleId, roles)) {
            Role role = roleRepository.findById(roleId).orElseThrow(SignalAppNotFoundException::new);
            roles.add(role);
            userRepository.save(user);
        }
        return roles.stream().map(RoleMapper.INSTANCE::roleToDto).toList();
    }

    public List<RoleDtoResponse> deleteRole(int userId, int roleId) {
        User user = userRepository.findById(userId).orElseThrow(SignalAppNotFoundException::new);
        Set<Role> roles = user.getRoles();
        if (checkRolesContainsRoleWithId(roleId, roles)) {
            user.setRoles(roles.stream().filter(role -> role.getId() != roleId).collect(Collectors.toSet()));
            userRepository.save(user);
        }
        return user.getRoles().stream().map(RoleMapper.INSTANCE::roleToDto).toList();
    }

    @Transactional
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(SignalAppNotFoundException::new);
        userRepository.delete(user);
        fileManager.deleteAllUserData(userId);
    }

    private void setLastActionTime(UserDtoResponse response) {
        UserToken userToken = userTokenRepository.findLastByUserId(response.getId());
        if (userToken != null) {
            response.setLastActionTime(userToken.getLastActionTime());
        }
    }

    private void setStoredSignalsNumber(UserDtoResponse response) {
        response.setStoredSignalsNumber(signalRepository.countByUserId(response.getId()));
    }

    private boolean checkRolesContainsRoleWithId(int roleId, Set<Role> roles) {
        return roles.stream().anyMatch(role -> role.getId() == roleId);
    }
}
