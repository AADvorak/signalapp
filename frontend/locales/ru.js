const ru = {
  language: 'Язык',
  name: 'Русский',
  common: {
    buttons: {
      open: 'Открыть',
      save: 'Сохранить',
      create: 'Создать',
      edit: 'Редактировать',
      delete: 'Удалить',
      deleteWithSignals: 'Удалить вместе с сигналами',
      ok: 'OK',
      cancel: 'Отмена',
      process: 'Преобразовать',
      exportWav: 'Экспортировать wav',
      exportTxt: 'Экспортировать txt',
      exportSignal: 'Экспортировать сигнал',
      exportData: 'Экспортировать данные',
      working: 'Вычисление...',
      changePassword: 'Изменить пароль',
      signIn: 'Войти',
      signUp: 'Зарегистрироваться',
      signOut: 'Выйти',
      play: 'Воспроизвести',
      stop: 'Остановить',
      folders: 'Добавить в папки',
      clear: 'Очистить',
      change: 'Поменять',
      toSignalManager: 'В менеджер сигналов',
      toSignalGenerator: 'В генератор сигналов',
      continueWorkingWithSignal: 'Продолжить работу с сигналом',
      askSelect: 'Показать диалог с выбором'
    },
    validation: {
      required: 'Поле обязательно к заполнению',
      positive: 'Значение должно быть положительным',
      greaterThanZero: 'Значение должно быть больше нуля',
      greaterThan: 'Значение должно быть больше {minValue}',
      number: 'Требуется числовое значение',
      notGreaterThan: 'Значение должно быть не больше {maxValue}',
      between: 'Значение должно быть в диапазоне от {minValue} до {maxValue}',
      same: 'Должны иметь одинаковое значение'
    },
    messages: {
      yes: 'Да',
      no: 'Нет',
      fileSaveError: 'Ошибка при сохранении файла',
      processSignalWith: 'Преобразовать сигнал {processorName}',
      processSignalsWith: 'Преобразовать сигналы {processorName}',
      processed: 'Преобразован',
      nothingIsFound: 'Ничего не найдено. Измените критерии поиска.',
      error: 'Ошибка',
      wrongSignalSamplesNumber: 'Количество отсчетов импортируемого сигнала должно быть не более {maxSamplesNumber}. В выбранном файле оно равно {samplesNumber}',
      noFolders: 'У вас нет папок',
      noData: 'Нет данных',
      alreadySignedIn: 'Вход уже выполнен',
      requiredRole: 'Требуется роль'
    },
    fields: {
      name: 'Имя',
      description: 'Описание',
      type: 'Тип',
      email: 'Email',
      emailConfirmed: 'Email подтвержден',
      password: 'Пароль',
      passwordRepeat: 'Повтор пароля',
      oldPassword: 'Старый пароль',
      firstName: 'Имя',
      lastName: 'Фамилия',
      patronymic: 'Отчество',
      search: 'Поиск',
      sampleRate: 'Частота дискретизации',
      createTime: 'Время создания',
      lastActionTime: 'Время последнего действия',
      roles: 'Роли',
      storedSignalsNumber: 'Количество сохраненных сигналов',
      sortBy: 'Сортировать по',
    },
    pagination: {
      total: 'Всего: {pages} страниц, {elements} записей',
      of: 'из',
      pageSize: 'Размер страницы',
      loadParams: 'Параметры загрузки'
    }
  },
  'confirm-dialog': {
    title: 'Подтверждение'
  },
  'folder-editor': {
    title: 'Редактор папок'
  },
  'message': {
    title: 'Сообщение'
  },
  'select-dialog': {
    title: 'Выбор',
    rememberSelection: 'Запомнить выбор'
  },
  'select-processor': {
    title: 'Выбор преобразователя',
    filterByTypes: 'Фильтр по типу'
  },
  'default': {
    settings: 'Настройки',
    user: 'Пользователь',
    navigation: 'Навигация',
    startPage: 'Стартовая страница',
    on: 'вкл',
    off: 'выкл',
    darkMode: 'Темный режим: {darkModeState}'
  },
  'number-input-type-select': {
    numberInputType: 'Ввод чисел',
    numberInputTypes: {
      text: 'Текст',
      slider: 'Слайдер'
    }
  },
  'mobile-version-select': {
    mobileVersion: 'Мобильная версия',
    mobileVersionStates: {
      auto: 'Авто',
      on: 'Вкл',
      off: 'Выкл'
    },
  },
  'change-password': {
    name: 'Изменение пароля',
    passwordChangeSuccess: 'Пароль успешно изменен',
    passwordChangeError: 'Ошибка при изменении пароля',
  },
  'email-confirm-error': {
    name: 'Ошибка подтверждения эл. почты',
    goToUserSettings: 'Перейти в настройки пользователя'
  },
  'folder-manager': {
    name: 'Менеджер папок',
    confirmDeleteFolder: 'Удалить папку {name}?',
    saveError: 'Ошибка при сохранении папки',
    deleteError: 'Ошибка при удалении папки',
  },
  'restore-password': {
    name: 'Восстановление пароля',
    newPasswordSentByEmail: 'Новый пароль отправлен на эл почту',
    passwordSendError: 'Ошибка при отправке нового пароля',
    restorePasswordMailTitle: 'SignalApp - восстановление пароля',
    restorePasswordMailMsg: 'Ваш новый пароль: $newPassword$. Рекомендуется изменить его сразу после входа в учетную запись.',
  },
  'selects-settings': {
    afterSaveSignalActions: 'Действия после сохрения сигнала',
    importSignalActions: 'Действия при импорте сигнала'
  },
  'signal': {
    name: 'Сигнал',
    saveAsNew: 'Сохранить как новый',
    sampleRate: 'Частота дискретизации: {sampleRate} Гц',
    samplesNumber: 'Количество отсчетов: {samplesNumber}',
    processSignalError: 'Ошибка при преобразовании сигнала',
    signalDataSaveError: 'Ошибка при сохранении данных сигнала',
    signalSaveError: 'Ошибка при сохранении сигнала',
    signalNotFound: 'Сигнал не найден',
    signalIsSaved: 'Сигнал сохранен'
  },
  'signal-importer': {
    'import': 'Импортировать',
    fromTextOrWavFile: 'Из файла txt, csv, json, xml или wav',
    importedFromFile: 'Импортирован из файла {name}',
    selectAction: 'Выберите действие с импортируемым сигналом',
    selectChannel: 'Выберите аудиоканал для импорта',
    wrongFileFormat: 'Неверный формат файла',
  },
  'signal-generator': {
    name: 'Генератор сигналов',
    generate: 'Сгенерировать',
    begin: 'Начало сигнала (B)',
    length: 'Длительность сигнала (L)',
    sampleRate: 'Частота дискретизации сигнала (S)',
    frequency: 'Частота сигнала (F)',
    amplitude: 'Амплитуда сигнала (A)',
    offset: 'Сдвиг сигнала (O)',
    form: 'Форма сигнала',
    forms: {
      sine: 'синусоидальный',
      square: 'прямоугольный',
      triangle: 'треугольный',
      sawtooth: 'пилообразный',
      noise: 'шумовой'
    },
    signalName: 'Сгенерированный {form} сигнал',
    description: 'Сгенерированный {form} сигнал с частотой F = {frequency} ({length} отсчетов)',
    wrongPointsNumber: 'Количество отсчетов (S * L) должно быть в диапазоне от 2 до 512000. Сейчас оно равно {pointsNumber}',
    lessThanHalfSampleRate: 'Значение должно быть не больше половины частоты дискретизации',
    preview: 'Предпросмотр'
  },
  'signal-manager': {
    name: 'Менеджер сигналов',
    sampleRates: 'Частоты дискретизации',
    folders: 'Папки',
    youHaveNoStoredSignals: 'У вас нет сохраненных сигналов',
    generate: 'Сгенерируйте',
    or: 'или',
    record: 'запишите',
    newSignalsToStartWorking: 'новые сигналы, чтобы начать работать',
    actionsWithSelectedSignals: 'Действия с выбранными сигналами',
    view: 'Просмотр',
    viewSignals: 'Просмотр сигналов',
    loadSignalsError: 'Ошибка при загрузке сигналов',
    confirmDeleteSignal: 'Удалить {name}?',
    confirmDeleteSignals: 'Удалить выбранные сигналы (количество: {length})?',
  },
  'signal-recorder': {
    name: 'Запись сигналов',
    sampleRate: 'Частота дискретизации (Гц)',
    rec: 'Запись',
    pause: 'Пауза',
    stop: 'Стоп',
    clear: 'Очистить',
    recordStatuses: {
      ready: 'Ожидание начала записи',
      recording: 'Идет запись...',
      paused: 'Запись поставлена на паузу',
    },
    nothingRecorded: 'Запись отсутствует',
    audioFormat: 'Формат: 1 канал, pcm, {sampleRate} Гц',
    fileName: 'Записано {dateTime}.wav',
  },
  'signin': {
    name: 'Вход',
    signInToContinue: 'Пожалуйста, войдите для продолжения',
    forgotPassword: 'Не помню пароль',
    signInError: 'Ошибка при входе',
  },
  'signup': {
    name: 'Регистрация',
    signUpError: 'Ошибка при регистрации'
  },
  'user-settings': {
    name: 'Настройки пользователя',
    emailNotConfirmed: 'Эл. почта не подтверждена',
    emailConfirmed: 'Эл. почта подтверждена',
    confirm: 'Подтвердить',
    deleteAccount: 'Удалить аккаунт',
    saveSuccess: 'Данные пользователя сохранены',
    saveError: 'Ошибка при сохранении данных пользователя',
    deleteConfirm: 'Вы точно хотите удалить аккаунт? Все ваши данные будут немедленно удалены без возможности восстановления.',
    deleteError: 'Ошибка при удалении аккаунта',
    sendEmailError: 'Ошибка при отправке письма',
    confirmSentCheckEmail: 'Письмо с ссылкой для подтвержения отправлено на {email}',
    confirmEmailMailTitle: 'SignalApp - подтверждение эл. почты',
    confirmEmailMailMsg: 'Чтобы подтвердить электронную почту перейдите по ссылке $origin$/api/users/confirm/$code$'
  },
  'admin-users': {
    name: 'Пользователи',
    roles: 'Роли',
    confirmDeleteUser: 'Удалить пользователя с email {email}?'
  },
  processorNames: {
    LinearAmp: 'Линейный усилитель',
    withLinearAmp: 'линейным усилителем',
    PiecewiseLinearSymmetricSaturationAmp: 'Усилитель с насыщением с кусочно-линейной симметричной амплитудной характеристикой',
    withPiecewiseLinearSymmetricSaturationAmp: 'усилителем с насыщением с кусочно-линейной симметричной амплитудной характеристикой',
    PiecewiseLinearAsymmetricSaturationAmp: 'Усилитель с насыщением с кусочно-линейной несимметричной амплитудной характеристикой',
    withPiecewiseLinearAsymmetricSaturationAmp: 'усилителем с насыщением с кусочно-линейной несимметричной амплитудной характеристикой',
    Inverter: 'Инвертер',
    withInverter: 'инвертером',
    AmplitudeModulator: 'Амплитудный модулятор',
    withAmplitudeModulator: 'амплитудным модулятором',
    FrequencyModulator: 'Частотный модулятор',
    withFrequencyModulator: 'частотным модулятором',
    LpRcFilter: 'RC фильтр нижних частот',
    withLpRcFilter: 'RC фильтром нижних частот',
    HpRcFilter: 'RC фильтр верхних частот',
    withHpRcFilter: 'RC фильтром верхних частот',
    LinearOscillator: 'Линейный осциллятор',
    withLinearOscillator: 'линейным осциллятором',
    Integrator: 'Интегратор',
    withIntegrator: 'интегратором',
    Differentiator: 'Дифференциатор',
    withDifferentiator: 'дифференциатором',
    SpectrumAnalyserDct: 'Анализатор спектра (ДКП)',
    withSpectrumAnalyserDct: 'анализатором спектра (ДКП)',
    SpectrumAnalyserFft: 'Анализатор спектра (БПФ)',
    withSpectrumAnalyserFft: 'анализатором спектра (БПФ)',
    SelfCorrelator: 'Автокоррелятор',
    withSelfCorrelator: 'автокоррелятором',
    Adder: 'Сумматор',
    withAdder: 'сумматором',
    Correlator: 'Коррелятор',
    withCorrelator: 'коррелятором',
    TwoSignalAmplitudeModulator: 'Амплитудный модулятор с двумя входными сигналами (несущий, модулирующий)',
    withTwoSignalAmplitudeModulator: 'амплитудным модулятором с двумя входными сигналами (несущий, модулирующий)'
  },
  processorTypes: {
    amplifier: 'Усилитель',
    modulator: 'Модулятор',
    filter: 'Фильтр',
    oscillator: 'Осциллятор',
    math: 'Математический',
  },
  processorParams: {
    Adder: {
      coefficient1: 'Коэффициент сигнала 1',
      coefficient2: 'Коэффициент сигнала 2',
      description: 'Сумма сигнала {name1} с коэффициентом {coefficient1} и {name2} с коэффициентом {coefficient2}',
    },
    AmplitudeModulator: {
      frequency: 'Частота несущего сигнала',
      amplitude: 'Амплитуда несущего сигнала',
      depth: 'Глубина модуляции',
    },
    Correlator: {
      description: 'Корреляция сигналов {name1} и {name2}',
    },
    FrequencyModulator: {
      frequency: 'Частота несущего сигнала',
      amplitude: 'Амплитуда несущего сигнала',
      coefficient: 'Коэффициент модуляции',
    },
    HpRcFilter: {
      tau: 'Постоянная времени',
    },
    LinearAmp: {
      coefficient: 'Коэффициент усиления'
    },
    LinearOscillator: {
      frequency: 'Частота',
      damping: 'Коэффициент затухания',
    },
    LpRcFilter: {
      tau: 'Постоянная времени',
    },
    PiecewiseLinearAsymmetricSaturationAmp: {
      coefficient: 'Коэффициент усиления',
      maxPositiveOutput: 'Максимальная положительная амплитуда',
      maxNegativeOutput: 'Максимальная отрицательная амплитуда',
    },
    PiecewiseLinearSymmetricSaturationAmp: {
      coefficient: 'Коэффициент усиления',
      maxOutput: 'Максимальная амплитуда',
    },
    TwoSignalAmplitudeModulator: {
      depth: 'Глубина модуляции',
      description: '{name1} модулированный по амплитуде сигналом {name2} с глубиной модуляции {depth}'
    }
  },
  signalNames: {
    signal1: 'Сигнал 1',
    signal2: 'Сигнал 2',
    carrierSignal: 'Несущий сигнал',
    modulatingSignal: 'Модулирующий сигнал'
  },
  operationNames: {
    preparingSignal: 'Подготовка сигнала',
    preparingSignals: 'Подготовка сигналов',
    calculating: 'Вычисление',
    calculatingMaxAbsValue: 'Постобработка',
  },
  serverMessages: {
    SIGNAL_DOES_NOT_EXIST: 'Сигнал не нейден',
    EMAIL_ALREADY_EXISTS: 'Данный email уже зарегистрирован',
    FOLDER_NAME_ALREADY_EXISTS: 'Папка с таким именем уже существует',
    EMAIL_ALREADY_CONFIRMED: 'Email уже подтвержден',
    WRONG_EMAIL_PASSWORD: 'Неверная пара email и пароль',
    WRONG_EMAIL: 'Email не существует или не подтвержден',
    WRONG_OLD_PASSWORD: 'Неверный старый пароль',
    TOO_LONG_FILE: 'Слишком большой файл. Максимальный размер файла {maxSize} байт.',
    TOO_LONG_SIGNAL: 'Слишком длинный сигнал. Максимальный размер сигнала {maxSize} отсчетов.',
    TOO_MANY_SIGNALS_STORED: 'Слишком много сохраненных сигналов. Максимальное число сигналов - {maxNumber}.',
    TOO_MANY_FOLDERS_CREATED: 'Создано слишком много папок. Максимальное число папок - {maxNumber}.',
    NotEmpty: 'Поле обязательно к заполнению',
    MinLength: 'Нарушение требования минимальной длины',
    Size: 'Нарушение требований размера',
    MaxLength: 'Нарушение требования максимальной длины',
    Email: 'Должно быть в формате email',
    RECAPTCHA_TOKEN_NOT_VERIFIED: 'Проверка Recaptcha не удалась',
    INTERNAL_SERVER_ERROR: 'Внутренняя ошибка сервера',
    UNKNOWN_ERROR: 'Неизвестная ошибка'
  },
  chart: {
    resetZoom: 'Сбросить увеличение'
  },
  userRoles: {
    'ADMIN': 'Админ',
    'EXTENDED_STORAGE': 'Расширенное хранилище'
  }
}

export default ru
