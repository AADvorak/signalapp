package link.signalapp.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum SignalAppDataErrorCode {

    SIGNAL_DOES_NOT_EXIST("Signal does not exist", List.of()),
    EMAIL_ALREADY_EXISTS("Email already exists", List.of("email")),
    MODULE_ALREADY_EXISTS("Module already exists", List.of("module")),
    FOLDER_NAME_ALREADY_EXISTS("Folder name already exists", List.of("name")),
    EMAIL_ALREADY_CONFIRMED("Email is already confirmed", List.of()),
    WRONG_EMAIL_PASSWORD("Wrong email and password pair", List.of("email", "password")),
    WRONG_EMAIL("Email does not exist or is not confirmed", List.of("email")),
    WRONG_OLD_PASSWORD("Wrong old password", List.of("oldPassword")),
    USER_TOKEN_NOT_FOUND("User token does not found", List.of()),
    RECAPTCHA_TOKEN_NOT_VERIFIED("Recaptcha token verification failed", List.of("token"));

    private final String description;
    private final List<String> fields;

}
