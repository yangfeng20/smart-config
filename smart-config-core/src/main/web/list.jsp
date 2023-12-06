<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Config List</title>
	<style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        button {
            padding: 5px 10px;
            cursor: pointer;
        }
	</style>
</head>
<body>
<table>
	<thead>
	<tr>
		<th>Key</th>
		<th>Value</th>
		<th>Desc</th>
		<th>Status</th>
		<th>Durable</th>
		<th>Create Date</th>
		<th>Update Date</th>
		<th>Action</th>
	</tr>
	</thead>
	<tbody>
	<!-- Replace the following rows with actual data using a server-side template engine -->
	<%--	<tr>--%>
	<%--		<td>${config.key}</td>--%>
	<%--		<td>${config.value}</td>--%>
	<%--		<td>${config.status}</td>--%>
	<%--		<td>${config.createDate}</td>--%>
	<%--		<td>${config.updateDate}</td>--%>
	<%--		<td>--%>
	<%--			<button onclick="editData('${config.key}')">Edit</button>--%>
	<%--		</td>--%>
	<%--	</tr>--%>

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
				<button onclick="editData('${config.key}')">Edit</button>
			</td>

		</tr>
	</c:forEach>

	</tbody>
</table>

<script>
    function editData(key) {
        // Assuming you have an API endpoint for updating data
        const newData = prompt('Enter the new value:');

        // Make an Ajax request to update data
        // Replace 'your-api-endpoint' with the actual API endpoint
        ${requestScope.url}
        fetch('edit', {
            method: 'POST',
            headers:{
                'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8'
            },
            body: "key=" + key + "&value=" + newData,
        })
            .then(response => response.json())
            .then(data => {
                // Handle the response, update the table or show a notification
                console.log('Data updated successfully:', data);
                location.reload();
            })
            .catch(error => {
                console.error('Error updating data:', error);
            });
    }
</script>

</body>
</html>
