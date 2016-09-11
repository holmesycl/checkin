/**
 * 
 */
(function($){
	
	/* attach a submit handler to the form */
	$('#mailForm').submit(function(event) {
		/* stop form from submitting normally */
		event.preventDefault();
		var l = $('#mailBtn').ladda();
		l.ladda('start');
		/* get some values from elements on the page: */
		var $form = $(this),
			to = $form.find('input[name="to"]').val(),
			cc = $form.find('input[name="cc"]').val(),
			bcc = $form.find('input[name="bcc"]').val(),
			subject = $form.find('input[name="subject"]').val(),
			text = $form.find('textarea[name="text"]').val(),
			_csrf = $form.find('input[name="_csrf"]').val(),
			url = $form.attr('action'),
			toAll = $form.find('input[name="toAll"]')[0].checked;
		//Send the data using post and put the results in a div 
		$.post(url, {
				to: to,
				cc: cc,
				bcc: bcc,
				subject: subject,
				text: text,
				toAll: toAll,
				_csrf: _csrf
			},
			function(data) {
				l.ladda('stop');
	    	}
		);
	});
	
	$('#mailForm').find('input[name="toAll"]').change( function() {
		if(this.checked){
			$('#mailForm').find('input[name="to"]')[0].disabled = true;
		}else{
			$('#mailForm').find('input[name="to"]')[0].disabled = false;
		}
	});
	
	Date.prototype.Format = function (fmt) { //author: meizz 
	    var o = {
	        "M+": this.getMonth() + 1, //月份 
	        "d+": this.getDate(), //日 
	        "h+": this.getHours(), //小时 
	        "m+": this.getMinutes(), //分 
	        "s+": this.getSeconds(), //秒 
	        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	        "S": this.getMilliseconds() //毫秒 
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
	};
	
	var now = new Date().Format("yyyy-MM-dd");
	var url = '/checkin/logs/' + now;
	$.get(url,function(data) {
			var vals = '';
			$.each(data,function(i, e){
				vals += e + '\n';
			})
			$('#mailForm').find('textarea[name="text"]').val(vals);
    	}
	);
	
})(jQuery)