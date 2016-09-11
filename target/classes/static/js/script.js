/**
 * 
 */
(function($){

	/* attach a submit handler to the form */
	$('#scriptForm').submit(function(event) {
		/* stop form from submitting normally */
		event.preventDefault();
		var l = $('#scriptBtn').ladda();
		l.ladda('start');
		/* get some values from elements on the page: */
		var $form = $(this),
			scripts = $form.find('textarea[name="scripts"]').val(),
			_csrf = $form.find('input[name="_csrf"]').val(),
			url = $form.attr('action');
		//Send the data using post and put the results in a div 
		$.post(url, {
				scripts: scripts,
				_csrf: _csrf
			},
			function(data) {
				l.ladda('stop');
	    	}
		);
	});
	
})(jQuery)