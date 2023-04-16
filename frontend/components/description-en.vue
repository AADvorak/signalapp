<template>
  <div>
    <h1 class="desc-h1">App description</h1>
    <div class="desc-div">This web app models signals and their transformations.</div>
    <div class="desc-div"><b>App abilities:</b></div>
    <ul class="desc-div desc-ul">
      <li>simple form signals generation,</li>
      <li>signal transformation,</li>
      <li>signal storage on server,</li>
      <li>signal export to / import from txt and wav files,</li>
      <li>signal recording / playing.</li>
    </ul>
    <h2 class="desc-h2">Signal generation</h2>
    <div class="desc-div">Use <a href="/signal-generator">signal generator</a> to generate signals and import from txt and wav files.</div>
    <h2 class="desc-h2">Signal recording</h2>
    <div class="desc-div">Use <a href="/signal-recorder">signal recorder</a> to record signals from your audio input device.</div>
    <h2 class="desc-h2">Signal management</h2>
    <div class="desc-div">Use <a href="/signal-manager">signal manager</a> to see / edit / delete / play your stored signals, send them to transformers, view, export to txt and wav files, do transformations with two input signals.</div>
    <div class="desc-div">All generated and stored signals can be opened to edit and do transformation chains. Every time you have generated new signal, opened stored signal, or done some transformation, the result signal is opened with the special module, where you can preview it, edit name and description, save it, export data, or do further transformations.</div>
    <h2 class="desc-h2" style="color: brown">Warning</h2>
    <div class="desc-div">The app is for educational purposes only. Not for processing of real signals and sound. Maximal length of</div>
    <ul class="desc-div desc-ul">
      <li>generated signals is 512000 points,</li>
      <li>stored on server signals is 1024000 points,</li>
      <li>imported wav files is 2048000 bytes.</li>
    </ul>
    <div class="desc-div">Not all browsers support playing signals. It is recommended to use Google Chrome.</div>
    <h2 class="desc-h2">Contacts</h2>
    <div class="desc-div">Author: <a href="https://www.linkedin.com/in/anton-dvorak-49060099/" target="_blank">Anton Dvorak</a></div>
    <div class="desc-div">Source code: <a href="https://github.com/AADvorak/signalapp" target="_blank">SignalApp on GitHub</a></div>
    <div class="desc-div">Email: <a href="mailto:dvorakaa@icloud.com">dvorakaa@icloud.com</a></div>
  </div>
</template>

<script>
export default {
  name: "description-en"
}
</script>
