$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// 获取发送目标和内容
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();

	// 异步发送请求
	$.post(
		// post请求url
		CONTEXT_PATH + "/letter/send",
		// 封装数据
		{"toName": toName, "content": content},
		// 收到后端数据
		function (data) {
			data = $.parseJSON(data);
			if (data.code == 0) {
				$("#hintBody").text("发送成功！");
			} else {
				$("#hintBody").text(data.msg);
			}

			$("#hintModal").modal("show");
			setTimeout(function(){
				// 隐藏提示框
				$("#hintModal").modal("hide");
				// 刷新页面
				location.reload();
			}, 2000);
		}
	);


}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}