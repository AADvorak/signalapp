<template>
  <div>
    <h1 class="desc-h1">App description</h1>
    <div class="desc-div">This web app models signals and their processing.</div>
    <div class="desc-div"><b>App abilities:</b></div>
    <ul class="desc-div desc-ul">
      <li>simple form signals generation,</li>
      <li>signal processing,</li>
      <li>signal storage on server,</li>
      <li>signal export to / import from txt and wav files,</li>
      <li>signal recording / playing.</li>
    </ul>
    <h2 class="desc-h2">Signal generation</h2>
    <div class="desc-div">Use <a href="/signal-generator">signal generator</a> to generate signals and import from txt and wav files.</div>
    <h2 class="desc-h2">Signal recording</h2>
    <div class="desc-div">Use <a href="/signal-recorder">signal recorder</a> to record signals from your audio input device.</div>
    <h2 class="desc-h2">Signal management</h2>
    <div class="desc-div">Use <a href="/signal-manager">signal manager</a> to view, edit, delete, play, process, export to txt and wav files your stored signals.</div>
    <div class="desc-div">All generated and stored signals can be opened to edit and do processing chains. Every time you have generated new signal, opened stored signal, or done some processing, the result signal is opened with the special module, where you can preview it, edit name and description, save it, export data, or do further processing.</div>
    <h2 class="desc-h2" style="color: brown">Warning</h2>
    <div class="desc-div">The app is for educational purposes only. Not for long audio files processing. Maximal length of</div>
    <ul class="desc-div desc-ul">
      <li>generated signals is 512000 samples,</li>
      <li>stored on server signals is 1024000 samples,</li>
      <li>imported wav files is 2048000 bytes.</li>
    </ul>
    <div class="desc-div">Not all browsers support playing signals. It is recommended to use Google Chrome.</div>
    <h2 class="desc-h2">Contacts</h2>
    <div class="desc-div">Author: <linkedin-link>Anton Dvorak</linkedin-link></div>
    <div class="desc-div">Source code: <github-link>SignalApp on GitHub</github-link></div>
    <div class="desc-div">Email: <email-link/></div>
    <div class="desc-div mt-4">App version: <app-version/></div>
  </div>
</template>

<script>
import EmailLink from "../components/links/email-link";
import GithubLink from "../components/links/github-link";
import LinkedinLink from "../components/links/linkedin-link";
import AppVersion from "~/components/description/app-version.vue";
export default {
  name: "description-en",
  components: {AppVersion, LinkedinLink, GithubLink, EmailLink}
}
</script>
