$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 发布后隐藏弹出框
	$("#publishModal").modal("hide");

	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		// 回调函数，处理返回的结果
		function (data) {
			data = $.parseJSON(data);
			// 在提示框中显示返回信息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果发帖成功，刷新页面
				if (data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);
}