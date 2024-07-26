const pl = {
  language: 'Język',
  name: 'Polski',
  common: {
    buttons: {
      open: 'Otwórz',
      save: 'Zapisz',
      create: 'Stwórz',
      edit: 'Edytuj',
      delete: 'Usuń',
      deleteWithSignals: 'Usuń razem z sygnałami',
      ok: 'OK',
      cancel: 'Anuluj',
      process: 'Przetwórz',
      exportWav: 'Eksportuj wav',
      exportTxt: 'Eksportuj txt',
      exportSignal: 'Eksportuj sygnał',
      exportData: 'Eksportuj dane',
      working: 'Trwa przetwarzanie...',
      changePassword: 'Zmień hasło',
      signIn: 'Zaloguj się',
      signUp: 'Zarejestruj się',
      signOut: 'Wyloguj się',
      play: 'Odtwórz',
      stop: 'Stop',
      folders: 'Dodaj do folderów',
      clear: 'Wyczyść',
      change: 'Zmień',
      toSignalManager: 'Do menadżera sygnałów',
      toSignalGenerator: 'Do generatora sygnałów',
      continueWorkingWithSignal: 'Przedłuż pracę z sygnałem',
      askSelect: 'Pokaż okno wyboru'
    },
    validation: {
      required: 'To pole jest wymagane',
      positive: 'Musi być dodatnie',
      greaterThanZero: 'Musi być większe od zera',
      greaterThan: 'Musi być większe niż {minValue}',
      number: 'Musi być liczbą',
      notGreaterThan: 'Nie może być większe niż {maxValue}',
      between: 'Musi być między {minValue} a {maxValue}',
      same: 'Musi mieć taką samą wartość'
    },
    messages: {
      yes: 'Tak',
      no: 'Nie',
      fileSaveError: 'Błąd podczas zapisywania pliku',
      processSignalWith: 'Przetwórz sygnał {processorName}',
      processSignalsWith: 'Przetwórz sygnały {processorName}',
      processed: 'Przetworzony',
      nothingIsFound: 'Nic nie znaleziono. Zmień kryteria wyszukiwania.',
      error: 'Błąd',
      wrongSignalSamplesNumber: 'Liczba próbek importowanego sygnału nie może być większa niż {maxSamplesNumber}. W wybranym pliku wynosi {samplesNumber}',
      noFolders: 'Nie masz folderów',
      noData: 'Nie ma danych',
      alreadySignedIn: 'Już jesteś zalogowany(a)',
      requiredRole: 'Potrzebna rola'
    },
    fields: {
      name: 'Nazwa',
      description: 'Opis',
      type: 'Typ',
      email: 'E-mail',
      emailConfirmed: 'E-mail potwierdzony',
      password: 'Hasło',
      passwordRepeat: 'Powtórz hasło',
      oldPassword: 'Stare hasło',
      firstName: 'Imię',
      lastName: 'Nazwisko',
      patronymic: 'Patronimik',
      search: 'Szukaj',
      sampleRate: 'Częstotliwość próbkowania',
      createTime: 'Czas stworzenia',
      lastActionTime: 'Czas ostatniej akcji',
      roles: 'Roli',
      storedSignalsNumber: 'Liczba przechowywanych sygnałów',
      sortBy: 'Sortuj według',
    },
    pagination: {
      total: 'Razem: {pages} stron, {elements} wpisów',
      pageSize: 'Rozmiar strony',
      loadParams: 'Parametry ładowania'
    }
  },
  'confirm-dialog': {
    title: 'Potwierdzenie'
  },
  'folder-editor': {
    title: 'Edytuj folder'
  },
  'message': {
    title: 'Wiadomość'
  },
  'select-dialog': {
    title: 'Wybór',
    rememberSelection: 'Zapamiętaj wybór'
  },
  'select-processor': {
    title: 'Wybór przetwarzacza',
    filterByTypes: 'Filtruj według typów',
  },
  'default': {
    settings: 'Ustawienia',
    user: 'Użytkownik',
    navigation: 'Nawigacja',
    startPage: 'Strona startowa',
    on: 'wł',
    off: 'wył',
    darkMode: 'Tryb ciemny: {darkModeState}'
  },
  'number-input-type-select': {
    numberInputType: 'Wprowadzanie liczb',
    numberInputTypes: {
      text: 'Tekst',
      slider: 'Suwak'
    }
  },
  'change-password': {
    name: 'Zmiana hasła',
    passwordChangeSuccess: 'Hasło zostało zmienione pomyślnie',
    passwordChangeError: 'Błąd podczas zmiany hasła',
  },
  'email-confirm-error': {
    name: 'Błąd potwierdzenia e-maila',
    goToUserSettings: 'Przejdź do ustawień użytkownika'
  },
  'folder-manager': {
    name: 'Menadżer folderów',
    confirmDeleteFolder: 'Czy na pewno chcesz usunąć folder {name}?',
    saveError: 'Błąd podczas zapisywania folderu',
    deleteError: 'Błąd podczas usuwania folderu',
  },
  'restore-password': {
    name: 'Przywrócenie hasła',
    newPasswordSentByEmail: 'Nowe hasło zostało wysłane e-mailem',
    passwordSendError: 'Błąd podczas wysyłania nowego hasła',
    restorePasswordMailTitle: 'SignalApp - przywrócenie hasła',
    restorePasswordMailMsg: 'Twoje nowe hasło: $newPassword$. Zaleca się zmienić go natychmiast po zalogowaniu.',
  },
  'selects-settings': {
    afterSaveSignalActions: 'Działania po zapisywaniu sygnału',
    importSignalActions: 'Działania z zaimportowanym sygnałem'
  },
  'signal': {
    name: 'Sygnał',
    saveAsNew: 'Zapisz jako nowy',
    sampleRate: 'Częstotliwość próbkowania: {sampleRate} Hz',
    samplesNumber: 'Liczba próbek: {samplesNumber}',
    processSignalError: 'Błąd podczas przetwarzania sygnału',
    signalDataSaveError: 'Błąd podczas zapisywania danych sygnału',
    signalSaveError: 'Błąd podczas zapisywania sygnału',
    signalNotFound: 'Nie znaleziono sygnału',
    signalIsSaved: 'Sygnał został zapisany pomyślnie'
  },
  'signal-importer': {
    'import': 'Importuj',
    fromTextOrWavFile: 'Z pliku txt, csv, json, xml lub wav',
    importedFromFile: 'Zaimportowano z pliku {name}',
    selectAction: 'Wybierz akcję z importowanym sygnałem',
    selectChannel: 'Wybierz kanał audio do zaimportowania',
    wrongFileFormat: 'Niewłaściwy format pliku',
  },
  'signal-generator': {
    name: 'Generator sygnałów',
    generate: 'Generuj',
    begin: 'Początek sygnału (B)',
    length: 'Długość sygnału (L)',
    sampleRate: 'Częstotliwość próbkowania sygnału (S)',
    frequency: 'Częstotliwość sygnału (F)',
    amplitude: 'Amplituda sygnału (A)',
    offset: 'Przesunięcie sygnału (O)',
    form: 'Kształt sygnału',
    forms: {
      sine: 'sinusoidalny',
      square: 'prostokątny',
      triangle: 'trójkątny',
      sawtooth: 'piłokształtny',
      noise: 'szumowy'
    },
    signalName: 'Wygenerowany sygnał {form}',
    description: 'Wygenerowany sygnał {form} z F = {frequency} ({length} próbek)',
    wrongPointsNumber: 'Liczba próbek sygnału (S * L) musi być między 2 a 512000. Obecnie wynosi {pointsNumber}',
    lessThanHalfSampleRate: 'Musi być mniejsza niż połowa częstotliwości próbkowania',
    preview: 'Podgląd'
  },
  'signal-manager': {
    name: 'Menadżer sygnałów',
    sampleRates: 'Częstotliwośći próbkowania',
    folders: 'Foldery',
    youHaveNoStoredSignals: 'Nie masz zapisanych sygnałów',
    generate: 'Generuj',
    or: 'lub',
    record: 'nagraj',
    newSignalsToStartWorking: 'nowe sygnały do rozpoczęcia pracy',
    actionsWithSelectedSignals: 'Działania z wybranymi sygnałami',
    view: 'Przegląd',
    viewSignals: 'Przegląd sygnałów',
    loadSignalsError: 'Błąd podczas ładowania sygnałów',
    confirmDeleteSignal: 'Czy na pewno chcesz usunąć {name}?',
    confirmDeleteSignals: 'Czy na pewno chcesz usunąć wybrane sygnały (liczba: {length})?',
  },
  'signal-recorder': {
    name: 'Nagrywarka sygnałów',
    sampleRate: 'Częstotliwość próbkowania (Hz)',
    rec: 'Nagrywaj',
    pause: 'Pauza',
    stop: 'Stop',
    clear: 'Wyczyść',
    recordStatuses: {
      ready: 'Gotowy do nagrywania',
      recording: 'Trwa nagrywanie...',
      paused: 'Nagrywanie wstrzymane',
    },
    nothingRecorded: 'Nic nie jest nagrane',
    audioFormat: 'Format: 1 kanał, pcm, {sampleRate} Hz',
    fileName: 'Nagrany {dateTime}.wav',
  },
  'signin': {
    name: 'Logowanie',
    signInToContinue: 'Aby kontynuować, zaloguj się',
    forgotPassword: 'Zapomniałeś hasła',
    signInError: 'Błąd podczas logowania',
  },
  'signup': {
    name: 'Rejestracja',
    signUpError: 'Błąd podczas rejestracji'
  },
  'user-settings': {
    name: 'Ustawienia użytkownika',
    emailNotConfirmed: 'Adres e-mail nie jest potwierdzony',
    emailConfirmed: 'Adres e-mail jest potwierdzony',
    confirm: 'Potwierdź',
    deleteAccount: 'Usuń konto',
    saveSuccess: 'Informacje o użytkowniku zostały zapisane',
    saveError: 'Błąd podczas zapisywania informacji o użytkowniku',
    deleteConfirm: 'Czy na pewno chcesz usunąć swoje konto? Wszystkie Twoje dane zostaną natychmiast usunięte bez możliwości odzyskania.',
    deleteError: 'Błąd podczas usuwania konta',
    sendEmailError: 'Błąd podczas wysyłania e-maila',
    confirmSentCheckEmail: 'Wiadomość potwierdzająca została wysłana, sprawdź swój e-mail {email}',
    confirmEmailMailTitle: 'SignalApp - potwierdzenie e-maila',
    confirmEmailMailMsg: 'Aby potwierdzić e-mail, użyj linku $origin$/api/users/confirm/$code$'
  },
  'admin-users': {
    name: 'Użytkownicy',
    roles: 'Role',
    confirmDeleteUser: 'Czy na pewno chcesz usunąć użytkownika z e-mailem {email}?'
  },
  processorNames: {
    LinearAmp: 'Wzmacniacz liniowy',
    withLinearAmp: 'wzmacniaczem liniowym',
    PiecewiseLinearSymmetricSaturationAmp: 'Wzmacniacz symetryczny liniowy z nasyceniem',
    withPiecewiseLinearSymmetricSaturationAmp: 'symetrycznym wzmacniaczem liniowym z nasyceniem',
    PiecewiseLinearAsymmetricSaturationAmp: 'Wzmacniacz asymetryczny liniowy z nasyceniem',
    withPiecewiseLinearAsymmetricSaturationAmp: 'asymetrycznym wzmacniaczem liniowym z nasyceniem',
    Inverter: 'Inwerter',
    withInverter: 'inwerterem',
    AmplitudeModulator: 'Modulator amplitudy',
    withAmplitudeModulator: 'modulatorem amplitudy',
    FrequencyModulator: 'Modulator częstotliwości',
    withFrequencyModulator: 'modulatorem częstotliwości',
    LpRcFilter: 'Filtr RC dolnoprzepustowy',
    withLpRcFilter: 'filtrem RC dolnoprzepustowym',
    HpRcFilter: 'Filtr RC górnoprzepustowy',
    withHpRcFilter: 'filtrem RC górnoprzepustowym',
    LinearOscillator: 'Oscylator liniowy',
    withLinearOscillator: 'oscylatorem liniowym',
    Integrator: 'Integrator',
    withIntegrator: 'integratorem',
    Differentiator: 'Dyferencjator',
    withDifferentiator: 'dyferencjatorem',
    SpectrumAnalyserDct: 'Analizator widma (DCT)',
    withSpectrumAnalyserDct: 'analizatorem widma (DCT)',
    SpectrumAnalyserFft: 'Analizator widma (FFT)',
    withSpectrumAnalyserFft: 'analizatorem widma (FFT)',
    SelfCorrelator: 'Autokorelator',
    withSelfCorrelator: 'autokorelatorem',
    Adder: 'Sumator',
    withAdder: 'sumatorem',
    Correlator: 'Korelator',
    withCorrelator: 'korelatorem',
    TwoSignalAmplitudeModulator: 'Modulator amplitudy nośnego sygnału modulującym',
    withTwoSignalAmplitudeModulator: 'modulatorem amplitudy nośnego sygnału modulującym'
  },
  processorTypes: {
    amplifier: 'Wzmacniacz',
    modulator: 'Modulator',
    filter: 'Filtr',
    oscillator: 'Oscylator',
    math: 'Matematyczne',
  },
  processorParams: {
    Adder: {
      coefficient1: 'Współczynnik sygnału 1',
      coefficient2: 'Współczynnik sygnału 2',
      description: 'Suma {name1} z współczynnikiem {coefficient1} i {name2} z współczynnikiem {coefficient2}',
    },
    AmplitudeModulator: {
      frequency: 'Częstotliwość nośnego sygnału',
      amplitude: 'Amplituda nośnego sygnału',
      depth: 'Głębokość modulacji',
    },
    Correlator: {
      description: 'Korelacja {name1} i {name2}',
    },
    FrequencyModulator: {
      frequency: 'Częstotliwość nośnego sygnału',
      amplitude: 'Amplituda nośnego sygnału',
      deviation: 'Współczynnik modulacji częstotliwości',
    },
    HpRcFilter: {
      tau: 'Stała czasowa',
    },
    LinearAmp: {
      coefficient: 'Współczynnik wzmocnienia'
    },
    LinearOscillator: {
      frequency: 'Częstotliwość',
      damping: 'Tłumienie',
    },
    LpRcFilter: {
      tau: 'Stała czasowa',
    },
    PiecewiseLinearAsymmetricSaturationAmp: {
      coefficient: 'Współczynnik wzmocnienia',
      maxPositiveOutput: 'Maksymalna dodatnia amplituda wyjściowa',
      maxNegativeOutput: 'Maksymalna ujemna amplituda wyjściowa',
    },
    PiecewiseLinearSymmetricSaturationAmp: {
      coefficient: 'Współczynnik wzmocnienia',
      maxOutput: 'Maksymalna amplituda wyjściowa',
    },
    TwoSignalAmplitudeModulator: {
      depth: 'Głębokość modulacji',
      description: '{name1} z amplitudą zmodulowaną przez {name2} z głębokością modulacji {depth}'
    }
  },
  signalNames: {
    signal1: 'Sygnał 1',
    signal2: 'Sygnał 2',
    carrierSignal: 'Sygnał nośny',
    modulatingSignal: 'Sygnał modulujący'
  },
  operationNames: {
    preparingSignal: 'Przygotowywanie sygnału',
    preparingSignals: 'Przygotowywanie sygnałów',
    estimating: 'Obliczanie',
    calculatingMaxAbsValue: 'Przetwarzanie końcowe',
  },
  serverMessages: {
    SIGNAL_DOES_NOT_EXIST: 'Nie znaleziono sygnału',
    EMAIL_ALREADY_EXISTS: 'Adres e-mail już istnieje',
    FOLDER_NAME_ALREADY_EXISTS: 'Imię folderu już istnieje',
    EMAIL_ALREADY_CONFIRMED: 'Adres e-mail jest już potwierdzony',
    WRONG_EMAIL_PASSWORD: 'Nieprawidłowa para adres e-mail i hasło',
    WRONG_EMAIL: 'Adres e-mail nie istnieje lub nie jest potwierdzony',
    WRONG_OLD_PASSWORD: 'Nieprawidłowe stare hasło',
    TOO_LONG_FILE: 'Plik jest zbyt długi. Maksymalny rozmiar wynosi {maxSize} bajtów.',
    TOO_LONG_SIGNAL: 'Sygnał jest zbyt długi. Maksymalny rozmiar wynosi {maxSize} próbek.',
    TOO_MANY_SIGNALS_STORED: 'Masz zbyt wiele przechowywanych sygnałów. Maksymalna ich liczba to {maxNumber}.',
    TOO_MANY_FOLDERS_CREATED: 'Masz zbyt wiele stworzonych folderów. Maksymalna ich liczba to {maxNumber}.',
    NotEmpty: 'To pole jest wymagane',
    MinLength: 'Naruszenie minimalnej długości',
    Size: 'Naruszenie rozmiaru',
    MaxLength: 'Naruszenie maksymalnej długości',
    Email: 'Musi być w formacie adresu e-mail',
    RECAPTCHA_TOKEN_NOT_VERIFIED: 'Weryfikacja recaptcha nie udała się',
    INTERNAL_SERVER_ERROR: 'Wewnętrzny błąd serwera',
    UNKNOWN_ERROR: 'Niewiadomy błąd'
  },
  chart: {
    resetZoom: 'Zresetuj zoom'
  },
  userRoles: {
    'ADMIN': 'Admin',
    'EXTENDED_STORAGE': 'Rozszerzone przechowywanie'
  }
}

export default pl
