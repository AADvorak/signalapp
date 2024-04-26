package link.signalapp.service.admin;

import link.signalapp.dto.request.UserFilterDto;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.mapper.UserMapper;
import link.signalapp.model.User;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserRepository;
import link.signalapp.repository.UserTokenRepository;
import link.signalapp.service.utils.FilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final String DEFAULT_SORT_FIELD = "createTime";
    private static final List<String> AVAILABLE_SORT_FIELDS = List.of("first_name", "last_name",
            "patronymic", "email", "createTime");

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final FilterUtils filterUtils = new FilterUtils(AVAILABLE_SORT_FIELDS, DEFAULT_SORT_FIELD);

    public ResponseWithTotalCounts<UserDtoResponse> filter(UserFilterDto filter) {
        Pageable pageable = filterUtils.getPageable(filter, 25);
        String search = filterUtils.getSearch(filter);
        Page<User> users = userRepository.findByFilter(search, pageable);
        ResponseWithTotalCounts<UserDtoResponse> responseWithTotalCounts
                = new ResponseWithTotalCounts<UserDtoResponse>()
                .setData(users.stream().map(UserMapper.INSTANCE::userToDto).toList())
                .setPages(users.getTotalPages())
                .setElements(users.getTotalElements());
        responseWithTotalCounts.getData().forEach(this::setLastActionTime);
        return responseWithTotalCounts;
    }

    private void setLastActionTime(UserDtoResponse response) {
        UserToken userToken = userTokenRepository.findByUserId(response.getId());
        if (userToken != null) {
            response.setLastActionTime(userToken.getLastActionTime());
        }
    }
}
