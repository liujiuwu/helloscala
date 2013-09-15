$(function () {
  $("select[multiple]").select2();

  $("#main-search .input-group").focusin(function () {
    $("#main-search .input-group").animate({"width": "400"});
  });

  $("#main-search .input-group").focusout(function () {
    $("#main-search .input-group").animate({"width": "220"});
  });

})
