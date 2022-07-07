function muteAudio() {
  console.log('mute all audios...');
  var audios = document.getElementsByTagName('audio'),
    i, len = audios.length;
  for (i = 0; i < len; i++) {
    console.log(audios[i]);
    audios[i].muted = true;
  }
}