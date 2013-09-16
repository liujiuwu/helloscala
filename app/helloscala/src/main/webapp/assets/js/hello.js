$(function () {
  $("select[multiple]").select2();

  $("#main-search .input-group").focusin(function () {
    $("#main-search .input-group").animate({"width": "400"});
  });

  $("#main-search .input-group").focusout(function () {
    $("#main-search .input-group").animate({"width": "220"});
  });


})

function signUpPwd(controlExp, controlExp2) {
  $(controlExp + ' :password').keyup(function () {
    var pwd = $(controlExp + ' :password').val();
    if (pwd != '') $(controlExp2 ).show(1000);
  });
}

function formControlSuccess() {
  var exp = arguments[0];
  var msg = "";
  if (arguments.length > 1) msg = arguments[1];

  $(exp).removeClass('has-warning has-error').addClass('has-success');
  $(exp + ' .help-block').html(msg);
}

function formControlWarning() {
  var exp = arguments[0];
  var msg = "";
  if (arguments.length > 1) msg = arguments[1];

  $(exp).removeClass('has-success has-error').addClass('has-warning');
  $(exp + ' .help-block').html(msg);
}

function formControlError() {
  var exp = arguments[0];
  var msg = "";
  if (arguments.length > 1) msg = arguments[1];

  $(exp).removeClass('has-warning has-success').addClass('has-error');
  $(exp + ' .help-block').html(msg);
}
