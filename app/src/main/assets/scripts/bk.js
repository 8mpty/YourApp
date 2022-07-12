'use strict';
const lactRefreshInterval = 5 * 60 * 1000; // 5 mins
const initialLactDelay = 1000;

const IS_YOUTUBE = window.location.hostname.search(/(?:^|.+\.)youtube\.com/) > -1 ||
                   window.location.hostname.search(/(?:^|.+\.)youtube-nocookie\.com/) > -1;
const IS_MOBILE_YOUTUBE = window.location.hostname == 'm.youtube.com';
const IS_DESKTOP_YOUTUBE = IS_YOUTUBE && !IS_MOBILE_YOUTUBE;

const IS_ANDROID = window.navigator.userAgent.indexOf('Android') > -1;


// Page Visibility API
if(IS_ANDROID || !IS_DESKTOP_YOUTUBE) {
    Object.defineProperties(document, { 'hidden': { value: false }, 'visibilityState': { value: 'visible' } });
}

window.addEventListener('visibilitychange', e => e.stopImmediatePropagation(), true);

// _lact stuff
function waitForYoutubeLactInit(delay = initialLactDelay) {
  if (window.hasOwnProperty('_lact')) {
	window.setInterval(() => { window._lact = Date.now(); }, lactRefreshInterval);
  }
  else{
    window.setTimeout(() => waitForYoutubeLactInit(delay * 2), delay);
  }
}

waitForYoutubeLactInit();