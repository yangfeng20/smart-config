<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="./static/bootstrap.min.js"></script>
	<link rel="stylesheet" href="./static/bootstrap.css">
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>配置列表</title>
	<style>
        /* 样式仅为演示目的，可根据需要自行修改 */
        .config-item {
            margin-bottom: 10px;
        }

        .edit-form {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.5);
        }

        .edit-form label {
            display: block;
            margin-bottom: 5px;
        }

        .edit-form textarea,
        .edit-form input[type="submit"] {
            width: 100%;
            margin-bottom: 10px;
        }

        .edit-form textarea {
            resize: vertical;
        }

        .success-message {
            display: none;
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background: #5cb85c;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.5);
        }
	</style>
</head>

<br>
<body>
<div class="container">
	<div style="margin-bottom: 10px">
		<a class="btn btn-success" href="#">
			<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
			添加配置
		</a>
		<a class="btn btn-success" href="#">
			<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
			发布配置
		</a>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<span class="glyphicon glyphicon-th" aria-hidden="true"></span>
			配置列表
		</div>

		<button type="button" class="btn btn-default" aria-label="Left Align">
			<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>
		</button>

		<button type="button" class="btn btn-default btn-lg">
			<span class="glyphicon glyphicon-star" aria-hidden="true"></span> Star
		</button>

		<table class="table table-striped table-hover">
			<thead>
			<tr>
				<th>配置Key</th>
				<th>值</th>
				<th>描述</th>
				<th>状态</th>
				<th>持久化</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>操作</th>
			</tr>
			</thead>

			<tbody>
			<c:forEach items="${requestScope.configList}" var="config">
			<tr>
				<td>${config.key}</td>
				<td>${config.value}</td>
				<td>${config.desc}</td>
				<td>${config.status}</td>
				<td>${config.durable}</td>
				<td>${config.createDate}</td>
				<td>${config.updateDate}</td>
				<td>
					<button onclick="editConfig('${config.key}')" class="btn btn-primary btn-xs">编辑</button>
				</td>
			</tr>
			</c:forEach>
		</table>
	</div>
</div>


<!-- 编辑表单 -->
<div id="editForm" class="edit-form">
	<form onsubmit="saveConfig(event)">
		<label>按esc退出</label>
		<br>
		<label for="key">Key:</label>
		<input type="text" id="key" readonly disabled><br><br>
		<label for="value">Value:</label><br>
		<textarea id="value" rows="4" cols="50"></textarea><br><br>
		<input type="submit" value="保存">
	</form>
</div>

<!-- 提示信息 -->
<div id="successMessage" class="success-message"></div>

<script>

    function editConfig(key) {
        // 显示编辑表单
        document.getElementById('editForm').style.display = 'block';
        // 设置Key值
        document.getElementById('key').value = key;

        // 监听 ESC 按键事件关闭表单
        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                document.getElementById('editForm').style.display = 'none';
            }
        });

    }


    // 保存配置项
    function saveConfig(event) {
        event.preventDefault(); // 阻止表单默认提交行为

        const key = document.getElementById('key').value;
        const value = document.getElementById('value').value;

        // 模拟后端保存，实际需替换为调用后端接口
        saveConfigToBackend(key, value);

    }

    // 模拟从后端获取数据的函数
    function getConfigValueFromBackend(key) {
        // 这里模拟数据源，实际中需要使用适当的方法从后端获取数据
        const configData = {
            'ConfigKey1': 'Value1',
            'ConfigKey2': 'Value2'
        };
        return configData[key] || '';
    }

    // 模拟保存数据到后端的函数
    function saveConfigToBackend(key, value, isCreate) {
        const data = `key=\${key}&value=\${value}&isCreate=\${isCreate}`;
        // 这里模拟向后端保存数据的行为，实际中需要使用适当的后端接口
        fetch('http://localhost:8080/edit', {
            method: 'POST',
            headers:{
                'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8',
                'credentials':'include'
            },
            body: data
        })
            .then(response => {
                if (response.ok) {
                    const successMessage = document.getElementById('successMessage');
                    successMessage.innerText = '保存成功';
                    successMessage.style.display = 'block';

                    setTimeout(() => {
                        location.reload()
                    }, 2000)
                } else {
                    const successMessage = document.getElementById('successMessage');
                    successMessage.innerText = '保存失败';
                    successMessage.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('保存失败:', error);
            }).finally(() => {
            // 隐藏编辑表单
            document.getElementById('editForm').style.display = 'none';

            // 自动隐藏保存成功提示
            setTimeout(() => {
                successMessage.style.display = 'none';
            }, 2000);
        });
    }

</script>

</body>
</html>