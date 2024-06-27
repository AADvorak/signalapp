package link.signalapp.service;

import link.signalapp.audio.AudioSampleReader;
import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.request.SignalsPageDtoRequest;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.dto.response.PageDtoResponse;
import link.signalapp.dto.response.SignalDtoResponse;
import link.signalapp.error.code.SignalAppErrorCode;
import link.signalapp.error.exception.SignalAppConflictException;
import link.signalapp.error.exception.SignalAppException;
import link.signalapp.error.exception.SignalAppNotFoundException;
import link.signalapp.error.params.MaxNumberExceptionParams;
import link.signalapp.error.params.MaxSizeExceptionParams;
import link.signalapp.file.FileManager;
import link.signalapp.mapper.SignalMapper;
import link.signalapp.model.Role;
import link.signalapp.model.Signal;
import link.signalapp.properties.ApplicationProperties;
import link.signalapp.repository.SignalRepository;
import link.signalapp.service.specifications.SignalSpecifications;
import link.signalapp.service.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    private static final String DEFAULT_SORT_FIELD = "createTime";
    private static final List<String> AVAILABLE_SORT_FIELDS = List.of("name", "description", "sampleRate");

    private final SignalRepository signalRepository;
    private final FileManager fileManager;
    private final ApplicationProperties applicationProperties;
    private final PagingUtils pagingUtils = new PagingUtils(AVAILABLE_SORT_FIELDS, DEFAULT_SORT_FIELD);

    public PageDtoResponse<SignalDtoResponse> getPage(SignalsPageDtoRequest request) {
        int userId = getUserFromContext().getId();
        Pageable pageable = pagingUtils.getPageable(request, applicationProperties.getMaxPageSize());
        Specification<Signal> specification = makeSignalSpecification(userId, pagingUtils.getSearch(request),
                request.getSampleRates(), request.getFolderIds());
        Page<Signal> signalPage = signalRepository.findAll(specification, pageable);
        return new PageDtoResponse<SignalDtoResponse>()
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
        return signalRepository.findByIdAndUserId(id, getUserFromContext().getId())
                .orElseThrow(SignalAppNotFoundException::new);
    }

    private void checkStoredByUserSignalsNumber(int userId) {
        int maxUserSignalsNumber = getMaxUserSignalsNumber();
        if (signalRepository.countByUserId(userId) >= maxUserSignalsNumber) {
            throw new SignalAppConflictException(SignalAppErrorCode.TOO_MANY_SIGNALS_STORED,
                    new MaxNumberExceptionParams(maxUserSignalsNumber));
        }
    }

    private int getMaxUserSignalsNumber() {
        int maxUserSignalsNumber = applicationProperties.getLimits().getMaxUserSignalsNumber();
        return checkUserHasRole(Role.EXTENDED_STORAGE)
                ? maxUserSignalsNumber * applicationProperties.getLimits().getExtendedStorageMultiplier()
                : maxUserSignalsNumber;
    }

    private void checkAudioData(byte[] data) throws UnsupportedAudioFileException, IOException {
        AudioSampleReader asr = new AudioSampleReader(new ByteArrayInputStream(data));
        long sampleCount = asr.getSampleCount();
        int maxSignalLength = applicationProperties.getLimits().getMaxSignalLength();
        if (sampleCount > maxSignalLength) {
            throw new SignalAppException(SignalAppErrorCode.TOO_LONG_SIGNAL,
                    new MaxSizeExceptionParams(maxSignalLength));
        }
        AudioFormat format = asr.getFormat();
        if (format.getChannels() > 1) {
            throw new SignalAppException(SignalAppErrorCode.WRONG_VAW_FORMAT, null);
        }
    }

    private Specification<Signal> makeSignalSpecification(
            int userId, String search,
            List<BigDecimal> sampleRates, List<Integer> folderIds
    ) {
        return Specification.where(SignalSpecifications.userIdEqual(userId))
                .and(search == null ? null : SignalSpecifications.nameOrDescriptionLike(search))
                .and(sampleRates == null ? null : SignalSpecifications.sampleRateIn(sampleRates))
                .and(folderIds == null ? null : SignalSpecifications.existsFolderWithIdIn(folderIds));
    }
}
