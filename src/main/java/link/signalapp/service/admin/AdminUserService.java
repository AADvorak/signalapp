package link.signalapp.service.admin;

import link.signalapp.dto.request.UserFilterDto;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.error.exception.SignalAppNotFoundException;
import link.signalapp.file.FileManager;
import link.signalapp.mapper.UserMapper;
import link.signalapp.model.Role;
import link.signalapp.model.User;
import link.signalapp.model.UserToken;
import link.signalapp.properties.ApplicationProperties;
import link.signalapp.repository.RoleRepository;
import link.signalapp.repository.SignalRepository;
import link.signalapp.repository.UserRepository;
import link.signalapp.repository.UserTokenRepository;
import link.signalapp.service.utils.FilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final String DEFAULT_SORT_FIELD = "createTime";
    private static final List<String> AVAILABLE_SORT_FIELDS = List.of("firstName", "lastName",
            "patronymic", "email", "createTime");

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final RoleRepository roleRepository;
    private final SignalRepository signalRepository;
    private final FileManager fileManager;
    private final ApplicationProperties applicationProperties;
    private final FilterUtils filterUtils = new FilterUtils(AVAILABLE_SORT_FIELDS, DEFAULT_SORT_FIELD);

    public ResponseWithTotalCounts<UserDtoResponse> filter(UserFilterDto filter) {
        Pageable pageable = filterUtils.getPageable(filter, applicationProperties.getMaxPageSize());
        String search = filterUtils.getSearch(filter);
        Page<User> users = userRepository.findByFilter(search,
                filterUtils.listWithDefaultValue(filter.getRoleIds(), 0), pageable);
        ResponseWithTotalCounts<UserDtoResponse> responseWithTotalCounts
                = new ResponseWithTotalCounts<UserDtoResponse>()
                .setData(users.stream().map(UserMapper.INSTANCE::userToDto).toList())
                .setPages(users.getTotalPages())
                .setElements(users.getTotalElements());
        responseWithTotalCounts.getData().forEach(this::setLastActionTime);
        responseWithTotalCounts.getData().forEach(this::setStoredSignalsNumber);
        return responseWithTotalCounts;
    }

    public void setRole(int userId, int roleId) {
        User user = userRepository.findById(userId).orElseThrow(SignalAppNotFoundException::new);
        Role role = roleRepository.findById(roleId).orElseThrow(SignalAppNotFoundException::new);
        user.setRole(role);
        userRepository.save(user);
    }

    public void deleteRole(int userId, int roleId) {
        User user = userRepository.findById(userId).orElseThrow(SignalAppNotFoundException::new);
        if (user.getRole() == null || user.getRole().getId() != roleId) {
            throw new SignalAppNotFoundException();
        }
        user.setRole(null);
        userRepository.save(user);
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
}
