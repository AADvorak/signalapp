const pl = {
  language: 'Język',
  name: 'Polski',
  common: {
    buttons: {
      save: 'Zapisz',
      delete: 'Usuń',
      ok: 'OK',
      cancel: 'Anuluj',
      transform: 'Przetwórz',
      exportWav: 'Eksportuj wav',
      exportTxt: 'Eksportuj txt',
      working: 'Trwa przetwarzanie...',
      changePassword: 'Zmień hasło',
      signIn: 'Zaloguj się',
      signUp: 'Zarejestruj się',
      signOut: 'Wyloguj się'
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
      fileSaveError: 'Błąd podczas zapisywania pliku',
      transformSignalWith: 'Przetwórz sygnał {transformerName}',
      transformSignalsWith: 'Przetwórz sygnały {transformerName}',
      transformed: 'Przetworzony'
    },
    fields: {
      name: 'Nazwa',
      description: 'Opis',
      type: 'Typ',
      email: 'E-mail',
      password: 'Hasło',
      passwordRepeat: 'Powtórz hasło',
      firstName: 'Imię',
      lastName: 'Nazwisko',
      patronymic: 'Patronimik'
    },
    search: 'Szukaj'
  },
  'confirm-dialog': {
    title: 'Potwierdzenie'
  },
  'message': {
    title: 'Wiadomość'
  },
  'select-transformer-dialog': {
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
  'change-password': {
    name: 'Zmiana hasła',
    oldPassword: 'Stare hasło',
    passwordChangeSuccess: 'Hasło zostało zmienione pomyślnie',
    passwordChangeError: 'Błąd podczas zmiany hasła',
  },
  'email-confirm-error': {
    name: 'Błąd potwierdzenia e-maila',
    goToUserSettings: 'Przejdź do ustawień użytkownika'
  },
  'restore-password': {
    name: 'Przywrócenie hasła',
    newPasswordSentByEmail: 'Nowe hasło zostało wysłane e-mailem',
    passwordSendError: 'Błąd podczas wysyłania nowego hasła',
  },
  'signal': {
    name: 'Sygnał',
    saveAsNew: 'Zapisz jako nowy',
    sampleRate: 'Częstotliwość próbkowania = {sampleRate}',
    transformSignalError: 'Błąd podczas przetwarzania sygnału',
    signalDataSaveError: 'Błąd podczas zapisywania danych sygnału',
    signalSaveError: 'Błąd podczas zapisywania sygnału',
    signalNotFound: 'Nie znaleziono sygnału'
  },
  'signal-generator': {
    name: 'Generator sygnałów',
    generate: 'Generuj',
    begin: 'Początek sygnału',
    length: 'Długość sygnału',
    sampleRate: 'Częstotliwość próbkowania sygnału',
    frequency: 'Częstotliwość sygnału',
    amplitude: 'Amplituda sygnału',
    offset: 'Przesunięcie sygnału',
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
    'import': 'Importuj',
    fromTxtOrWavFile: 'Z pliku txt lub wav',
    importedFromFile: 'Zaimportowano z pliku {name}',
    wrongPointsNumber: 'Liczba próbek sygnału (S * L) musi być między 2 a 512000. Obecnie wynosi {pointsNumber}',
    lessThanHalfSampleRate: 'Musi być mniejsza niż połowa częstotliwości próbkowania',
    preview: 'Podgląd'
  },
  'signal-manager': {
    name: 'Menadżer sygnałów',
    total: 'Razem: {pages} stron, {elements} wpisów',
    pageSize: 'Rozmiar strony',
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
    confirmDeleteSignals: 'Czy na pewno chcesz usunąć wybrane sygnały (liczba: {length})?'
  },
  'signal-recorder': {
    name: 'Nagrywarka sygnałów',
    sampleRateHz: 'Częstotliwość próbkowania (Hz)',
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
    signInError: 'Błąd podczas logowania'
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
    confirmSentCheckEmail: 'Wiadomość potwierdzająca została wysłana, sprawdź swój e-mail {email}'
  },
  transformerNames: {
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
    SpectrumAnalyser: 'Analizator widma',
    withSpectrumAnalyser: 'analizatorem widma',
    SelfCorrelator: 'Autokorelator',
    withSelfCorrelator: 'autokorelatorem',
    Adder: 'Sumator',
    withAdder: 'sumatorem',
    Correlator: 'Korelator',
    withCorrelator: 'korelatorem',
    TwoSignalAmplitudeModulator: 'Modulator amplitudy nośnego sygnału modulującym',
    withTwoSignalAmplitudeModulator: 'modulatorem amplitudy nośnego sygnału modulującym'
  },
  transformerTypes: {
    amplifier: 'Wzmacniacz',
    modulator: 'Modulator',
    filter: 'Filtr',
    oscillator: 'Oscylator',
    math: 'Matematyczne',
  },
  transformerParams: {
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
    calculatingMaxAbsValue: 'Obliczanie maksymalnej wartości bezwzględnej',
  },
  serverMessages: {
    SIGNAL_DOES_NOT_EXIST: 'Nie znaleziono sygnału',
    EMAIL_ALREADY_EXISTS: 'Adres e-mail już istnieje',
    EMAIL_ALREADY_CONFIRMED: 'Adres e-mail jest już potwierdzony',
    WRONG_EMAIL_PASSWORD: 'Nieprawidłowa para adres e-mail i hasło',
    WRONG_EMAIL: 'Adres e-mail nie istnieje lub nie jest potwierdzony',
    WRONG_OLD_PASSWORD: 'Nieprawidłowe stare hasło',
    TOO_LONG_FILE: 'Plik jest zbyt długi. Maksymalny rozmiar wynosi {maxSize} bajtów.',
    TOO_MANY_SIGNALS_STORED: 'Masz zbyt wiele przechowywanych sygnałów. Maksymalna ich liczba to {maxNumber}.',
    NotEmpty: 'To pole jest wymagane',
    MinLength: 'Naruszenie minimalnej długości',
    Size: 'Naruszenie rozmiaru',
    MaxLength: 'Naruszenie maksymalnej długości',
    Email: 'Musi być w formacie adresu e-mail'
  },
  chart: {
    resetZoom: 'Zresetuj zoom'
  }
}

export default pl
