const ru = {
  language: 'Язык',
  name: 'Русский',
  common: {
    buttons: {
      save: 'Сохранить',
      delete: 'Удалить',
      ok: 'OK',
      cancel: 'Отмена',
      transform: 'Преобразовать',
      exportWav: 'Экспортировать wav',
      exportTxt: 'Экспортировать txt',
      working: 'Вычисление...',
      changePassword: 'Изменить пароль',
      signIn: 'Войти',
      signUp: 'Зарегистрироваться',
      signOut: 'Выйти'
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
      fileSaveError: 'Ошибка при сохранении файла',
      transformSignalWith: 'Преобразовать сигнал {transformerName}',
      transformSignalsWith: 'Преобразовать сигналы {transformerName}',
      transformed: 'Преобразован',
    },
    fields: {
      name: 'Имя',
      description: 'Описание',
      type: 'Тип',
      email: 'Email',
      password: 'Пароль',
      passwordRepeat: 'Повтор пароля',
      firstName: 'Имя',
      lastName: 'Фамилия',
      patronymic: 'Отчество'
    },
    search: 'Поиск',
  },
  'confirm-dialog': {
    title: 'Подтверждение'
  },
  'message': {
    title: 'Сообщение'
  },
  'select-transformer-dialog': {
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
  'change-password': {
    name: 'Изменение пароля',
    oldPassword: 'Старый пароль',
    passwordChangeSuccess: 'Пароль успешно изменен',
    passwordChangeError: 'Ошибка при изменении пароля',
  },
  'email-confirm-error': {
    name: 'Ошибка подтверждения эл. почты',
    goToUserSettings: 'Перейти в настройки пользователя'
  },
  'restore-password': {
    name: 'Восстановление пароля',
    newPasswordSentByEmail: 'Новый пароль отправлен на эл почту',
    passwordSendError: 'Ошибка при отправке нового пароля',
  },
  'signal': {
    name: 'Сигнал',
    saveAsNew: 'Сохранить как новый',
    sampleRate: 'Частота дискретизации = {sampleRate}',
    transformSignalError: 'Ошибка при преобразовании сигнала',
    signalDataSaveError: 'Ошибка при сохранении данных сигнала',
    signalSaveError: 'Ошибка при сохранении сигнала',
    signalNotFound: 'Сигнал не найден'
  },
  'signal-generator': {
    name: 'Генерация сигнала',
    generate: 'Сгенерировать',
    begin: 'Начало сигнала',
    length: 'Длительность сигнала',
    sampleRate: 'Частота дискретизации сигнала',
    frequency: 'Частота сигнала',
    amplitude: 'Амплитуда сигнала',
    offset: 'Сдвиг сигнала',
    form: 'Форма сигнала',
    forms: {
      sine: 'синусоидальный',
      square: 'прямоугольный',
      triangle: 'треугольный',
      sawtooth: 'пилообразный',
      noise: 'шумовой'
    },
    signalName: 'Сгенерированный {form} сигнал',
    description: 'Сгенерированный {form} сигнал с частотой F = {frequency} ({length} точек)',
    'import': 'Импортировать',
    fromTxtOrWavFile: 'Из файла txt или wav',
    importedFromFile: 'Импортирован из файла {name}',
    wrongPointsNumber: 'Количество точек (S * L) должно быть в диапазоне от 2 до 512000. Сейчас оно равно {pointsNumber}',
    lessThanHalfSampleRate: 'Значение должно быть не больше половины частоты дискретизации'
  },
  'signal-manager': {
    name: 'Менеджер сигналов',
    total: 'Всего: {pages} страниц, {elements} записей',
    pageSize: 'Размер страницы',
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
    confirmDeleteSignals: 'Удалить выбранные сигналы (количество: {length})?'
  },
  'signal-recorder': {
    name: 'Запись сигнала',
    sampleRateHz: 'Частота дискретизации (Гц)',
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
    signInError: 'Ошибка при входе'
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
    confirmSentCheckEmail: 'Письмо с ссылкой для подтвержения отправлено на {email}'
  },
  transformerNames: {
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
    withLinearOscillator: 'линейныйм осциллятор',
    Integrator: 'Интегратор',
    withIntegrator: 'интегратором',
    Differentiator: 'Дифференциатор',
    withDifferentiator: 'дифференциатором',
    SpectrumAnalyser: 'Анализатор спектра',
    withSpectrumAnalyser: 'анализатором спектра',
    SelfCorrelator: 'Автокоррелятор',
    withSelfCorrelator: 'автокоррелятором',
    Adder: 'Сумматор',
    withAdder: 'сумматором',
    Correlator: 'Коррелятор',
    withCorrelator: 'коррелятором',
    TwoSignalAmplitudeModulator: 'Амплитудный модулятор с двумя входными сигналами (несущий, модулирующий)',
    withTwoSignalAmplitudeModulator: 'амплитудным модулятором с двумя входными сигналами (несущий, модулирующий)'
  },
  transformerTypes: {
    amplifier: 'Усилитель',
    modulator: 'Модулятор',
    filter: 'Фильтр',
    oscillator: 'Осциллятор',
    math: 'Математический',
  },
  transformerParams: {
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
      transformed: '{name1} модулированный по амплитуде сигналом {name2} с глубиной модуляции {depth}'
    }
  },
  signalNames: {
    signal1: 'Сигнал 1',
    signal2: 'Сигнал 2',
    carrierSignal: 'Несущий сигнал',
    modulatingSignal: 'Модулирующий сигнал'
  },
  operationNames: {
    preparingSignals: 'Подготовка сигналов',
    calculating: 'Вычисление',
    calculatingMaxAbsValue: 'Расчет максимального абсолютного значения',
  },
  serverMessages: {
    SIGNAL_DOES_NOT_EXIST: 'Сигнал не нейден',
    EMAIL_ALREADY_EXISTS: 'Данный email уже зарегистрирован',
    EMAIL_ALREADY_CONFIRMED: 'Email уже подтвержден',
    WRONG_EMAIL_PASSWORD: 'Неверная пара email и пароль',
    WRONG_EMAIL: 'Email не существует или не подтвержден',
    WRONG_OLD_PASSWORD: 'Неверный старый пароль',
    TOO_LONG_FILE: 'Слишком большой файл. Максимальный размер файла {maxSize} байт.',
    TOO_MANY_SIGNALS_STORED: 'Слишком много сохраненных сигналов. Максимальное число сигналов - {maxNumber}.',
    NotEmpty: 'Поле обязательно к заполнению',
    MinLength: 'Нарушение требования минимальной длины',
    Size: 'Нарушение требований размера',
    MaxLength: 'Нарушение требования максимальной длины',
    Email: 'Должно быть в формате email'
  }
}

export default ru
