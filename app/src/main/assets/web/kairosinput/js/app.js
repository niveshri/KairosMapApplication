var SERVER_PORT = 9010;

$(document).ready(function($) {
  var locking = false;

  document.ontouchmove = function(e) {
    if (locking) {
      e.preventDefault();
    }
  }

  // Screen setup
  $('.direction').text('Swipe here');

  var hammertime = new Hammer($('.touch-area').get(0));

  // Connect to the server
  var socket = new ReconnectingWebSocket('ws://' + window.location.hostname + ":" + SERVER_PORT + "/swipe", 'input-protocol');

  // Show our connection status to the server
  socket.onopen = function() {
    $('.server-status').find('.status').text('Connected').removeClass('error').addClass('success');
  };
  socket.onerror = function() {
    $('.server-status').find('.status').text('Not Connected').removeClass('success').addClass('error');
  };
  socket.onclose = function() {
    $('.server-status').find('.status').text('Not Connected').removeClass('success').addClass('error');
  }

  // Let Hammer.js know we want to capture all swipe directions
  hammertime.get('swipe').set({ direction: Hammer.DIRECTION_ALL });

  // Swipe Event handler
  hammertime.on('swipe  ', function(ev) {
    switch (ev.direction) {
      case Hammer.DIRECTION_LEFT:
        $('.direction').text('Left');
        socket.send('SWIPE_LEFT');
        break;
      case Hammer.DIRECTION_RIGHT:
        $('.direction').text('Right');
        socket.send('SWIPE_RIGHT');
        break;
      case Hammer.DIRECTION_UP:
        $('.direction').text('Up');
        socket.send('SWIPE_UP');
        break;
      case Hammer.DIRECTION_DOWN:
        $('.direction').text('Down');
        socket.send('SWIPE_DOWN');
        break;
      default:
        $('.direction').text('None');
        break;
    }
  });

  // Button events
  $('.button').on('click', function() {
    var action = $(this).data('action');
    socket.send('CLICK_' + action)
  });

  // Lock scrolling
  $('.lock').on('click', function() {
    if (locking) {
      locking = false;
    } else {
      locking = true;
    }
  });
});
