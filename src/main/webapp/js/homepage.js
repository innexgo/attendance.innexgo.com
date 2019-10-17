jQuery(document).ready(function ($) {

  (function () {

    var $menu = $('nav'),
      optionsList = '<option value="" selected>Go to..</option>';

    $menu.find('li').each(function () {
        var $this = $(this),
          $anchor = $this.children('a'),
          depth = $this.parents('ul').length - 1,
          indent = '';

        if (depth) {
          while (depth > 0) {
            indent += ' - ';
            depth--;
          }

        }
        $(".nav li").parent().addClass("bold");

        optionsList += '<option value="' + $anchor.attr('href') + '">' + indent + ' ' + $anchor.text() + '</option>';
      }).end()
      .after('<select class="selectmenu">' + optionsList + '</select>');

    $('select.selectmenu').on('change', function () {
      window.location = $(this).val();
    });

  })();

  //add some elements with animate effect
  $(".features").hover(
    function () {
      $(this).find('.icon').addClass("animated fadeInUp");
      $(this).find('a.btn').addClass("animated fadeInRight");
      $(this).find('.features_content').addClass("animated fadeInDown");
    },
    function () {
      $(this).find('.icon').removeClass("animated fadeInUp");
      $(this).find('a.btn').removeClass("animated fadeInRight");
      $(this).find('.features_content').removeClass("animated fadeInDown");
    }
  );

  $(".cta-box").hover(
    function () {
      $(this).find('.cta a').addClass("animated wiggle");
    },
    function () {
      $(this).find('.cta a').removeClass("animated wiggle");
    }
  );

  $("a[data-pretty^='prettyPhoto']").prettyPhoto({
    social_tools: false
  });
});
