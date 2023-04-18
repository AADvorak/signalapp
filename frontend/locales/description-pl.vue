<template>
  <div>
    <h1 class="desc-h1">Opis aplikacji</h1>
    <div class="desc-div">Ta aplikacja internetowa modeluje sygnały i ich przetwarzanie.</div>
    <div class="desc-div"><b>Mozliwości aplikacji:</b></div>
    <ul class="desc-div desc-ul">
      <li>generowanie sygnałów w prostym kształcie,</li>
      <li>przetwarzanie sygnałów,</li>
      <li>przechowywanie sygnałów na serwerze,</li>
      <li>eksportowanie i importowanie sygnałów do / z plików txt i wav,</li>
      <li>nagrywanie i odtwarzanie sygnałów.</li>
    </ul>
    <h2 class="desc-h2">Generowanie sygnałów</h2>
    <div class="desc-div">Użyj <a href="/signal-generator">generatora sygnałów</a>, aby wygenerować sygnały lub importować je z plików txt i wav.</div>
    <h2 class="desc-h2">Nagrywanie sygnałów</h2>
    <div class="desc-div">Użyj <a href="/signal-recorder">nagrywarki sygnałów</a>, aby nagrywać sygnały ze swojego urządzenia wejściowego audio.</div>
    <h2 class="desc-h2">Zarządzanie sygnałami</h2>
    <div class="desc-div">Użyj <a href="/signal-manager">menedżera sygnałów</a>, aby przeglądać, edytować, usuwać, odtwarzać, przetwarzać, eksportować do plików txt i wav przechowywane sygnały.</div>
    <div class="desc-div">Wszystkie wygenerowane i przechowywane sygnały można otworzyć w celu edycji i przetwarzania. Za każdym razem, gdy wygenerujesz nowy sygnał, otworzysz przechowywany sygnał lub wykonasz jakieś przetwarzanie, wynikowy sygnał jest otwierany w specjalnym module, gdzie można go zobaczyć, edytować nazwę i opis, zapisać, wyeksportować dane lub wykonać dalsze przetwarzanie.</div>
    <h2 class="desc-h2" style="color: brown">Ostrzeżenie</h2>
    <div class="desc-div">Aplikacja jest przeznaczona wyłącznie do celów edukacyjnych. Nie służy do przetwarzania plików audio dużego rozmiaru. Maksymalna długość:</div>
    <ul class="desc-div desc-ul">
      <li>wygenerowanych sygnałów wynosi 512000 próbek,</li>
      <li>przechowywanych sygnałów na serwerze wynosi 1024000 próbek,</li>
      <li>importowanych plików wav wynosi 2048000 bajtów.</li>
    </ul>
    <div class="desc-div">Nie wszystkie przeglądarki obsługują odtwarzanie sygnałów. Zaleca się korzystanie z Google Chrome.</div>
    <h2 class="desc-h2">Kontakty</h2>
    <div class="desc-div">Autor: <linkedin-link>Anton Dvorak</linkedin-link></div>
    <div class="desc-div">Kod źródłowy: <github-link>SignalApp na GitHubie</github-link></div>
    <div class="desc-div">E-mail: <email-link/></div>
  </div>
</template>

<script>
import LinkedinLink from "../components/links/linkedin-link";
import GithubLink from "../components/links/github-link";
import EmailLink from "../components/links/email-link";
export default {
  name: "description-pl",
  components: {EmailLink, GithubLink, LinkedinLink}
}
</script>
