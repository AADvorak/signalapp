<template>
  <div>
    <h1 class="desc-h1">Описание приложения</h1>
    <div class="desc-div">Данное веб-приложение служит для моделирования сигналов и их преобразований.</div>
    <div class="desc-div"><b>Возможности приложения:</b></div>
    <ul class="desc-div desc-ul">
      <li>генерация сигналов простой формы,</li>
      <li>преобразования сигналов,</li>
      <li>хранение сигналов на сервере,</li>
      <li>экспорт / импорт сигналов в / из txt и wav файлов,</li>
      <li>запись и воспроизведение сигналов.</li>
    </ul>
    <h2 class="desc-h2">Генерация сигналов</h2>
    <div class="desc-div">Используйте <a href="/signal-generator">генератор сигналов</a> для генерации сигналов и их импортирования из txt и wav файлов.</div>
    <h2 class="desc-h2">Запись сигналов</h2>
    <div class="desc-div">Используйте <a href="/signal-recorder">модуль для записи</a> чтобы записать звук с вашего звукового устройства.</div>
    <h2 class="desc-h2">Управление сигналами</h2>
    <div class="desc-div">Используйте <a href="/signal-manager">менеджер сигналов</a> для просмотра, редактирования, удаления, проигрывания, преобразования, экспортирования в txt и wav файлы сохраненных сигналов.</div>
    <div class="desc-div">Все сгенерированные и сохраненные сигналы могут быть отправлены для выполнения цепочек преобразований. Каждый раз когда вы генерируете новый сигнал, открываете сохраненный, или делаете очередное преобразование, сигнал открывается в специальном модуле, где возможен его предпросмотр, редактирование имени и описания, сохранение, экспорт данных, или выполнение дальнейших преобразований.</div>
    <h2 class="desc-h2" style="color: brown">Внимание</h2>
    <div class="desc-div">Приложение предназначено исключительно для образовательных целей. Оно не подойдет для обработки больших звукозаписей. Максимальная длина</div>
    <ul class="desc-div desc-ul">
      <li>сгенерированного сигнала составляет 512000 отсчетов,</li>
      <li>сохраненного на сервере сигнала составляет 1024000 отсчетов,</li>
      <li>импортированного из wav сигнала 2048000 байт.</li>
    </ul>
    <div class="desc-div">Не все браузеры поддерживают проигрывание сигналов. Рекомендуется использовать Google Chrome.</div>
    <h2 class="desc-h2">Контакты</h2>
    <div class="desc-div">Автор: <linkedin-link>Антон Дворак</linkedin-link></div>
    <div class="desc-div">Исходный код: <github-link>SignalApp на гитхабе</github-link></div>
    <div class="desc-div">Электронная почта: <email-link/></div>
    <div class="desc-div mt-4">Версия приложения: <app-version/></div>
  </div>
</template>

<script>
import LinkedinLink from "../components/links/linkedin-link";
import GithubLink from "../components/links/github-link";
import EmailLink from "../components/links/email-link";
import AppVersion from "~/components/description/app-version.vue";
export default {
  name: "description-ru",
  components: {AppVersion, EmailLink, GithubLink, LinkedinLink}
}
</script>
