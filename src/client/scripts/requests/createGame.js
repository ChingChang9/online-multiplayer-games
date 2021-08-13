document.addEventListener("DOMContentLoaded", createGameBuilder, false);

function createGameBuilder() {
	xhr.open("POST", "http://localhost:8000/game/create-builder");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && (xhr.status === 200 || xhr.status === 201)) {
            promptQuestion(JSON.parse(xhr.response).designQuestion);
		}
	}

	xhr.send(JSON.stringify({
		templateID: window.location.href.split("?templateId=")[1],
		userID: sessionStorage.getItem("userId")
	}));
}

function promptQuestion(questionText) {
    const question = document.createElement("label");
    const input = document.createElement("input");
    input.required = true;
    input.onkeypress = event => {
        if (event.keyCode === 13) makeDesignChoice();
    }
    input.setAttribute("data-id", document.getElementsByTagName("tag").length);
    question.innerHTML = questionText;
    question.appendChild(input);

    document.getElementById("form").insertBefore(question, document.getElementsByClassName("buttons")[0]);
    question.focus();
}

function makeDesignChoice() {
    const inputs = document.getElementsByTagName("input");
    const choice = inputs[inputs.length - 1].value;
    if (choice == "") return alert("The input is invalid. Please re-enter");

    xhr.open("POST", "http://localhost:8000/game/make-design-choice");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
		    inputs[inputs.length - 1].readOnly = true;
            promptQuestion(JSON.parse(xhr.response).designQuestion);
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 201) {
		    alert("Successfully created game");
		    window.location = "http://localhost:8080/pages/my-games";
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 400) {
		    alert("The input is invalid. Please re-enter");
		}
	}

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId"),
		designChoice: choice
	}));
}

function resetQuestions() {
    xhr.open("POST", "http://localhost:8000/game/cancel-builder");

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            window.location.reload();
        }
    }

    xhr.send(JSON.stringify({
        userID: sessionStorage.getItem("userId")
    }));
}
