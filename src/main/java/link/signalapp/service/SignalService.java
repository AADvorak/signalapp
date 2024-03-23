package link.signalapp.service;

import link.signalapp.audio.AudioSampleReader;
import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.request.SignalFilterDto;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.SignalDtoResponse;
import link.signalapp.error.code.SignalAppErrorCode;
import link.signalapp.error.exception.SignalAppConflictException;
import link.signalapp.error.exception.SignalAppException;
import link.signalapp.error.exception.SignalAppNotFoundException;
import link.signalapp.error.params.MaxNumberExceptionParams;
import link.signalapp.error.params.MaxSizeExceptionParams;
import link.signalapp.file.FileManager;
import link.signalapp.mapper.SignalMapper;
import link.signalapp.model.Signal;
import link.signalapp.repository.SignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignalService extends ServiceBase {

    public static final int MAX_SIGNAL_LENGTH = 1024000;
    public static final int MAX_USER_STORED_SIGNALS_NUMBER = 50;
    private static final String DEFAULT_SORT_FIELD = "createTime";
    private static final List<String> AVAILABLE_SORT_FIELDS = List.of("name", "description", "sampleRate");

    private final SignalRepository signalRepository;
    private final FileManager fileManager;

    public ResponseWithTotalCounts<SignalDtoResponse> filter(SignalFilterDto filter) {
        int userId = getUserFromContext().getId();
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize() > MAX_USER_STORED_SIGNALS_NUMBER
                || filter.getSize() <= 0
                ? MAX_USER_STORED_SIGNALS_NUMBER
                : filter.getSize(), getSort(filter));
        String search = filter.getSearch() == null || filter.getSearch().isEmpty() ? "" : "%" + filter.getSearch() + "%";
        Page<Signal> signalPage = signalRepository.findByUserIdAndFilter(userId, search,
                listWithDefaultValue(filter.getSampleRates(), BigDecimal.ZERO),
                listWithDefaultValue(filter.getFolderIds(), 0), pageable);
        return new ResponseWithTotalCounts<SignalDtoResponse>()
                .setData(signalPage.stream().map(SignalMapper.INSTANCE::signalToDto).toList())
                .setPages(signalPage.getTotalPages())
                .setElements(signalPage.getTotalElements());
    }

    @Transactional(rollbackFor = IOException.class)
    public IdDtoResponse add(SignalDtoRequest request, byte[] data)
            throws IOException, UnsupportedAudioFileException {
        checkAudioData(data);
        int userId = getUserFromContext().getId();
        checkStoredByUserSignalsNumber(userId);
        Signal signal = signalRepository.save(new Signal(request, userId));
        fileManager.writeWavToFile(userId, signal.getId(), data);
        return new IdDtoResponse().setId(signal.getId());
    }

    @Transactional(rollbackFor = IOException.class)
    public void update(SignalDtoRequest request, byte[] data, int id)
            throws IOException, UnsupportedAudioFileException {
        checkAudioData(data);
        int userId = getUserFromContext().getId();
        Signal signal = getSignalById(id)
                .setName(request.getName())
                .setDescription(request.getDescription())
                .setMaxAbsY(request.getMaxAbsY())
                .setSampleRate(request.getSampleRate())
                .setXMin(request.getXMin());
        signalRepository.save(signal);
        fileManager.writeWavToFile(userId, signal.getId(), data);
    }

    @Transactional
    public void delete(int id) {
        int userId = getUserFromContext().getId();
        if (signalRepository.deleteByIdAndUserId(id, userId) == 0) {
            throw new SignalAppNotFoundException();
        } else {
            fileManager.deleteSignalData(userId, id);
        }
    }

    public SignalDtoResponse getSignal(int id) {
        Signal signal = getSignalById(id);
        return SignalMapper.INSTANCE.signalToDto(signal);
    }

    public byte[] getWav(int id) throws IOException {
        Signal signal = getSignalById(id);
        return fileManager.readWavFromFile(signal.getUserId(), signal.getId());
    }

    public List<BigDecimal> getSampleRates() {
        return signalRepository.sampleRatesByUserId(getUserFromContext().getId());
    }

    private Signal getSignalById(int id) {
        Signal signal = signalRepository.findByIdAndUserId(id, getUserFromContext().getId());
        if (signal == null) {
            throw new SignalAppNotFoundException();
        }
        return signal;
    }

    private Sort getSort(SignalFilterDto filter) {
        Sort sort = Sort.by(camelToSnake(getSortByOrDefault(filter.getSortBy())));
        return "asc".equals(filter.getSortDir()) ? sort : sort.descending();
    }

    private String getSortByOrDefault(String sortBy) {
        return sortBy != null && !sortBy.isEmpty() && AVAILABLE_SORT_FIELDS.contains(sortBy)
                ? sortBy : DEFAULT_SORT_FIELD;
    }

    private void checkStoredByUserSignalsNumber(int userId) {
        if (signalRepository.countByUserId(userId) >= MAX_USER_STORED_SIGNALS_NUMBER) {
            throw new SignalAppConflictException(SignalAppErrorCode.TOO_MANY_SIGNALS_STORED,
                    new MaxNumberExceptionParams(SignalService.MAX_USER_STORED_SIGNALS_NUMBER));
        }
    }

    private void checkAudioData(byte[] data) throws UnsupportedAudioFileException, IOException {
        AudioSampleReader asr = new AudioSampleReader(new ByteArrayInputStream(data));
        long sampleCount = asr.getSampleCount();
        if (sampleCount > MAX_SIGNAL_LENGTH) {
            throw new SignalAppException(SignalAppErrorCode.TOO_LONG_SIGNAL,
                    new MaxSizeExceptionParams(MAX_SIGNAL_LENGTH));
        }
        AudioFormat format = asr.getFormat();
        if (format.getChannels() > 1) {
            throw new SignalAppException(SignalAppErrorCode.WRONG_VAW_FORMAT, null);
        }
    }

    private <T> List<T> listWithDefaultValue(List<T> list, T defaultValue) {
        return list == null || list.isEmpty() ? List.of(defaultValue) : list;
    }

    private String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

}
